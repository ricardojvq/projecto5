package jms;
import java.io.IOException;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import webc.Crawler;


public class TopicSender {

	public static void main(String[] args) throws IOException, NamingException, JMSException {
		
		final String xml2String = Crawler.crawl();


		Properties props = new Properties();

		props.setProperty("java.naming.factory.initial","org.jboss.naming.remote.client.InitialContextFactory");
		props.setProperty("java.naming.provider.url","http-remoting://127.0.0.1:8080");
		props.setProperty("java.naming.security.principal", "user");
		props.setProperty("java.naming.security.credentials", "qwerty123");

		InitialContext ic = new InitialContext(props);

		ConnectionFactory factory = (ConnectionFactory) ic.lookup("jms/RemoteConnectionFactory");

		Topic topic = (Topic) ic.lookup("jms/topic/noticias");

		Connection jmsConnection = factory.createConnection("user","qwerty123");

		Session session = jmsConnection.createSession(false,Session.AUTO_ACKNOWLEDGE);

		MessageProducer sender = session.createProducer(topic);

		TextMessage msg = session.createTextMessage(xml2String);

		sender.send(msg);
		System.out.println("Message sent!");
		sender.close();
		session.close();
		jmsConnection.close();

	}

}
