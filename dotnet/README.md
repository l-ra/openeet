# Forked for .NET
[This repo was forked and you can find more .NET foccused repo](https://github.com/vlastikcocek/openeet/tree/master/dotnet)
I this repo I will fix reported bugs and I will try to keep the basic code working in case of protocol change. No functionality will be added at the moment (I may change mind in the future :-)).

# Work In Progress
Even if working well with v3 of the playground of the EET system, this is still work in progress.

# C# implementation of core EET functionalities
Based on Java implementation now works for generating valid signed SOAP request.

!!WARNING!! I am not C# native, the code could be ugly it can even can harm your c#ish eyes.
But still... the code works for me. 

Look at tests project for example how to use the main class EetRegisterRequest.

There are no other dependences but .NET framework (built on 4.5, I see no restriction 
to compile with any lower version - sha256 support is required).

List of features:

* build a sale registration based on business data
* generate PKP/BKP for receipt printing
* generate valid signed SOAP message
* send the request to the playground endpoint
* receive response containing FIK


# Build 
Open & build solution in dotnet\openeet-lite\openeet-lite.sln then find dotnet\openeet-lite\bin\Release\openeet-lite.dll and use it in your builds or use the code&project directly.


# Basic usage

```c#
public static void simpleRegistrationProcessTest(){
    //set minimal business data & certificate with key loaded from pkcs12 file in the resources
    EetRegisterRequest request=EetRegisterRequest.builder()
       .dic_popl("CZ1212121218")
       .id_provoz("1")
       .id_pokl("POKLADNA01")
       .porad_cis("1")
       .dat_trzby("2016-06-30T08:43:28+02:00")
       .celk_trzba(100.0)
       .rezim(0)
       .pkcs12(TestData._01000003)
       .pkcs12password("eet")
       .build();

    //for receipt printing in online mode
    String bkp=request.formatBkp();
    if (bkp == null) throw new ApplicationException("BKP is null");

    //for receipt printing in offline mode
    String pkp=request.formatPkp();
    if (pkp == null) throw new ApplicationException("PKP is null");
    //the receipt can be now stored for offline processing

    //try send
    String requestBody=request.generateSoapRequest();
    if (requestBody == null) throw new ApplicationException("SOAP request is null");

    String response=request.sendRequest(requestBody, "https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3");
    //extract FIK
    if (response == null) throw new ApplicationException("response is null");
    if (response.IndexOf("Potvrzeni fik=") < 0) throw new ApplicationException("FIK not found in the response");
    //ready to print online receipt
    Console.WriteLine("OK!"); //a bit brief :-) but enough
}
```
