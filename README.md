# Abandoned
As EET (elektronicka evidence trzeb) was canceled in Czechia - this repoitory is not maintained anymore. It will be kept as a record of history only. 

# OpenEET
Open source light implementation of EET client library (Java, C#, UNIX shell). Working client (XMLDSig, WS-Security, SOAP call) with no external dependencies in 16/25kB JAR/DLL file. Get the devel snapshot and !Try It! (Use the code to get latest fixes&features)

The project consists of following repositories (no full release yet - use prerelease snapshot or source code):
* [OpenEET Java](https://github.com/l-ra/openeet-java) ![build status](https://camo.githubusercontent.com/596e721ea9e5279dc30fcca1f1f176ae99266289/68747470733a2f2f7472617669732d63692e6f72672f6c2d72612f6f70656e6565742d6a6176612e7376673f6272616e63683d6d6173746572)
  * latest build prerelease
    * Java 7 [openeet-lite-java7-20190610-1420.jar](/l-ra/openeet/blob/master/releases/prerelease/openeet-lite-java7-20190610-1420.jar) 
* [OpenEET Net](https://github.com/l-ra/openeet-net)
  * latest prerelease build [openeet-lite-shapshot-20170303-1130.zip](releases/prerelease/openeet-lite-shapshot-20170303-1130.zip) - from now (2016-09-13) .NET mplementation will not progress much in this repo (only bug fixes and protocol changes will be refelcted) - [see fork focuussed on .NET](https://github.com/vlastikcocek/openeet/tree/master/dotnet)
  * you might be interested in [Windows XP TLS problem workaround](#windows-xp--tls11-problem)
* [OpenEET Shell](https://github.com/l-ra/openeet-shell)
* [OpenEET JS](https://github.com/l-ra/openeet-js) - PoC implementation in browser Javascript (no deps, webcrypto)
* [OpenEET Android](https://github.com/l-ra/openeet-android)
  * [Android Application OpenEET](https://play.google.com/store/apps/details?id=com.github.openeet.openeet) - unmaintained


## Java example:

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

	String response=request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3"));
	//extract FIK
	assertNotNull(response);
	assertTrue(response.contains("Potvrzeni fik="));
	//ready to print online receipt
}
```


## C# example:
```c#
public static void simpleRegistrationProcessTest(){
    //set minimal business data & certificate with key loaded from pkcs12 file
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

    String response=request.sendRequest(requestBody, "https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3);
    //extract FIK
    if (response == null) throw new ApplicationException("response is null");
    if (response.IndexOf("Potvrzeni fik=") < 0) throw new ApplicationException("FIK not found in the response");
    //ready to print online receipt
    Console.WriteLine("OK!"); //a bit brief :-) but enough
}
```

## Other opensource implementations

There are also other projects implementing EET API client (not related to OpenEET implementation in this repository):
* https://github.com/ondrejnov/eet (PHP, MIT license)
* https://github.com/novakmi/eetlite (Groovy, MIT license)
* https://github.com/mirus77/DelphiEET (Delphi, MIT license)
* https://github.com/todvora/eet-client (Java, MIT license)
* https://github.com/vlastikcocek/openeet/tree/master/dotnet - fork of this repo focussed on the .NET/C# implementation

## Windows XP & TLS1.1 problem
To interact with EET endpoint at least TLS v1.1 is needed. [Windows XP does not support TLS 1.0+](https://blogs.msdn.microsoft.com/kaushal/2011/10/02/support-for-ssltls-protocols-on-windows/). The problem canbe solved by SSL/TLS tunneling using stunnel. The tunneeling concept is described in following schema:

```
[aplikace]------http------>[stunnel]-------https/tls/1.1------->[EET Server]
```
You can use stunnel distribution tailored to EET needs available in this repo in the [stunnel-eet](stunnel-eet/win32/) folder. The proposed solution was not tested on WinXP yet. Use issue to let me know whether it works or not.  

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

The templates are used in all versions of the library. It is necessary to force git not to normalize line ends when clonning. The templates must be binary identical when checking out. Master branch contains template hash validation. In case the hash validation fails (exceptoin during soap message generation), check the files in the templates folder. To reconfigure git not to modify line ends use `git config --global core.autocrlf input` and checkout master branch.

##UNIX Shell based Proof of Concept Implementation
Shell implementation of the EET client - working out of box for the simplest case receipt.
Shell experiment available at [shell/](shell/) - it is able to send valid request to playground EET API.

Follow me [on twitter](https://twitter.com/_lra) if you want to be notified when something great happens to this repo.

If you want to support me with beer or pizza, Bitcoins are welcomed at 15wuP8HRu29pYNe4jtfiTuGXrWtzdg53Jg 



