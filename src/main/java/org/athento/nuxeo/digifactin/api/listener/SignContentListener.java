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
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.api.model.PropertyNotFoundException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import java.io.File;
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
                // Prepare content to sign
                Blob content = (Blob) doc.getPropertyValue("file:content");
                if (content == null) {
                    LOG.warn("Document has firma:digifactin=true, but it doesn't have a content.");
                    return;
                }
                CoreSession session = ctxt.getCoreSession();
                digifactinClient = new DigifactinClientImpl(session);
                // Get default client information
                String clientId = (String) getProperty(session, "digifactinconfig:clientId");
                String username = (String) getProperty(session, "digifactinconfig:username");
                String password = (String) getProperty(session, "digifactinconfig:password");
                // Token
                String authToken = null;
                try {
                    LOG.info("Singing in Digifactin...");
                    // First, getting token
                    authToken = getAuthToken(clientId, username, password);
                    if (authToken == null) {
                        event.markRollBack();
                        throw new NuxeoException("Unable to authenticate against Digifactin, please check your configuration.");
                    }
                    LOG.info("Login OK: " + authToken);
                    // Sign blob
                    Blob signedBlob = signBlob(session, authToken, username, content);
                    LOG.info ("Signed " + signedBlob.getFilename() + ", " + signedBlob.getMimeType());
                    // Update content with signed blob
                    doc.setPropertyValue("file:content", (Serializable) signedBlob);
                } catch (DigifactinException | IOException e) {
                    LOG.error("Unable to sign with Digifactin", e);
                    event.markRollBack();
                    throw new NuxeoException(e);
                } finally {
                    // Always, update firma:digifactin to false
                    doc.setPropertyValue("firma:digifactin", false);
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
     * @param username
     * @param blob
     * @throws IOException, DigifactinException on error
     */
    private Blob signBlob(CoreSession session, String authToken, String username, Blob blob) throws IOException, DigifactinException {
        if (digifactinClient == null) {
            throw new DigifactinException("Client is not initializated");
        }
        DigifactinResponse response = digifactinClient.signCertified(authToken, getPostValue(session, blob));
        if (response != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Response " + response);
            }
            if (((SignCertifiedResponse) response).getStatusCode() != StatusCode.FIRMADO_CORRECTAMENTE) {
                throw new DigifactinException(((SignCertifiedResponse) response).getsError());
            }
            // Getting signed file from Folder into response
            String folderWithFile = ((SignCertifiedResponse) response).getFolder();
            // Get blog filename
            String filename = blob.getFilename();
            LOG.info("Filename " + filename);
            // Get signed file given the fetch mode
            File signedFile;
            String fetchMode = (String) getProperty(session, "digifactinconfig:fetchMode");
            if (DigifactinUtils.FETCHMODE_FILESYSTEM.equals(fetchMode)) {
                signedFile = new File(DigifactinUtils.sanitizeFile(folderWithFile));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Signed file " + signedFile);
                }
                if (!signedFile.exists()) {
                    throw new DigifactinException("Signed file is not found: " + signedFile);
                }
            } else {
                // Download signed file
                DigifactinResponse downloadResponse = digifactinClient.download(authToken, username,
                        DigifactinUtils.sanitizeString(folderWithFile), true);
                if (((DownloadResponse) downloadResponse).getStatus() == 200) {
                    signedFile = ((DownloadResponse) downloadResponse).getSignedFile();
                    // Check file content
                    if (!DigifactinUtils.checkValidSignedFile(signedFile)) {
                        throw new DigifactinException("Signed file is invalid, please check your configuration.");
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Signed file " + signedFile.getAbsolutePath());
                    }
                } else {
                    throw new DigifactinException("Signed file is invalid, please check your configuration.");
                }
            }
            if (signedFile == null) {
                throw new DigifactinException("Signed file must be not null");
            }
            // Update blob with signed file (always in pdf)
            blob = new FileBlob(signedFile);
            blob.setMimeType(DigifactinUtils.PDF);
            blob.setFilename(filename + ".pdf");
        }
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
        postValue.setUser((String) getProperty(session, "digifactinconfig:username"));
        postValue.setCertificate((String) getProperty(session, "digifactinconfig:certificate"));
        postValue.setCertruta((String) getProperty(session, "digifactinconfig:certruta"));
        postValue.setFolder((String) getProperty(session, "digifactinconfig:folder"));
        postValue.setName((String) getProperty(session, "digifactinconfig:name"));
        postValue.setSha((String) getProperty(session, "digifactinconfig:sha"));
        postValue.setFv((Boolean) getProperty(session, "digifactinconfig:fv", false));
        postValue.setImagen((String) getProperty(session, "digifactinconfig:imagen"));
        postValue.setOivfv((Boolean) getProperty(session, "digifactinconfig:oivfv", false));
        postValue.setIivftp((Boolean) getProperty(session, "digifactinconfig:iivftp", false));
        postValue.setAm((Boolean) getProperty(session, "digifactinconfig:am", false));
        postValue.setAmeni((Boolean) getProperty(session, "digifactinconfig:ameni", false));
        postValue.setPtpfv((Boolean) getProperty(session, "digifactinconfig:ptpfv", false));
        postValue.setCvfv((String) getProperty(session, "digifactinconfig:cvfv"));
        postValue.setChfv((String) getProperty(session, "digifactinconfig:chfv"));
        postValue.setAltofv((String) getProperty(session, "digifactinconfig:altofv"));
        postValue.setAnchofv((String) getProperty(session, "digifactinconfig:anchofv"));
        postValue.setTp((Boolean) getProperty(session, "digifactinconfig:tp", false));
        postValue.setUtp((String) getProperty(session, "digifactinconfig:utp"));
        postValue.setE((Boolean) getProperty(session, "digifactinconfig:e", false));
        postValue.setEuser((String) getProperty(session, "digifactinconfig:euser"));
        postValue.setEadmin((String) getProperty(session, "digifactinconfig:eadmin"));
        postValue.setSt((Boolean) getProperty(session, "digifactinconfig:st", false));
        postValue.setStuser((String) getProperty(session, "digifactinconfig:stuser"));
        postValue.setStpass((String) getProperty(session, "digifactinconfig:stpass"));
        postValue.setSturl((String) getProperty(session, "digifactinconfig:sturl"));
        postValue.setPdfa((Boolean) getProperty(session, "digifactinconfig:pdfa", false));
        postValue.setFvp((Boolean) getProperty(session, "digifactinconfig:fvp", false));
        postValue.setPfv(((Long) getProperty(session, "digifactinconfig:pfv", 0)).intValue());
        postValue.setFvpp((Boolean) getProperty(session, "digifactinconfig:fvpp", false));
        postValue.setFvup((Boolean) getProperty(session, "digifactinconfig:fvup", false));
        postValue.setMesdesde(((Long) getProperty(session, "digifactinconfig:mesdesde", 0)).intValue());
        postValue.setAnyodesde(((Long) getProperty(session, "digifactinconfig:anyodesde", 0)).intValue());
        postValue.setTipoperiodo(((Long) getProperty(session, "digifactinconfig:tipoperiodo", 0)).intValue());
        FormDataFile image = new FormDataFile();
        image.setFilename(blob.getFilename());
        image.setFile(blob.getFile());
        image.setMimetype(blob.getMimeType());
        postValue.setUploadedImage(image);
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
                Boolean digifactEnabled = (Boolean) doc.getPropertyValue("firma:digifactin");
                return digifactEnabled != null && digifactEnabled;
            } catch (PropertyNotFoundException e) {
                LOG.trace("Unable to check digifact sign flag because " +
                        "property 'firma:digifactin' is not found in the document type. Please, ensure it is included.");
            }
        }
        return hasSign;
    }


}
