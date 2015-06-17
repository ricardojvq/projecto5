package projecto5.grupo1.html.pedroricardo.xmlsubscriber;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import javax.naming.CommunicationException;
import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import projecto5.grupo1.html.pedroricardo.validator.XMLValid;

public class XMLSubscriber {

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();

		props.setProperty("java.naming.factory.initial","org.jboss.naming.remote.client.InitialContextFactory");
		props.setProperty("java.naming.provider.url","http-remoting://127.0.0.1:8080");
		props.setProperty("java.naming.security.principal", "user");
		props.setProperty("java.naming.security.credentials", "qwerty123");

		InitialContext ic = new InitialContext(props);

		
		try {
			ConnectionFactory factory = (ConnectionFactory) ic.lookup("jms/RemoteConnectionFactory");

			Topic topic = (Topic) ic.lookup("jms/topic/noticias");
			Connection connection = factory.createConnection("user","qwerty123");
			try {
				connection.setClientID("user2");
			} catch (InvalidClientIDException ice) {
				Random rd = new Random();
				int r = 100 * rd.nextInt();
				connection.setClientID("user"+r);
			}

			Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

			MessageConsumer receiver = session.createDurableConsumer(topic, "user");
			connection.start();

			TextMessage msg = null;
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
			if (msg != null) {
				String msg2XML = msg.getText();
				Document file = loadXMLFromString(msg2XML);
				String s = XMLSubscriber.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				String[] temps = s.split("/");
				String finalPath = "";
				String rootPath = "";
				for (int i = 0; i < temps.length-1; i++) {
					rootPath += temps[i] + "/";
				}
				finalPath = rootPath;
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				File newFile = new File(finalPath+"newsoutput.xml");
				Result output = new StreamResult(newFile);
				Source input = new DOMSource(file);
				transformer.transform(input, output);
				XMLValid validator = new XMLValid();
				validator.validateXML(newFile);

				// HTML creation
				InputStream xslFile = XMLSubscriber.class.getResourceAsStream("/newspaper.xsl");
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer tr = tf.newTransformer(new StreamSource(xslFile));
				output = new StreamResult(new File(finalPath+"noticias.html"));
				tr.transform(input, output);
				receiver.close();
				session.close();
				connection.close();

				System.out.println("Message Received");

			} else System.out.println("No messages to receive.");
		} catch (CommunicationException ce) {
			System.out.println("Server is DOWN!");
		}


	}

	public static Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

}
