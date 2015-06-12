<?xml version="1.0" encoding="UTF-8"?>

<html xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsl:version="1.0">
	<head>
		<title> News </title>
	</head>
	<body>
		<h1> Current News in Catalog</h1>
		<table border="1">
			<tr>
				<td>
					<b> Title </b>
				</td>
				<td>
					<b> Body </b>
				</td>
				<td>
					<b> Data </b>
				</td>
			</tr>
			<xsl:for-each select="//noticia">
				<tr>
					<td style="margin-left:20px;margin-bottom:1em;font-size:10pt;color:blue">
						<xsl:value-of select="titulo" />
					</td>
					<td style="margin-left:20px;margin-bottom:1em;font-size:10pt;color:green">
						<xsl:value-of select="corpo" />
					</td>
					<td style="margin-left:20px;margin-bottom:1em;font-size:10pt;color:red">
						<xsl:value-of select="data" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</body>
</html>