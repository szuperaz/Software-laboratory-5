<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!-- XML kimenet, kódlap beállítása -->
<xsl:output encoding="UTF-8" media-type="application/xml" method="xml"/>

<!-- Gyökérelemre illeszkedő sablon -->
<xsl:template match="/">
	<szorakozohelyek>
		<xsl:for-each select="page/PLACES/PLACE">
			<szorakozohely>
				<nev><xsl:value-of select="NAME"/></nev>
				<tel><xsl:value-of select="PHONE"/></tel>
				<cim><xsl:value-of select="ADDRESS"/></cim>
				<arfekves><xsl:value-of select="PRICE"/></arfekves>
			</szorakozohely>
		</xsl:for-each>
	</szorakozohelyek>
</xsl:template>

</xsl:stylesheet>