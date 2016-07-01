package openeet.lite.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import java.util.Iterator;

import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import openeet.lite.EetRegisterRequest;

public class EetMessageDataTest {

	static PrivateKey key;
	static X509Certificate cert;
	
	private static byte[] loadStream(InputStream in) throws IOException{
		byte[] buf=new byte[1024];
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		int n=0;
		while ((n=in.read(buf))>0) bos.write(buf,0,n);
		return bos.toByteArray();
	}
	
	private static void loadCert() throws IOException, CertificateException{
		CertificateFactory cf=CertificateFactory.getInstance("X509");
		cert=(X509Certificate)cf.generateCertificate(EetMessageDataTest.class.getResourceAsStream("/01000003.pem"));
	}
	
	//pkcs1->pkcs8 openssl pkcs8 -topk8 -inform PEM -outform DER -in mykey.pem -out mykey.der -nocrypt
	private static void loadKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
		KeyFactory kf=KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec ks=new PKCS8EncodedKeySpec(loadStream(EetMessageDataTest.class.getResourceAsStream("/01000003.der")));
		key=kf.generatePrivate(ks);
	}

	private static void loadP12() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		char[] password="eet".toCharArray();
		KeyStore ks=KeyStore.getInstance("PKCS12");
		ks.load(EetMessageDataTest.class.getResourceAsStream("/01000003.p12"), password);
		Enumeration<String> aliases= ks.aliases();
		while(aliases.hasMoreElements() ){
			String alias=aliases.nextElement();
			Key _key=ks.getKey(alias, password);
			if (_key!=null){
				Certificate _cert=ks.getCertificate(alias);
				if (_cert!=null){
					if (   _cert instanceof X509Certificate 
						&& _key instanceof RSAPrivateKey ){
						key=(PrivateKey) _key;
						cert=(X509Certificate)_cert;
					}
				}
			}
			
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//loadKey();
		//loadCert();
		loadP12();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/*
    <Data dic_popl="CZ1212121218" id_provoz="1" id_pokl="POKLADNA01" porad_cis="1" dat_trzby="2016-06-30T08:43:28+02:00" celk_trzba="100.00" rezim="0"/>
    <pkp cipher="RSA2048" digest="SHA256" encoding="base64">Ddk2WTYu8nzpQscH7t9n8cBsGq4k/ggCwdfkPjM+gHUHPL8P7qmnWofzeW2pAekSSmOClBjF141yN+683g0aXh6VvxY4frBjYhy4XB506LDykIW0oAv086VH7mR0utA8zGd7mCI55p3qv1M/oog/2yG0DefD5mtHIiBG7/n7jgWbROTatJPQYeQWEXEoOJh9/gAq2kuiK3TOYeGeHwOyFjM2Cy3UVal8E3LwafP49kmGOWjHG+cco0CRXxOD3b8y4mgBqTwwC4V8e85917e5sVsaEf3t0hwPkag+WM1LIRzW+QwkkgiMEwoIqCAkhoF1eq/VcsML2ZcrLGejAeAixw==</pkp>
    <bkp digest="SHA1" encoding="base16">AC502107-1781EEE4-ECFD152F-2ED08CBA-E6226199</bkp>
    */
	@Test
	public void signAndSend() throws Exception {
		EetRegisterRequest data=EetRegisterRequest.builder()
		   .dic_popl("CZ1212121218")
		   .id_provoz("1")
		   .id_pokl("POKLADNA01")
		   .porad_cis("1")
		   .dat_trzby("2016-06-30T08:43:28+02:00")
		   .celk_trzba(100.0)
		   .rezim(0)
		   .certificate(cert)
		   .key(key)
		   .build();
		assertNotNull(data);
		String pkp=EetRegisterRequest.formatPkp(data.getPkp());
		String bkp=EetRegisterRequest.formatBkp(data.getBkp());
		assertEquals(pkp,"Ddk2WTYu8nzpQscH7t9n8cBsGq4k/ggCwdfkPjM+gHUHPL8P7qmnWofzeW2pAekSSmOClBjF141yN+683g0aXh6VvxY4frBjYhy4XB506LDykIW0oAv086VH7mR0utA8zGd7mCI55p3qv1M/oog/2yG0DefD5mtHIiBG7/n7jgWbROTatJPQYeQWEXEoOJh9/gAq2kuiK3TOYeGeHwOyFjM2Cy3UVal8E3LwafP49kmGOWjHG+cco0CRXxOD3b8y4mgBqTwwC4V8e85917e5sVsaEf3t0hwPkag+WM1LIRzW+QwkkgiMEwoIqCAkhoF1eq/VcsML2ZcrLGejAeAixw==");
		assertEquals("AC502107-1781EEE4-ECFD152F-2ED08CBA-E6226199",bkp);
		String signed=data.generateSoapRequest(key);
		assertTrue(validateXmlDSig(signed));
		data.sendRequest(signed, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v2"));
	}


	/**
	 * Utility function to validate XML Signature to do a self check
	 * @param signed request 
	 * @return
	 */
	private boolean validateXmlDSig(String signed){
		try {
			DocumentBuilderFactory dbf = 
					  DocumentBuilderFactory.newInstance(); 
			dbf.setNamespaceAware(true);

			DocumentBuilder builder = dbf.newDocumentBuilder();  
			Document doc = builder.parse(new ByteArrayInputStream(signed.getBytes("utf-8")));
			NodeList signatureNodeList = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			NodeList bodyNodeList = doc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body");
			
			if (signatureNodeList.getLength() == 0) {
			  throw new Exception("Cannot find Signature element");
			}
			DOMValidateContext valContext = new DOMValidateContext(cert.getPublicKey(), signatureNodeList.item(0));
			valContext.setIdAttributeNS((Element)bodyNodeList.item(0),"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd","Id");
			
			XMLSignatureFactory factory = 
					  XMLSignatureFactory.getInstance("DOM");
			XMLSignature signature = 
					  factory.unmarshalXMLSignature(valContext);
			boolean coreValidity = signature.validate(valContext); 
			
			/* 
			//detailed validation - use when solving validity problems
			boolean sv = signature.getSignatureValue().validate(valContext);
			Iterator<Reference> i = signature.getSignedInfo().getReferences().iterator();
			for (int j=0; i.hasNext(); j++) {
			  boolean refValid = ( i.next()).validate(valContext);
			} 
			*/
			
			return coreValidity;
		}
		catch (Exception e){
			throw new IllegalArgumentException("validation failes", e);
		}
	}
	
	
	

}
