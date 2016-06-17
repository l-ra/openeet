#!/bin/sh
DIR=$(dirname $0)


[ -d $DIR/templates ] || mkdir $DIR/templates
xmlsec1 --sign --store-references --store-signatures \
        --privkey-pem $DIR/cert/01000003.key,$DIR/cert/01000003.pem \
        --output /dev/null $DIR/data/template.xml \
        | php extract-c14n-templates.php $DIR/templates/digest-template $DIR/templates/signature-template 

cp $DIR/data/template.xml $DIR/templates/template.xml