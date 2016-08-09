# Work In Progress
Even if working well with playground of the EET system, this is still work in progress.

Any contribution is welcomed!


# Java implementation of core EET functionalities
Based on proof of concept implemented in the shell folder, Java implementation now works for generating valid signed SOAP request.

Look at tests for example ho to use the class.

There are no other dependences but java runtime (at least 1.4.2.)

List of features:

* build a sale registration based on business data
* generate PKP/BKP for receipt printing
* generate valid signed SOAP message
* send the request to the playground endpoint
* receive response containing FIK
* setup custom trust manager defaulting to included JKS with the right CA certificates


# Build 
```
git clone https://github.com/l-ra/openeet.git
cd openeet/java
./gradlew jar
```
Then find openeet/java/openeet-lite/build/libs/openeet-lite.jar and use it in your builds.


# Basic usage

```java
@Test
public void simpleRegistrationProcessTest() 
    throws MalformedURLException, IOException{

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

	String response=request.sendRequest(requestBody, 
		      new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v2"));
	//extract FIK
	assertNotNull(response);
	assertTrue(response.contains("Potvrzeni fik="));
	//ready to print online receipt
}
```

# Plans

* releas & publish maven artefact
* certificate management utilities - create cert request, get certificate and key ready to use (depends on including CA services in the playground)
* basic offline processing - mainly in the point of view of request structure manipulation (what remains unchanged when resending)
