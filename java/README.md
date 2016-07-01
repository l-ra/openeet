# Work In Progress
Even if working well with playground of the EET system, this is still work in progress.

Any cotribuution is welcomed!


# Java implementation of core EET functionalities
Based on proof of concept implemented in the shell folder, Java implementation now works for generating valid signed SOAP request.

Look at tests for example ho to use the class.

There are no other dependences but java runtime (at least 1.4.2.)


# Build 
```
git clone https://github.com/l-ra/openeet.git
cd openeet/java
./gradlew jar
```
Then find openeet/java/openeet-lite/build/libs/openeet-lite.jar and use it in your builds.


# Basic usage

```
EetMessageData request=EetMessageData.builder()
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
String requestBody=request.generateSoapRequest(key);
String response=data.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v2"));
```

For key&certificate manipulation see the tests source code. There you can find a code for key&certificate loading from PKCS12 file.

# Plans

* releas & publish maven artefact
* certificate management utilities - create cert request, get certificate and key ready to use (depends on including CA services in the playground)
* basic offline processing - mainly in the point of view of request structure manipulation (what remains unchanged when resending)
