<?php

date_default_timezone_set("Europe/Prague");
$dir=dirname($_SERVER["SCRIPT_FILENAME"]);
if ( ! file_exists($dir."/work")) 
	mkdir($dir."/work");

#load & parse  "business" data from json file
$dataJson=file_get_contents($dir."/data/uctenka-data.json");
$data=json_decode($dataJson, $assoc=true);


#load templates
$requestTemplate=file_get_contents($dir."/templates/template_request.txt");
$bodyTemplate=file_get_contents($dir."/templates/template_body.txt");
$signatureTemplate=file_get_contents($dir."/templates/template_signature.txt");

#prepare dynamic data
$data["dat_odesl"]=date("c");
$data["dat_trzby"]=date("c");

#generate message uuid 
$uuidFile=$dir."/work/uuid";
system("uuid | tr -d \"\\n\"> $uuidFile");  #uid generated, extra spaces&newlines removed
$data["uuid_zpravy"]=file_get_contents($uuidFile);

#compute PKP
#format data first
$pkpInputFile=$dir."/work/pkp-input";
$pkpInput=sprintf("%s|%s|%s|%s|%s|%s", $data["dic_popl"],$data["id_provoz"],$data["id_pokl"],$data["porad_cis"],$data["dat_trzby"],$data["celk_trzba"]);
file_put_contents($pkpInputFile,$pkpInput);

#compute rsassa-pkcs1_5 signature using demo key
$pkpValueFile=$dir."/work/pkp-value";
$certFile=$dir."/cert/01000003.pem";
$keyFile=$dir."/cert/01000003.key";
$signDataCmd= "openssl sha256 -binary $pkpInputFile " #compute hash
             ."| openssl pkeyutl -sign -inkey $keyFile -pkeyopt digest:SHA256 " #apply rsa signature alg to the hash
             ."| base64 -w 0 > $pkpValueFile";  # base64 resulting raw signature 
system($signDataCmd);
$pkpValue=file_get_contents($pkpValueFile);
$data["pkp"]=$pkpValue;


#compute BKP
#shit - more than hour spent until great discovery - BKP IS CASE SENSITIVE - the hexcode must be uppercase to be accepted as valid
$bkpValueFile=$dir."/work/bkp-value";
$digestCmd= "base64 -d $pkpValueFile "    #take PKP base64 encoded value and decode back to binary
           ."| openssl sha1 -binary "    #compute SHA1 over the binary representation of the signature
           ."| xxd -p "                   #hexdump resulting hash value
           ."| tr -d \" \\n\" "           #remove spaces and newlines added by xxd
           ."| tr \"abcdef\" \"ABCDEF\""  #upercase the hex code to be recognized by EET server as valid (WTF WTF WTF)
           ."| sed -e \"s/\\(........\\)/\\1-/g\" | sed -e \"s/-\$//\" > $bkpValueFile";  #and format the hexdump of hash according to spec (WHY ? who knows!)
system($digestCmd);
$bkpValue=file_get_contents($bkpValueFile);
#for debug
#$bkpValue=file_get_contents($dir."/data/example-bkp");
#end debug shit
$data["bkp"]=$bkpValue;





#compute digest from canonicalized data of the Body element based on template extracted enriched with business data
#replace the placeholders with real business data and stpore to file  
$digestFinal=$bodyTemplate;
#fill in data in the digest first
foreach ($data as $key => $value) {
	$digestFinal=str_replace("\${".$key."}",$value,$digestFinal);
  $digestFinal=str_replace("@{".$key."}","$key=\"$value\"",$digestFinal);
}
#remove unused fields
#$digestFinal=preg_replace("/ [a-z_0-9]+=\"\\$\\{[0-9_a-z]+\\}\"/","",$digestFinal);
$digestFinal=preg_replace("/\\$\\{[a-z_0-9:]+\\}/","",$digestFinal);
$digestFinal=preg_replace("/ @\\{[a-z_0-9:]+\\}/","",$digestFinal);

#$digestFinal=preg_replace("/ @/","BUBUBUBU",$digestFinal);

$digestDataFile=$dir."/work/digest-data";
$digestValueFile=$dir."/work/digest-value";
file_put_contents($digestDataFile, $digestFinal);
$data["soap_body"]=$digestFinal;

#compute digest over the enriched data
$digestCmd= "openssl sha256 -binary $digestDataFile "  #compute hash
           ."| base64  "                               #apply base64 according to XMLDSig
           ."| tr -d \" \\n\" > $digestValueFile";     #fix extra spaces & newlines
system($digestCmd);
$digestValue=file_get_contents($digestValueFile);
$data["digest"]=$digestValue; #add digest to data - it is used in the next replacement step


#compute signature value from canonicalized siginfo enriched with digest value 
#replace placeholders - in fact the only placeholder in signaturte template needs to be replaced - ${digest}
$signatureFinal=$signatureTemplate;
foreach ($data as $key => $value) {
	$signatureFinal=str_replace("\${".$key."}",$value,$signatureFinal);
  $signatureFinal=str_replace("@{".$key."}","$key=\"$value\"",$signatureFinal);
}
#remove unused fields
#$signatureFinal=preg_replace("/ [a-z_0-9]+=\"\\$\\{[0-9_a-z]+\\}\"/","",$signatureFinal);
$signatureFinal=preg_replace("/\\$\\{[a-z_0-9]+\\}/","",$signatureFinal);
$signatureFinal=preg_replace("/ @\\{[a-z_0-9]+\\}/","",$signatureFinal);


$signatureDataFile=$dir."/work/signature-data";
file_put_contents($signatureDataFile, $signatureFinal);
#compute rsassa-pkcs1_5 over the data 
$signatureValueFile=$dir."/work/signature-value";
$signSigCmd= "openssl sha256 -binary $signatureDataFile "    #compute hash
            ."| openssl pkeyutl -sign -inkey $keyFile -pkeyopt digest:SHA256 "  #compute sig of the hash
            ."| base64 -w 0 > $signatureValueFile"; #apply base64 according to XMLDSig
system($signSigCmd);
$signatureValue=file_get_contents($signatureValueFile);
$data["signature"]=$signatureValue;

#complete XML with all the values computes
$xmlFinal=$requestTemplate;
foreach ($data as $key => $value) {
	$xmlFinal=str_replace("\${".$key."}",$value,$xmlFinal);
  $xmlFinal=str_replace("@{".$key."}","$key=\"$value\"",$xmlFinal);
}
#remove unused fields
#$xmlFinal=preg_replace("/ [a-z_0-9]+=\"\\$\\{[0-9_a-z]+\\}\"/","",$xmlFinal);
$xmlFinal=preg_replace("/\\$\\{[a-z_0-9]+\\}/","",$xmlFinal);
$xmlFinal=preg_replace("/@\\{[a-z_0-9]+\\}/","",$xmlFinal);

$signedMessageFile=$dir."/work/signed-message";
file_put_contents($signedMessageFile, $xmlFinal);

#selfcheck - to be sure we had templates right and didn't messed anything
#the verification produces binary snapshot of digested and signed data
#work/digest-data-verify must be binary equal to work/digest-data
#work/signature-data-verify must be binary equal to work/signature-data
#if not - difference must be eliminated by changing inputs/process 
#during development the only lline end in uuid escaped - now it is fixed and working well
$xmlsecCmd="xmlsec1 --verify --store-references --store-signatures --pubkey-cert-pem $certFile $signedMessageFile | php $dir/extract-c14n-templates.php $dir/work/digest-data-verify $dir/work/signature-data-verify";
system($xmlsecCmd);


?>