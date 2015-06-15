package projecto5.grupo1.html.pedroricardo.xmlsubscriber;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
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

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
		connection.setClientID("user5");

		Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		
		MessageConsumer receiver = session.createDurableConsumer(topic, "user");
		
		connection.start();
		
		TextMessage msg = (TextMessage) receiver.receive();
		String msg2XML = msg.getText();
		Document file = loadXMLFromString(msg2XML);
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Result output = new StreamResult(new File("newsoutput.xml"));
		Source input = new DOMSource(file);
		transformer.transform(input, output);

		File newsOP = new File("newsoutput.xml");

		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();

		Document doc = dBuilder.parse(newsOP);

		if (doc.hasChildNodes()) {

			Map<String,Integer> resumo = printNote(doc.getChildNodes());
			String resumo2file = "Resumo das notícias: \n";
			for (String s:resumo.keySet()) {
				resumo2file += s + " - "+resumo.get(s) + " notícia(s)\n";
			}
			resumo2file += "----------------\n\n";
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

	private static Map<String,Integer> printNote(NodeList nodeList) {
		Map<String, Integer> cat = new HashMap<String, Integer>();
		for (int count = 0; count < nodeList.getLength(); count++) {

			Node tempNode = nodeList.item(count);
			
			if (tempNode.hasChildNodes()) {
				NodeList list2 = tempNode.getChildNodes();
				
				for (int i = 0; i < list2.getLength(); i++) {
					Node tNode = list2.item(i);
					if (tNode.hasChildNodes()) {
						NodeList nodeMap = tNode.getChildNodes();
						for (int j = 0; j < nodeMap.getLength(); j++) {
							if (nodeMap.item(j).getNodeName().equalsIgnoreCase("categoria")) {
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

}
