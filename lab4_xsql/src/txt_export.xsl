<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!-- TXT kimenet, kódlap beállítása -->
<xsl:output encoding="UTF-8" media-type="text/text" method="txt"/>

<!-- Gyökérelemre illeszkedő sablon -->
<xsl:template match="/">
	<xsl:for-each select="page/PLACES/PLACE">
		<xsl:value-of select="NAME"/><xsl:text>&#x9;</xsl:text>
		<xsl:value-of select="PHONE"/><xsl:text>&#x9;</xsl:text>
		<xsl:value-of select="ADDRESS"/><xsl:text>&#x9;</xsl:text>
		<xsl:value-of select="PRICE"/><xsl:text>&#10;</xsl:text>
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
