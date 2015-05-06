<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:import href="footer.xsl"/>
<!-- HTML kimenet, kódlap beállítása -->
<xsl:output encoding="UTF-8" media-type="text/html" method="html"/>
<xsl:variable name="base" select="'szhely_keres.xsql'"/>

<!-- Gyökérelemre illeszkedő sablon -->
<xsl:template match="/">
	<html>
		<head>
			<link rel="stylesheet" href="style.css"/>
			<title>Szórakozóhelyek</title>
		</head>
		<body>
		<div class="container col-xs-8 col-xs-offset-2">
			<div class="page-header">
				<h1>Szórakozóhelyek</h1>
			</div>
			<xsl:choose>
				<!-- Hiba esetén -->
				<xsl:when test="//xsql-error">
					<p>Hiba történt:
						<xsl:value-of select="//xsql-error/message[1]"/>
					</p>
				</xsl:when>
				<!-- Ha nem történt hiba -->
				<xsl:otherwise>
					<xsl:call-template name="export-form"/>
					<xsl:call-template name="search_form"/>
					<xsl:call-template name="results_header"/>
					<xsl:call-template name="resultset"/>
				</xsl:otherwise>
			</xsl:choose>
			<!-- Lábléc betöltése -->
			<xsl:apply-imports/>
    	</div>
		</body>
	</html>
</xsl:template>

<!-- Keresési űrlap megjelenítése -->
<!-- Legördülő lista -->
<xsl:template name="search_form">
	<h3>Keresés</h3>
	<p><form method="get">
	<select name="search-key">
	   <option value="pname">
	      <xsl:if test="/page/search-key/text()='name'">
	           <xsl:attribute name="selected"/>
	      </xsl:if>Név
	   </option>
	   <option value="address">
	      <xsl:if test="/page/search-key/text()='address'">
	           <xsl:attribute name="selected"/>
	      </xsl:if>Cím
	   </option>
	   <option value="phone">
	      <xsl:if test="/page/search-key/text()='phone'">
	           <xsl:attribute name="selected"/>
	      </xsl:if>Telefonszám
	   </option>
	</select>
	<!-- Beviteli mező -->
	<input name="search-value" type="text" size="50" value="{/page/search-value}"/>
	<!-- Stíluslapot kiválasztó paraméter -->
	<input type="hidden" name="style" value="szhely_keres"/>
	</form>
	</p>
</xsl:template>

<!-- Találatok exportálása -->
<xsl:template name="export-form">
	<h3>Exportlálás</h3>
	<div class="btn-group" role="group" aria-label="...">
			<button type="button" onclick="location.href='szhely_keres.xsql?style=xml_export&amp;skip=0&amp;max-rows=-1&amp;search-key={/page/search-key}&amp;search-value={/page/search-value}'" class="btn btn-default">XML</button>
			<button type="button" onclick="location.href='szhely_keres.xsql?style=txt_export&amp;skip=0&amp;max-rows=-1&amp;search-key={/page/search-key}&amp;search-value={/page/search-value}'" class="btn btn-default">TXT</button>

	</div>
</xsl:template>

<!-- Lapozás -->
<xsl:template name="results_header">
<table width="100%">
<tr>
  <td width="20%" align="left">
     <xsl:if test="number(/page/skip) &gt;= number(/page/max-rows)">
        <xsl:call-template name="results_header_href">
         <xsl:with-param name="label" select="'Előző'"/>
         <xsl:with-param name="skip" select="/page/skip - /page/max-rows"/>
        </xsl:call-template>
     </xsl:if>
  </td>
  <td align="center"><b>Találatok:
     <xsl:choose>
       <!-- Ha van egyáltalán találat -->
       <xsl:when test='/page/PLACES/PLACE'>
         <xsl:value-of select="page/skip + 1"/>
       </xsl:when>
       <xsl:otherwise>0</xsl:otherwise>
     </xsl:choose> -
     <xsl:choose>
       <xsl:when test="number(/page/skip + /page/max-rows) &lt; number(/page/num-results)">
         <xsl:value-of select="/page/skip + /page/max-rows"/>
     </xsl:when>
     <xsl:otherwise>
         <xsl:value-of select="/page/num-results"/>
      </xsl:otherwise>
     </xsl:choose> /
     	<xsl:value-of select="/page/num-results"/>
  </b></td>
  <td width="20%" align="right">
     <xsl:if test="number(/page/skip + /page/max-rows) &lt; number(/page/num-results)">
       <xsl:call-template name="results_header_href">
         <xsl:with-param name="label" select="'Következő'"/>
         <xsl:with-param name="skip" select="/page/skip + /page/max-rows"/>
       </xsl:call-template>
     </xsl:if>
  </td>
</tr>
</table>
</xsl:template>

<!-- Címke és link felrakása -->
<xsl:template name="results_header_href">
  <xsl:param name="label" select="'Hiba'"/>
  <xsl:param name="skip" select="0"/>

<xsl:choose>
  <!-- Ha nincs keresési feltétel -->
  <xsl:when test="/page/search-value = ''">
    <a href="{$base}?skip={$skip}&amp;style=szhely_keres">
      <xsl:value-of select="$label"/>
    </a>
  </xsl:when>
  <!-- Ha van keresési feltétel -->
  <xsl:otherwise>
    <a href="{$base}?search-key={/page/search-key}&amp;search-value={/page/search-value}&amp;skip={$skip}&amp;style=szhely_keres">
       <xsl:value-of select="$label"/>
    </a>
  </xsl:otherwise>
</xsl:choose>
</xsl:template>

<!-- Találatok megjelenítése -->
<xsl:template name="resultset">
	<div>
	<h3> Találatok </h3>
	<xsl:for-each select="page/PLACES/PLACE">
		<div class="panel panel-default">
			<div class="panel-heading">
				<!-- ez az ID-t adjuk át a látogatókat megjelenítő oldalnak -->
				<xsl:variable name="place_id" select="PLACE_ID"/>
				<!-- szhely nevének formázása a látogatók száma alapján -->
				<xsl:variable name="placeName" select="NAME"/>
				<xsl:choose>
					<!-- ha kevesebb, mint 5 látogató volt -->
					<xsl:when test="count(/page/VISITS/PNAME[contains(NAME, $placeName)]) &lt; 5">
						<b><del>
							<a href="latogatok.xsql?place_id={$place_id}">
								<xsl:value-of select="NAME"/>
							</a>
						</del></b><br/>					
					</xsl:when>
					<!-- ha több, mint 15 látogató volt -->
					<xsl:when test="count(/page/VISITS/PNAME[contains(NAME, $placeName)]) &gt; 15">
						<b>
							<a href="latogatok.xsql?place_id={$place_id}">
								<xsl:value-of select="NAME"/>*
							</a>
						</b><br/>					
					</xsl:when>
					<!-- egyébként -->
					<xsl:otherwise>
						<b>
							<a href="latogatok.xsql?place_id={$place_id}">
								<xsl:value-of select="NAME"/>
							</a>
						</b><br/>	
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<div class="panel-body">
				Cím: <xsl:value-of select="ADDRESS"/><br/>
				Telefonszám: <xsl:value-of select="PHONE"/><br/>
				Árfekvés: <xsl:value-of select="PRICE"/><br/>
			</div>
		</div>
	</xsl:for-each>
	</div>
</xsl:template>
</xsl:stylesheet>