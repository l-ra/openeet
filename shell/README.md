#Shell implementation

This is a shell implementation of sending registered sale to testing endpoint (for now). The main intention 
is to bring possibility to register sale using the most basic tools to help other developers implementing 
usable products for tax payers obliged to register sales.


##Signing&sending soap request
Every request sent to API must be signed according to WS-Security. WS-Security standard does not use stright 
XMLDSig implementation. It brings in a level of indirection. This tweak complicates development on restricted 
platforms withouth full fledged WS-Security implementation. Shell implementation uses combines templating to 
create SOAP message with open source XMLDSign implementation comming in XMLSEC1 package.

The xmlsec1 functionality is available in the form of library and command line tool xmlsec1. Command line tool 
is used in this case. 

The API accepts (for now) only signatures which uses #id reference to soap:Body. Unfortunately when xmlsec1 tool is 
used directly on prefabricated SOAP message as provided by GFR, signing fails due to XML lacking standard definition 
of ID attribute. xmlsec1 documentation suggests several workarounds. Current implementation uses xml:id extension 
to XML standard. Adding xml:id attribute with the same value as wsu:Id solves the issue. 
During signing/validation using xmlsec1 xml:id is used. API internaly (presumably) uses wsu:Id but the result is (fortunately)
the same.   

##How to use it
To prepare message for signing take data/template.xml and change anything inside element <Trzby> 
save it to sale-to-register-soap.xml.

To sign prefabricated SOAP message use script:

`sign.sh sale-to-register-soap.xml signed-soap.xml`

To send signed message to testing API:

`send-req.sh signed-soap.xml`

After sending you shoul see API response on the output containing FIK.

The scripts hard code certificates/keys used for signing for now. The original message and keys/certificate 
are published on http://etrzby.cz