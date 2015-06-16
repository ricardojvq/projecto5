package jms;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.CommunicationException;
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
		String s = TopicSender.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String[] temps = s.split("/");
		String targetPath = "";
		String rootPath = "";
		for (int i = 0; i < temps.length-2; i++) {
			rootPath += temps[i] + "/";
		}
		targetPath = rootPath + "target/";
		try {
			File f = new File(targetPath+"delayed.txt");
			ConnectionFactory factory = (ConnectionFactory) ic.lookup("jms/RemoteConnectionFactory");
			String option = "";
			if (f.exists()) {
				Topic topic = (Topic) ic.lookup("jms/topic/noticias");

				Connection jmsConnection = factory.createConnection("user","qwerty123");

				Session session = jmsConnection.createSession(false,Session.AUTO_ACKNOWLEDGE);

				MessageProducer sender = session.createProducer(topic);
				byte[] savedNews = Files.readAllBytes(f.toPath());
				String newsMsg = new String(savedNews,"UTF-8");
				TextMessage m = session.createTextMessage(newsMsg);
				sender.close();
				session.close();
				jmsConnection.close();
				f.delete();
				while (!option.equals("1") && !option.equals("2")) {
					System.out.println("Previous messages were found (gathered when server was down), recovered and sent.\n"
							+ "Do you wish to fetch the latest news?\n"
							+ "[1] Yes\n"
							+ "[2] No (exit application)");
					Scanner sc = new Scanner(System.in);
					option = sc.nextLine();
					sc.close();
				}
			}
			if ((option.equals("1") && !f.exists()) || option.equals("")) {
				
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
			if (option.equals("2")) {
				System.out.println("Discarded latest news.");
			}
		} catch (CommunicationException ce) {
			BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath+"delayed.txt"));
			bw.write(xml2String);
			System.out.println("Server is down: saving news for later...");
			bw.close();
		}


	}

}
