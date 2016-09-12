#!/bin/bash

wget --method=POST \
   "--header=Content-Type: text/xml;charset=UTF-8" \
   "--header=SOAPAction: http://fs.mfcr.cz/eet/OdeslaniTrzby" \
   "--body-file=$1"\
   -q -O -\
   https://pg.eet.cz/eet/services/EETServiceSOAP/v3
