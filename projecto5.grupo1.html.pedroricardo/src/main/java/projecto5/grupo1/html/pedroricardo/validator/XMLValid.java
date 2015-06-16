package projecto5.grupo1.html.pedroricardo.validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class XMLValid {
	
	public XMLValid() {}
	
	public void validateXML(File file) throws SAXException, IOException {
		InputStream schemaFile = getClass().getResourceAsStream("/newspaper.xsd");
		Source xsd = new StreamSource(schemaFile);
		Source xmlFile = new StreamSource(file);
		SchemaFactory schemaFactory = SchemaFactory
		    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(xsd);
		Validator validator = schema.newValidator();
		try {
		  validator.validate(xmlFile);
		  System.out.println("\n----\n"+xmlFile.getSystemId() + " é válido.\n----\n");
		} catch (SAXException e) {
		  System.out.println("\n----\n"+xmlFile.getSystemId() + " NÃO é válido.");
		  System.out.println("Reason: " + e.getLocalizedMessage()+"\n----\n");
		}
	}

}
