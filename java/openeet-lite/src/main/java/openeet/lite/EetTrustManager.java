package openeet.lite;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Special purpose trust manager defaulting to current EET service server certificate.
 * Throws exception when server side usage is attempted. When asked for issuerlist, returns empty list.
 * @author rasekl
 *
 */
public class EetTrustManager implements X509TrustManager {

    X509TrustManager pkixTrustManager;

    
    /**
     * Init with custom keystore
     * @param ks loaded keystore containing trusted certificates (usually root CA cert and intermediate CA cert)
     */
    public EetTrustManager(KeyStore ks) {
    	initWithKeyStore(ks);
    }
      
    /**
     * Init with keystore loaded from resources suitable for EET public interface
     */
    public EetTrustManager()  {
    	KeyStore ks=null;
    	try {
	        ks = KeyStore.getInstance("JKS");
	        ks.load(EetTrustManager.class.getResourceAsStream("/openeet/lite/eet-trust.jks"), "eeteet".toCharArray());
    	}
    	catch (Exception e){
    		throw new RuntimeException("failed to init with keystore",e);
    	}
        initWithKeyStore(ks);
    }
    
    protected void initWithKeyStore(KeyStore ks) {
    	try {
    		//x509 covers both PKIX & X509
	        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
	        tmf.init(ks);
	
	        TrustManager tms [] = tmf.getTrustManagers();
	
	        for (int i = 0; i < tms.length; i++) {
	            if (tms[i] instanceof X509TrustManager) {
	                pkixTrustManager = (X509TrustManager) tms[i];
	                return;
	            }
	        }
    	}
    	catch (Exception e){
    		throw new RuntimeException("failed to init with keystore",e);
    	}
    	throw new RuntimeException("failed to finnd suitable trust manager");
    }

    /**
     * Throws - no server side usage expected
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
    	throw new CertificateException("client auth not expected");
    }

    /**
     * delegates to trust manager initiated with supplied or default keystore
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
    	pkixTrustManager.checkServerTrusted(chain, authType);
    }

    /**
     * Returns empty list.
     */
    public X509Certificate[] getAcceptedIssuers() {
    	return new X509Certificate[]{};
    }
}