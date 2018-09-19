package org.athento.nuxeo.digifactin.api.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.athento.nuxeo.digifactin.api.client.*;
import org.athento.nuxeo.digifactin.api.exception.DigifactinException;
import org.athento.nuxeo.digifactin.api.model.PostValue;
import org.athento.nuxeo.digifactin.api.util.DigifactinUtils;
import org.athento.nuxeo.digifactin.api.util.FormDataFile;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.model.PropertyNotFoundException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import java.io.IOException;
import java.io.Serializable;

/**
 * Listener to Sign the file:content metadata when a new document is created.
 */
public class SignContentListener implements EventListener {

    /** Log. */
    private static final Log LOG = LogFactory.getLog(SignContentListener.class);

    /** Client. */
    private DigifactinClient digifactinClient;

    /**
     * Handle event.
     *
     * @param event
     */
    @Override
    public void handleEvent(Event event) {
        DocumentEventContext ctxt = (DocumentEventContext) event.getContext();
        if (ctxt instanceof DocumentEventContext) {
            DocumentModel doc = ctxt.getSourceDocument();
            if (doc.isVersion()) {
                return;
            }
            if (hasSignFlag(doc)) {
                LOG.info("Singing in Digifacting...");
                CoreSession session = ctxt.getCoreSession();
                digifactinClient = new DigifactinClientImpl(session);
                // Get default client information
                String clientId = (String) getProperty(session, "digiextendedconfig:clientId");
                String username = (String) getProperty(session, "digiextendedconfig:username");
                String password = (String) getProperty(session, "digiextendedconfig:password");
                // Token
                String authToken = null;
                try {
                    // First, getting token
                    authToken = getAuthToken(clientId, username, password);
                    if (authToken == null) {
                        event.markRollBack();
                        throw new NuxeoException("Unable to authenticate against Digifactin, please check your configuration.");
                    }
                    LOG.info("Login OK: " + authToken);
                    // Sign blob
                    Blob blob = signBlob(session, authToken, (Blob) doc.getPropertyValue("file:content"));
                    // Update content with signed blob
                    doc.setPropertyValue("file:content", (Serializable) blob);
                } catch (DigifactinException | IOException e) {
                    LOG.error("Unable to sign with Digifactin", e);
                    event.markRollBack();
                    throw new NuxeoException(e);
                } finally {
                    try {
                        digifactinClient.logout(username, authToken);
                        LOG.info("Logout OK for " + authToken);
                    } catch (DigifactinException e) {
                        LOG.error("Unable to do logout", e);
                    }
                }
            }
        }
    }

    /**
     * Get property from extended config.
     *
     * @param session
     * @param property
     * @return
     */
    private Object getProperty(CoreSession session, String property) {
        return getProperty(session, property, null);
    }

