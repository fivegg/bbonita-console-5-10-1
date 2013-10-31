<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
	<xsl:output method = "xml" encoding = "UTF-8" standalone = "no" indent = "yes"/>
	<xsl:template match = "/">
	  	<xsl:element name = "forms-definition" >
	    	<xsl:attribute name = "product-version" ><xsl:value-of select = "forms-definition/@product-version"/></xsl:attribute> 
	    	<xsl:copy-of select = "forms-definition/welcome-page" />
	    	<xsl:copy-of select = "forms-definition/external-welcome-page" />
	    	<xsl:copy-of select = "forms-definition/home-page" />
	    	<xsl:element name = "migration-product-version" >5.10</xsl:element>
	    	<xsl:copy-of select = "forms-definition/application" />
	    </xsl:element>
	</xsl:template>
</xsl:stylesheet>
