package projecto5.grupo1.html.pedroricardo.xmlsubscriber;

import java.io.File;
import java.io.StringReader;
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
import org.xml.sax.InputSource;

public class XMLSubscriber {
	
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
		connection.setClientID("user2");

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

}
