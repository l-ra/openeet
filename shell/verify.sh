#!/bin/sh
DIR=$(dirname $0)
IN=$1

xmlsec1 --verify --pubkey-cert-pem $DIR/cert/01000003.pem $1