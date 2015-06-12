package marshall;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import jaxb.Noticia;
import jaxb.Noticias;

public class JAXBHandler {
	
	// Export
	public static void marshal(List<Noticia> news, File selectedFile)
            throws IOException, JAXBException {
        JAXBContext context;
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(selectedFile));
        context = JAXBContext.newInstance(Noticias.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(new Noticias(news), writer);
        writer.close();
    }
	
	// Import
    public static List<Noticia> unmarshal(File importFile) throws JAXBException {
        Noticias news = new Noticias();
 
        JAXBContext context = JAXBContext.newInstance(Noticias.class);
        Unmarshaller um = context.createUnmarshaller();
        news = (Noticias) um.unmarshal(importFile);
 
        return news.getNoticia();
    }

}
