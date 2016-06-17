<?php

$dump = file_get_contents("php://stdin");


$dumpLen=strlen($dump);

#find digested data
$idxPredigestStartBuffer=strpos($dump, "== PreDigest data - start buffer:"); #find label 
$idxDigestedDataStart=strpos($dump,"<",$idxPredigestStartBuffer); #skip line end
$idxPredigestedEndBuffer=strpos($dump,"== PreDigest data - end buffer",$idxDigestedDataStart);
$idxDigestedDataEnd=strrpos($dump, ">", -($dumpLen-$idxPredigestedEndBuffer));


#find signature data
$idxPresignedStartBuffer=strpos($dump, "== PreSigned data - start buffer:"); #find label 
$idxPresignedDataStart=strpos($dump,"<",$idxPresignedStartBuffer); #skip line end
$idxPresignedEndBuffer=strpos($dump,"== PreSigned data - end buffer",$idxPresignedDataStart);
$idxPresignedDataEnd=strrpos($dump, ">", -($dumpLen-$idxPresignedEndBuffer));


$digestTemplate=substr($dump,$idxDigestedDataStart,$idxDigestedDataEnd-$idxDigestedDataStart+1);
$f=fopen($argv[1],"w");
fwrite($f,$digestTemplate);
fclose($f);

#remove generated digest value from signatrure data
$signatureTemplate=substr($dump,$idxPresignedDataStart, $idxPresignedDataEnd-$idxPresignedDataStart+1);
$pattern="@<ds:DigestValue>[^<]+</ds:DigestValue>@";
$replacement="<ds:DigestValue>\${digest}</ds:DigestValue>";
$signatureTemplate=preg_replace($pattern, $replacement, $signatureTemplate);

$f=fopen($argv[2],"w");
fwrite($f,$signatureTemplate);
fclose($f);

?>