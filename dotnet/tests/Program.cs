using System;
using System.Collections.Generic;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.IO;
using System.Text;
using System.Text.RegularExpressions;
using openeet_lite;

namespace tests
{
    class Program
    {

        static void formatString()
        {
            DateTime dt = DateTime.Now;
            string d = dt.ToString("yyyy-MM-dd'T'HH:mm:sszzz");
            DateTime dp = DateTime.Parse(d);
            string d1 = dt.ToString("yyyy-MM-dd'T'HH:mm:sszzz");
            Console.WriteLine(d);
            Console.WriteLine(d1);
        }

        static RSACryptoServiceProvider key;
        static X509Certificate2 certificate;

        static void loadP12(byte[] p12data, string password)
        {
            X509Certificate2Collection col = new X509Certificate2Collection();
            col.Import(p12data, password, X509KeyStorageFlags.Exportable & X509KeyStorageFlags.DefaultKeySet);
            foreach (X509Certificate2 cert in col)
            {
                if (cert.HasPrivateKey)
                {
                    certificate = cert;
                    key = (RSACryptoServiceProvider)cert.PrivateKey;
                }
            }

            if (key == null || certificate == null) throw new ArgumentException("key and/or certificate still missing after p12 processing");
        }

        static void sign()
        {
            HashAlgorithm hashAlg = new SHA256CryptoServiceProvider();

            byte[] data = UTF8Encoding.UTF8.GetBytes("aaa");
            byte[] hash = hashAlg.ComputeHash(data);

            RSAPKCS1SignatureFormatter fmt = new RSAPKCS1SignatureFormatter();
            fmt.SetKey(key);
            fmt.SetHashAlgorithm("SHA256");

            byte[] sig = key.Encrypt(hash, false);

        }

        public static String formatBkp(byte[] _bkp)
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < _bkp.Length; i++)
            {
                sb.Append(String.Format("{0:X2}", _bkp[i]));
            }
            Regex re = new Regex("^([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})$");

            return re.Replace(sb.ToString().ToUpper(), @"$1-$2-$3-$4-$5"); ;
        }

        public static byte[] parseBkp(String val)
        {
            byte[] _bkp = new byte[20];
            val = val.Replace("-", "");
            Regex re = new Regex("^[A-F0-9]{40}$");

            if (val.Length != 40)
                throw new ArgumentException("Wrong length (~=!=40) of bkp string after dash removal:" + val);
            if (re.Matches(val.ToUpper()).Count==0)
                throw new ArgumentException("Wrong format, hexdump expected:" + val);

            for (int i = 0; i < 20; i++)
            {
                _bkp[i] = (byte)Convert.ToUInt32(val.Substring(i * 2, 2), 16);
            }
            return _bkp;
        }

        static void testFormatBkp()
        {
            byte[] a = new byte[20];
            for (int i = 0; i < 20; i++) a[i] = (byte)i;
            String bkp = formatBkp(new SHA1CryptoServiceProvider().ComputeHash(a));
            Console.WriteLine(bkp);
            byte[] b = parseBkp(bkp);
            Console.WriteLine(formatBkp(b));
        }

        
	    public void signAndSend() {
		    EetRegisterRequest data=EetRegisterRequest.builder()
		       .dic_popl("CZ1212121218")
		       .id_provoz("1")
		       .id_pokl("POKLADNA01")
		       .porad_cis("1")
		       .dat_trzby("2016-06-30T08:43:28+02:00")
		       .celk_trzba(100.0)
		       .pkcs12(TestData._01000003)
		       .pkcs12password("eet")
               .rezim(0)
               .build();
            if (data == null) 
                throw new Exception("failed - data null") ;
		    String pkp=EetRegisterRequest.formatPkp(data.pkp);
		    String bkp=EetRegisterRequest.formatBkp(data.bkp);
            String expectedPkp="Ddk2WTYu8nzpQscH7t9n8cBsGq4k/ggCwdfkPjM+gHUHPL8P7qmnWofzeW2pAekSSmOClBjF141yN+683g0aXh6VvxY4frBjYhy4XB506LDykIW0oAv086VH7mR0utA8zGd7mCI55p3qv1M/oog/2yG0DefD5mtHIiBG7/n7jgWbROTatJPQYeQWEXEoOJh9/gAq2kuiK3TOYeGeHwOyFjM2Cy3UVal8E3LwafP49kmGOWjHG+cco0CRXxOD3b8y4mgBqTwwC4V8e85917e5sVsaEf3t0hwPkag+WM1LIRzW+QwkkgiMEwoIqCAkhoF1eq/VcsML2ZcrLGejAeAixw==";
            String expectedBkp="AC502107-1781EEE4-ECFD152F-2ED08CBA-E6226199";
            if (!pkp.Equals(expectedPkp))
                throw new Exception("failed - PKP differs");
            if (!bkp.Equals(expectedBkp))
                throw new Exception("failed - BKP differs");

		    //String signed=data.generateSoapRequest();
		    //assertTrue(validateXmlDSig(signed, data.getCertificate()));
		    //data.sendRequest(signed, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v2"));
	    }

        static void Main(string[] args)
        {


            Console.ReadKey();
        }
    }
}

