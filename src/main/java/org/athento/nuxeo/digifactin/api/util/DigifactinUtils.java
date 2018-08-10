package org.athento.nuxeo.digifactin.api.util;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by victorsanchez on 26/10/16.
 */
public final class DigifactinUtils {

    public static final String CONFIG_PATH = "/ExtendedConfig";

    /**
     * Read extended config properties.
     *
     * @param session
     * @return
     */
    public static Map<String, Object> readExtendedConfig(CoreSession session) {
        Map<String, Object> config = new HashMap<String, Object>();
        DocumentModel conf = session.getDocument(new PathRef(
                CONFIG_PATH));
        for (String schemaName : conf.getSchemas()) {
            Map<String, Object> metadata = conf.getProperties(schemaName);
            for (String keyName : metadata.keySet()) {
                String key = keyName;
                Object val = conf.getPropertyValue(key);
                config.put(key, val);
            }
        }
        return config;
    }

    /**
     * Get extended config.
     *
     * @param xpath of property of extended config
     * @return return extended config
     */
    public synchronized static Object getExtendedConfig(CoreSession session, String xpath) {
        return readExtendedConfig(session).get(xpath);
    }

    /**
     * Get XML from JAXB object.
     *
     * @param output
     */
    public static Document getJaxb(Object output, String pack) throws JAXBException {
        Document document = null;
        try {
            // Make document from JAXBContext
            document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
            JAXBContext jc = JAXBContext
                    .newInstance(pack);
            Marshaller marshaller = jc.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(output, document);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * Format XML for a document input.
     *
     * @param input
     * @return
     */
    public static String formatXML(Document input) {
        try {
            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");

            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(input);
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch (Exception e) {
            return input.toString();

        }
    }

    /**
     * Format date.
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }
}

