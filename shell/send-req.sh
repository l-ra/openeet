#!/bin/bash

curl -XPOST \
   -H "Content-Type: text/xml;charset=UTF-8" \
   -H "SOAPAction: http://fs.mfcr.cz/eet/OdeslaniTrzby" \
   --data-binary @$1\
   https://pg.eet.cz/eet/services/EETServiceSOAP/v2
