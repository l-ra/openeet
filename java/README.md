# Java implementation of core EET functionalities
Based on proof of concept implemented in the shell folder, Java implementation now works for generating valid signed SOAP request.

Look at tests for example ho to use the class.

There are no dependences but java runtime.

`
EetMessageData data=EetMessageData.builder()
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
String requestBody=data.generateSoapRequest(key);