# OpenEET
Open source experimenty s rozhraním pro EET.

Open source experimens with the Czech government system for Electronic Registration of Sales 

## Java implementation
Java implementation now works with EET playtground v2. No dependencies, just the java runtime itself (1.4.2). 

To register a sale it is as easy as this:

```java
@Test
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
```


##XMLDSig&SOAP&WS-Security approach for restricted devices
As XMLDsig&SOAP&WSS are huge standards, it is hard to find fuully compliant implementation on restricted devices. I decided to work around this. The intention of of this work (for now) is to provide "light" implementation of the EET API clien.
I will decsribe my approach later (not som much spare time now). For now just short intro. You ucan see working PoC in th shell implementation. The concept is based on XMLDSig processing (see spec). XML is preprocessed from the point of view of canonicalization during compile time and then message is constructed using preprocessing results (templates), some (careful) string replacements and basic crytpo primitives (SHA256, SHA1 and RSASSA_PKCS_1_5)which are widely available on most platforms.

###Templates preparation
This step take part during library build. It is bound to certain version of the spec and it is automated by calling a script. The "compile time" preprocessing of the example SOAP message produces two blobs.
* xml temmplate - template of the whole SOAP message
* digested data - template needs to be filled in with business data
* signature data - template needs to be filled in with digest computed over the first template
After the template instances with right data in are ready, signature value can be computed. 

The final step fills the computed values into xml template and the message is ready to be sent to EET API.

##UNIX Shell based Proof of Concept Implementation
Shell implementation of the EET client - working out of box for the simplest case receipt.
Shell experiment available at [shell/](shell/) - it is able to send valid request to playground EET API.

Follow me [on twitter](https://twitter.com/_lra) if you want to be notified when something great happens to this repo.

