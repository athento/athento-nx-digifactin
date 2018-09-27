package org.athento.nuxeo.digifactin.api.util;


import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.athento.nuxeo.digifactin.api.exception.DigifactinException;
import org.athento.nuxeo.digifactin.api.model.PostValue;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.runtime.api.Framework;
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
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Digifactin utils.
 */
public final class DigifactinUtils {

    /** Log. */
    private static final Log LOG = LogFactory.getLog(DigifactinUtils.class);

    public static final String CONFIG_PATH = "/DigifactinConfig";

    public static final String FETCHMODE_DOWNLOAD = "Download";
    public static final String FETCHMODE_FILESYSTEM = "Filesystem";
    public static final String PDF = "application/pdf";

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

    /**
     * Parse postValue to map.
     *
     * @param postValue
     * @return
     */
    public static Map<String,Object> parsePostValue(PostValue postValue) {
        Map<String, Object> data = new HashMap<>();
        data.put("USER", postValue.getUser());
        data.put("CERTIFICATE", postValue.getCertificate());
        data.put("CERTRUTA", postValue.getCertruta());
        data.put("FOLDER", postValue.getFolder());
        data.put("NAME", postValue.getName());
        data.put("SHA", postValue.getSha());
        data.put("FV", postValue.isFv());
        data.put("IMAGEN", postValue.getImagen());
        data.put("OIVFV", postValue.isOivfv());
        data.put("IIVFTP", postValue.isIivftp());
        data.put("AM", postValue.isAm());
        data.put("AMENI", postValue.isAmeni());
        data.put("PTPFV", postValue.isPtpfv());
        data.put("CVFV", postValue.getCvfv());
        data.put("CHFV", postValue.getChfv());
        data.put("ALTOFV", postValue.getAltofv());
        data.put("ANCHOFV", postValue.getAnchofv());
        data.put("TP", postValue.isTp());
        data.put("UTP", postValue.getUtp());
        data.put("E", postValue.isE());
        data.put("EUSER", postValue.getEuser());
        data.put("EADMIN", postValue.getEadmin());
        data.put("ST", postValue.isSt());
        data.put("STUSER", postValue.getStuser());
        data.put("STPASS", postValue.getStpass());
        data.put("STURL", postValue.getSturl());
        data.put("PDFA", postValue.isPdfa());
        data.put("FVP", postValue.isFvp());
        data.put("PFV", postValue.getPfv());
        data.put("FVPP", postValue.isFvpp());
        data.put("FVUP", postValue.isFvup());
        data.put("MESDESDE", postValue.getMesdesde());
        data.put("ANYODESDE", postValue.getAnyodesde());
        data.put("TIPOPERIODO", postValue.getTipoperiodo());
        data.put("UploadedImage", postValue.getUploadedImage());
        return data;
    }

    /**
     * Run operation.
     *
     * @param operationId
     * @param input
     * @param params
     * @param session
     * @return
     * @throws OperationException
     */
    public static Object runOperation(String operationId, Object input,
                                      Map<String, Object> params, CoreSession session)
            throws OperationException {
        AutomationService automationManager = Framework
                .getLocalService(AutomationService.class);
        // Input setting
        OperationContext ctx = new OperationContext(session);
        ctx.setInput(input);
        Object o = null;
        // Setting parameters of the chain
        try {
            // Run Automation service
            o = automationManager.run(ctx, operationId, params);
        } catch (Exception e) {
            LOG.error("Unable to run operation: " + operationId
                    + " Exception: " + e.getMessage(), e);
            throw new DigifactinException(e);
        }
        return o;
    }

    /**
     * Read config value.
     *
     * @param session
     * @param key
     * @return
     */
    public static Serializable readConfigValue(CoreSession session, final String key) {
        final List<Serializable> values = new ArrayList<>();
        new UnrestrictedSessionRunner(session) {
            @Override
            public void run() {
                DocumentModel conf = session.getDocument(new PathRef(
                        CONFIG_PATH));
                Serializable value = conf.getPropertyValue(key);
                if (value != null) {
                    values.add(value);
                }
            }
        }.runUnrestricted();
        if (!values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    /**
     * Sanitize folder to absolute path from digifactin response.
     *
     * @param folder
     * @return
     */
    public static String sanitizeFile(String folder) {
        return File.separator + folder.replace("\\", File.separator).replace("\r\n", "");
    }

    /**
     * Sanitize string.
     *
     * @param string
     * @return
     */
    public static String sanitizeString(String string) {
        return string.replace("\r\n", "");
    }

    /**
     * Check signed file.
     *
     * @param signedFile
     * @return
     */
    public static boolean checkValidSignedFile(File signedFile) {
        if (signedFile == null) {
            return false;
        }
        try {
            List<String> lines = FileUtils.readLines(signedFile);
            return !lines.isEmpty() && !lines.get(0).contains("null");
        } catch (IOException e) {
            return false;
        }
    }

}