    /**
     * Get property from extended config.
     *
     * @param session
     * @param property
     * @param defaultValue
     * @return
     */
    private Object getProperty(CoreSession session, String property, Object defaultValue) {
        Object value = DigifactinUtils.readConfigValue(session, property);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Get auth token from Digifactin.
     *
     * @param clientId
     * @param username
     * @param password
     * @return
     * @throws DigifactinException
     */
    private String getAuthToken(String clientId, String username, String password) throws DigifactinException {
        if (digifactinClient == null) {
            throw new DigifactinException("Client is not initializated");
        }
        DigifactinResponse response = digifactinClient.loginUser(clientId, username, password);
        return ((LoginUserResponse) response).getAuth_token();
    }

    /**
     * Sign blob.
     *
     * @param session
     * @param authToken
     * @param blob
     * @throws IOException, DigifactinException on error
     */
    private Blob signBlob(CoreSession session, String authToken, Blob blob) throws IOException, DigifactinException {
        if (digifactinClient == null) {
            throw new DigifactinException("Client is not initializated");
        }
        LOG.info("Signing blob " + blob.getFilename() + ", " + blob.getMimeType());
        DigifactinResponse response = digifactinClient.signCertified(authToken, getPostValue(session, blob));
        if (response != null) {
            LOG.info("Response " + response);
            if (((SignCertifiedResponse) response).getStatusCode() != StatusCode.FIRMADO_CORRECTAMENTE) {
                throw new DigifactinException(((SignCertifiedResponse) response).getsError());
            }
        }
        // TODO: Getting blob from Digifactin and return
        return blob;
    }

    /**
     * Generate a PostValue with default params.
     *
     * @param session is the core session
     * @param blob is the content
     * @return
     * @throws IOException on blob error
     */
    private PostValue getPostValue(CoreSession session, Blob blob) throws IOException {
        PostValue postValue = new PostValue();
        postValue.setUser((String) getProperty(session, "digiextendedconfig:username"));
        postValue.setCertificate((String) getProperty(session, "digiextendedconfig:certificate"));
        postValue.setCertruta((String) getProperty(session, "digiextendedconfig:certruta"));
        postValue.setFolder((String) getProperty(session, "digiextendedconfig:folder"));
        postValue.setName((String) getProperty(session, "digiextendedconfig:name"));
        postValue.setSha((String) getProperty(session, "digiextendedconfig:sha"));
        postValue.setFv((Boolean) getProperty(session, "digiextendedconfig:fv", false));
        postValue.setImagen((String) getProperty(session, "digiextendedconfig:imagen"));
        postValue.setOivfv((Boolean) getProperty(session, "digiextendedconfig:oivfv", false));
        postValue.setIivftp((Boolean) getProperty(session, "digiextendedconfig:iivftp", false));
        postValue.setAm((Boolean) getProperty(session, "digiextendedconfig:am", false));
        postValue.setAmeni((Boolean) getProperty(session, "digiextendedconfig:ameni", false));
        postValue.setPtpfv((Boolean) getProperty(session, "digiextendedconfig:ptpfv", false));
        postValue.setCvfv((String) getProperty(session, "digiextendedconfig:cvfv"));
        postValue.setChfv((String) getProperty(session, "digiextendedconfig:chfv"));
        postValue.setAltofv((String) getProperty(session, "digiextendedconfig:altofv"));
        postValue.setAnchofv((String) getProperty(session, "digiextendedconfig:anchofv"));
        postValue.setTp((Boolean) getProperty(session, "digiextendedconfig:tp", false));
        postValue.setUtp((String) getProperty(session, "digiextendedconfig:utp"));
        postValue.setE((Boolean) getProperty(session, "digiextendedconfig:e", false));
        postValue.setEuser((String) getProperty(session, "digiextendedconfig:euser"));
        postValue.setEadmin((String) getProperty(session, "digiextendedconfig:eadmin"));
        postValue.setSt((Boolean) getProperty(session, "digiextendedconfig:st", false));
        postValue.setStuser((String) getProperty(session, "digiextendedconfig:stuser"));
        postValue.setStpass((String) getProperty(session, "digiextendedconfig:stpass"));
        postValue.setSturl((String) getProperty(session, "digiextendedconfig:sturl"));
        postValue.setPdfa((Boolean) getProperty(session, "digiextendedconfig:pdfa", false));
        postValue.setFvp((Boolean) getProperty(session, "digiextendedconfig:fvp", false));
        postValue.setPfv((Integer) getProperty(session, "digiextendedconfig:pfv", 0));
        postValue.setFvpp((Boolean) getProperty(session, "digiextendedconfig:fvpp", false));
        postValue.setFvup((Boolean) getProperty(session, "digiextendedconfig:fvup", false));
        postValue.setMesdesde((Integer) getProperty(session, "digiextendedconfig:mesdesde", 0));
        postValue.setAnyiodesde((Integer) getProperty(session, "digiextendedconfig:anyodesde", 0));
        postValue.setTipoperiodo((Long) getProperty(session, "digiextendedconfig:tipoperiodo", 0));
        FormDataFile image = new FormDataFile();
        image.setFile(blob.getFile());
        image.setMimetype(blob.getMimeType());
        postValue.setUploadedImage(image);
        LOG.info("PostValue " + postValue);
        return postValue;
    }

    /**
     * Check if flag is enabled to sign.
     *
     * @param doc
     * @return
     */
    private boolean hasSignFlag(DocumentModel doc) {
        boolean hasSign = false;
        if (doc != null) {
            try {
                Boolean digifactEnabled = (Boolean) doc.getPropertyValue("firmado:digifactin");
                return digifactEnabled != null && digifactEnabled;
            } catch (PropertyNotFoundException e) {
                LOG.trace("Unable to check digifact sign flag because " +
                        "property 'firmado:digifactin' is not found in the document type. Please, ensure it is included.");
            }
        }
        return hasSign;
    }


}
