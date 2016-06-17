<?php

date_default_timezone_set("Europe/Prague");
$dir=dirname($_SERVER["SCRIPT_FILENAME"]);
if ( ! file_exists($dir."/work")) 
	mkdir($dir."/work");

#load & parse  "business" data from json file
$dataJson=file_get_contents($dir."/data/uctenka-data.json");
$data=json_decode($dataJson, $assoc=true);


#load templates
$xmlTemplate=file_get_contents($dir."/data/template.xml");
$digestTemplate=file_get_contents($dir."/templates/digest-template");
$signatureTemplate=file_get_contents($dir."/templates/signature-template");

#prepare dynamic data
$data["dat_odesl"]=date("c");
$data["dat_trzby"]=date("c");

#generate message uuid
$uuidFile=$dir."/work/uuid";
system("uuid | tr -d \"\\n\"> $uuidFile");
$data["uuid_zpravy"]=file_get_contents($uuidFile);

#compute PKP
#format data first
$pkpInputFile=$dir."/work/pkp-input";
$pkpInput=sprintf("%s|%s|%s|%s|%s|%s", $data["dic_popl"],$data["id_provoz"],$data["id_pokl"],$data["porad_cis"],$data["dat_trzby"],$data["celk_trzba"]);
file_put_contents($pkpInputFile,$pkpInput);

#compute rsassa-pkcs1_5 signature using demo key
$pkpValueFile=$dir."/work/pkp-value";
$pkpBinaryValueFile=$dir."/work/pkp-value-bin";
$pkpBinaryValueCheckFile=$dir."/work/pkp-value-bin-check";
$certFile=$dir."/cert/01000003.pem";
$keyFile=$dir."/cert/01000003.key";
$signDataCmd="openssl sha256 -binary $pkpInputFile | openssl pkeyutl -sign -pkeyopt digest:SHA256 -inkey $keyFile | tee $pkpBinaryValueFile | base64 -w 0 > $pkpValueFile";
system($signDataCmd);
$pkpValue=file_get_contents($pkpValueFile);
$data["pkp"]=$pkpValue;


#compute BKP
$bkpValueFile=$dir."/work/bkp-value";
$digestCmd="base64 -d $pkpValueFile | tee $pkpBinaryValueCheckFile | openssl sha1 -binary | xxd -p | tr -d \" \\n\" | tr \"abcdef\" \"ABCDEF\"  |  sed -e \"s/\\(........\\)/\\1-/g\" | sed -e \"s/-\$//\" > $bkpValueFile";
system($digestCmd);
$bkpValue=file_get_contents($bkpValueFile);
#for debug
#$bkpValue=file_get_contents($dir."/data/example-bkp");
#end debug shit
$data["bkp"]=$bkpValue;





#compute digest from canonicalized data of the Body element based on template extracted enriched with business data
$digestFinal=$digestTemplate;
#fill in data in the digest first
foreach ($data as $key => $value) {
	$digestFinal=str_replace("\${".$key."}",$value,$digestFinal);
}
$digestDataFile=$dir."/work/digest-data";
$digestValueFile=$dir."/work/digest-value";
file_put_contents($digestDataFile, $digestFinal);
$digestCmd="openssl sha256 -binary $digestDataFile | base64  | tr -d \" \\n\" > $digestValueFile";
system($digestCmd);
$digestValue=file_get_contents($digestValueFile);
$data["digest"]=$digestValue;






#compute signature value from canonicalized siginfo enriched with digest value 
$signatureFinal=$signatureTemplate;
foreach ($data as $key => $value) {
	$signatureFinal=str_replace("\${".$key."}",$value,$signatureFinal);
}
$signatureDataFile=$dir."/work/signature-data";
file_put_contents($signatureDataFile, $signatureFinal);
$signatureValueFile=$dir."/work/signature-value";
$signSigCmd="openssl sha256 -binary $signatureDataFile | openssl pkeyutl -sign -pkeyopt digest:SHA256 -inkey $keyFile | base64 -w 0 > $signatureValueFile";
system($signSigCmd);
$signatureValue=file_get_contents($signatureValueFile);
$data["signature"]=$signatureValue;

#complete XML with all the values computes
$xmlFinal=$xmlTemplate;
foreach ($data as $key => $value) {
	$xmlFinal=str_replace("\${".$key."}",$value,$xmlFinal);
}
$signedMessageFile=$dir."/work/signed-message";
file_put_contents($signedMessageFile, $xmlFinal);

#selfcheck
$xmlsecCmd="xmlsec1 --verify --store-references --store-signatures --pubkey-cert-pem $certFile $signedMessageFile | php $dir/extract-c14n-templates.php $dir/work/digest-data-verify $dir/work/signature-data-verify";
system($xmlsecCmd);





?>