#!/bin/sh
DIR=$(dirname $0)
IN=$1
OUT=$2

xmlsec1 --sign --privkey-pem $DIR/cert/01000003.key,$DIR/cert/01000003.pem $1 > $2