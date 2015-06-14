package webc;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import jaxb.Noticia;
import jaxb.Noticias;
import marshall.JAXBHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.ProgressBar;

public class Crawler {
	final static String cnnURL = "http://edition.cnn.com";
	final static String cnnPrefix = "http://edition.cnn.com";

	public static String crawl() throws IOException {

		org.jsoup.Connection connection = Jsoup.connect(cnnURL);
		Document html = connection.get();
		Elements headlines = html.select("[data-analytics=News%20+%20buzz_list-xs_article_] > a[href]");
		Noticias newsAgg = new Noticias();
		int count = 0;
		System.out.println("A carregar notícias...");
		for (Element e:headlines) {
			// Saltar se não for notícia (link começar por "2015/06/08"
			if (e.attr("href").startsWith("/2015")) {
				if (count > 100) break;
				org.jsoup.Connection c = Jsoup.connect(cnnPrefix + e.attr("href")).timeout(0);
				Document newsHTML = c.get();
				Noticia n = new Noticia();
				n.setTitulo(e.text());
				n.setUrl(cnnPrefix + e.attr("href"));
				n.setAutor(newsHTML.select("span.metadata__byline__author").text());
				n.setData(e.attr("href").substring(1, 11));
				String aux = e.attr("href").substring(11, 20);
				String[] categ = aux.split("/");
				n.setCategoria(categ[1]);
				Elements newsEl = newsHTML.select("p.zn-body__paragraph");
				String body = "";
				for (Element el:newsEl) {
					body += el.text();
				}
				n.setCorpo(body);
				newsAgg.getNoticia().add(n);
			}
			int tamanhototal = headlines.size();
			ProgressBar bar = new ProgressBar();
			bar.update(count, tamanhototal);
			count++;
		}
		File xmlFile = new File("news.xml");
		try {
			JAXBHandler.marshal(newsAgg.getNoticia(), xmlFile);
		} catch (JAXBException e1) {

			e1.printStackTrace();
		}

		return XMLConverter.convertXMLFileToString("news.xml");
	}

}
