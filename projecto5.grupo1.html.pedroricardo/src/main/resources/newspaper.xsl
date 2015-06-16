<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="html" indent="no" omit-xml-declaration="yes"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
		encoding="iso-8859-1" />
	<xsl:template match="/">
		<html>
			<head>
				<meta charset="UTF-8" />
				<title>Últimas notícias CNN</title>

				<!-- Latest compiled and minified CSS -->
				<link rel="stylesheet"
					href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css" />

				<!-- Optional theme -->
				<link rel="stylesheet"
					href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css" />

				<!-- Latest compiled and minified JavaScript -->
				<script
					src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
			</head>
			<body>
				<h2>Últimas Notícias: CNN</h2>
				<table class="table table-striped">
					<thead>
						<tr>
							<th style="text-align:center">Título</th>
							<th style="text-align:center">Autor</th>
							<th style="text-align:center">Categoria</th>
							<th style="text-align:center">Data</th>
							<th style="width:40%;text-align:center;">Conteúdo</th>
							<th colspan="3" style="text-align:center">Media (Imagens, Vídeos e Links)</th>
						</tr>
					</thead>
					<tbody>
						<xsl:for-each select="noticias/noticia">
							<tr>
								<td>
									<xsl:value-of select="titulo" />
								</td>
								<td>
									<xsl:value-of select="autor" />
								</td>
								<td>
									<xsl:value-of select="categoria" />
								</td>
								<td>
									<xsl:value-of select="data" />
								</td>
								<td
									style="overflow:scroll; display:block; height:150px; width:100%; font-size:14px;">
									<xsl:value-of select="corpo" />
								</td>
								<td>
									<xsl:element name="a">
										<xsl:attribute name="href">
    										<xsl:value-of select="imagens" />
  										</xsl:attribute>
										<xsl:text>Imagem</xsl:text>
									</xsl:element>
								</td>
								<td>
									<xsl:element name="a">
										<xsl:attribute name="href">
    										<xsl:value-of select="videos" />
  										</xsl:attribute>
										<xsl:text>Vídeo</xsl:text>
									</xsl:element>
								</td>
								<td>
									<xsl:element name="a">
										<xsl:attribute name="href">
    										<xsl:value-of select="url" />
  										</xsl:attribute>
										<xsl:text>Link</xsl:text>
									</xsl:element>
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>