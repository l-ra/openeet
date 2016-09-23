<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output encoding="UTF-8" 
				method="xml" 
				media-type="text/xml" 
				omit-xml-declaration="no"/>

	
	<xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
	
	
	
	<xsl:template xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" match="soap:Body">${soap_body}</xsl:template>

</xsl:stylesheet>