/*
    <Data dic_popl="CZ1212121218" id_provoz="1" id_pokl="POKLADNA01" porad_cis="1" dat_trzby="2016-06-30T08:43:28+02:00" celk_trzba="100.00" rezim="0"/>
    <pkp cipher="RSA2048" digest="SHA256" encoding="base64">Ddk2WTYu8nzpQscH7t9n8cBsGq4k/ggCwdfkPjM+gHUHPL8P7qmnWofzeW2pAekSSmOClBjF141yN+683g0aXh6VvxY4frBjYhy4XB506LDykIW0oAv086VH7mR0utA8zGd7mCI55p3qv1M/oog/2yG0DefD5mtHIiBG7/n7jgWbROTatJPQYeQWEXEoOJh9/gAq2kuiK3TOYeGeHwOyFjM2Cy3UVal8E3LwafP49kmGOWjHG+cco0CRXxOD3b8y4mgBqTwwC4V8e85917e5sVsaEf3t0hwPkag+WM1LIRzW+QwkkgiMEwoIqCAkhoF1eq/VcsML2ZcrLGejAeAixw==</pkp>
    <bkp digest="SHA1" encoding="base16">AC502107-1781EEE4-ECFD152F-2ED08CBA-E6226199</bkp>
    */

/*
public void simpleRegistrationProcessTest() throws MalformedURLException, IOException{
	    //set minimal business data & certificate with key loaded from pkcs12 file
		EetRegisterRequest request=EetRegisterRequest.builder()
		   .dic_popl("CZ1212121218")
		   .id_provoz("1")
		   .id_pokl("POKLADNA01")
		   .porad_cis("1")
		   .dat_trzby("2016-06-30T08:43:28+02:00")
		   .celk_trzba(100.0)
		   .rezim(0)
		   .pkcs12(loadStream(getClass().getResourceAsStream("/01000003.p12")))
		   .pkcs12password("eet")
		   .build();

		//for receipt printing in online mode
		String bkp=request.formatBkp();
		assertNotNull(bkp);

		//for receipt printing in offline mode
		String pkp=request.formatPkp();
		assertNotNull(pkp);
		//the receipt can be now stored for offline processing

		//try send
		String requestBody=request.generateSoapRequest();
		assertNotNull(requestBody);

		String response=request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v2"));
		//extract FIK
		assertNotNull(response);
		assertTrue(response.contains("Potvrzeni fik="));
		//ready to print online receipt
	}


	private boolean validateXmlDSig(String signed, X509Certificate cert){
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
			
			
			return coreValidity;
		}
		catch (Exception e){
			throw new IllegalArgumentException("validation failes", e);
		}
	}
  */
