package projecto5.grupo1.stats.pedroricardo.xmlsubscriber;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.InvalidClientIDException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import projecto5.grupo1.stats.pedroricardo.validator.XMLValid;

public class XMLStats {

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();

		props.setProperty("java.naming.factory.initial","org.jboss.naming.remote.client.InitialContextFactory");
		props.setProperty("java.naming.provider.url","http-remoting://127.0.0.1:8080");
		props.setProperty("java.naming.security.principal", "user");
		props.setProperty("java.naming.security.credentials", "qwerty123");

		InitialContext ic = new InitialContext(props);

		ConnectionFactory factory = (ConnectionFactory) ic.lookup("jms/RemoteConnectionFactory");

		Topic topic = (Topic) ic.lookup("jms/topic/noticias");

		Connection connection = factory.createConnection("user","qwerty123");
		try {
			connection.setClientID("user5");
		} catch (InvalidClientIDException ice) {
			Random rd = new Random();
			int r = 100 * rd.nextInt();
			connection.setClientID("user"+r);
		}

		Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

		MessageConsumer receiver = session.createDurableConsumer(topic, "user");
		TextMessage msg = null;
		connection.start();
		while (true) {
			Message m = receiver.receive(1000);
			if (m != null) {
				if (m instanceof TextMessage) {
					msg = (TextMessage) m;
				} else {
					break;
				}
			} else {
				break;
			}
		}

		String msg2XML = msg.getText();
		Document file = loadXMLFromString(msg2XML);

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Result output = new StreamResult(new File("newsoutput.xml"));
		Source input = new DOMSource(file);
		transformer.transform(input, output);
		File newsOP = new File("newsoutput.xml");
		
		XMLValid validator = new XMLValid();
		validator.validateXML(newsOP);

		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();

		Document doc = dBuilder.parse(newsOP);

		if (doc.hasChildNodes()) {

			Map<String,Integer> resumo = printNote(doc.getChildNodes());
			SimpleDateFormat df = new SimpleDateFormat("(dd/MM/yyyy @ HH:mm)");
			String resumo2file = "\n\n----------------\n\n"+"Resumo das notícias: "+ df.format(new Date()) +"\n\n";
			for (String s:resumo.keySet()) {
				if (!s.equalsIgnoreCase("excluidas")) {
					resumo2file += s.substring(0, 1).toUpperCase() + s.substring(1) + " - "+resumo.get(s) + " notícia(s)\n";
				}
			}
			resumo2file += "\n\nNotícias excluídas (mais de 12 horas): "+resumo.get("excluidas");
			try {
				File statsFile = new File("stats.txt");
				if (!statsFile.exists()) {
					statsFile.createNewFile();
				}

				FileWriter fw = new FileWriter(statsFile, true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(resumo2file);
				bw.close();
				fw.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		receiver.close();
		session.close();
		connection.close();

		System.out.println("Message Received");


	}

	public static Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

	private static Map<String,Integer> printNote(NodeList nodeList) throws DOMException, ParseException {
		int countOlderThan12 = 0;
		Map<String, Integer> cat = new HashMap<String, Integer>();
		for (int count = 0; count < nodeList.getLength(); count++) {

			Node tempNode = nodeList.item(count);

			if (tempNode.hasChildNodes()) {
				NodeList list2 = tempNode.getChildNodes();

				for (int i = 0; i < list2.getLength(); i++) {
					Node tNode = list2.item(i);
					if (tNode.hasChildNodes()) {
						NodeList nodeMap = tNode.getChildNodes();
						String hora = "";
						String data = "";
						boolean later = false;
						for (int j = 0; j < nodeMap.getLength(); j++) {
							if (nodeMap.item(j).getNodeName().equalsIgnoreCase("data")) {
								data = nodeMap.item(j).getTextContent();
							}
							if (nodeMap.item(j).getNodeName().equalsIgnoreCase("hora")) {
								later = laterThan12(data,nodeMap.item(j).getTextContent());
								if (later) {
									countOlderThan12++;
									cat.put("excluidas",countOlderThan12);
								}
							}
							if (nodeMap.item(j).getNodeName().equalsIgnoreCase("categoria") && !later) {
								if (cat.containsKey(nodeMap.item(j).getTextContent())) {
									int addOne = cat.get(nodeMap.item(j).getTextContent()) + 1;
									cat.put(nodeMap.item(j).getTextContent(), addOne);
								} else {
									String catgr = nodeMap.item(j).getTextContent();
									int c = 1;
									cat.put(catgr, c);
								}
							}
						}
					}

				}
			}
		}
		return cat;
	}

	private static boolean laterThan12(String data, String hora) throws ParseException {
		boolean later = false;
		DateFormat df = new SimpleDateFormat("HH:mm");
		String dataHora = data+" "+hora;
		Date publishTime = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(dataHora);
		Date currentTime = Calendar.getInstance().getTime();
		long difInMilis = currentTime.getTime() - publishTime.getTime();
		long differenceInHours = (difInMilis) / 1000L / 60L / 60L;
		if (differenceInHours > 12) later = true;
		return later;
	}

}
