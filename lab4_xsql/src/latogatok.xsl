<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:import href="footer.xsl"/>
<!-- HTML kimenet, kódlap beállítása -->
<xsl:output encoding="UTF-8" media-type="text/html" method="html"/>
<!-- Gyökérelemre illeszkedő sablon -->
<xsl:template match="/">
	<html>
		<head>
			<link rel="stylesheet" href="style.css"/>
			<title>Látogatók</title>
		</head>
		<body>
		<div class="container col-xs-6 col-xs-offset-3">
			<xsl:choose>
				<!-- Hiba esetén -->
				<xsl:when test="//xsql-error">
					<p>Hiba történt:
						<xsl:value-of select="//xsql-error/message[1]"/>
					</p>
				</xsl:when>
				<!-- Ha nem történt hiba -->
				<xsl:otherwise>
					<div class="page-header">
						<h1><xsl:value-of select="page/ROWSET/ROW[1]/PLACE"/> látogatói</h1>
					</div>
					<div>
						<ul class="list-group">
							<xsl:for-each select="page/ROWSET/ROW">
								<li class="list-group-item">
									<xsl:value-of select="PERSON"/>
								</li>
							</xsl:for-each>
						</ul>
					</div>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-imports/>
		</div>
		</body>
	</html>
</xsl:template>
</xsl:stylesheet>