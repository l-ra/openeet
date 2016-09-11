package openeet.lite;


import java.net.URL;
import java.security.KeyStore;
import java.util.Date;

public class Main {
	
	public static void main(String[] args) {
		try {
			EetRegisterRequest request=EetRegisterRequest.builder()
			   .dic_popl("CZ1212121218")
			   .id_provoz("1")
			   .id_pokl("POKLADNA01")
			   .porad_cis("1")
			   .dat_trzby(EetRegisterRequest.formatDate(new Date()))
			   .celk_trzba(100.0)
			   .rezim(0)
			   .pkcs12(EetRegisterRequest.loadStream(Main.class.getResourceAsStream("/openeet/lite/01000003.p12")))
			   .pkcs12password("eet")
			   .build();

			//try send
			String requestBody=request.generateSoapRequest();
			System.out.printf("===== BEGIN EET REQUEST =====\n%s\n===== END EET REQUEST =====\n",requestBody);

			String response=request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3"));
			System.out.printf("===== BEGIN EET RESPONSE =====\n%s\n===== END EET RESPONSE =====\n",response);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
