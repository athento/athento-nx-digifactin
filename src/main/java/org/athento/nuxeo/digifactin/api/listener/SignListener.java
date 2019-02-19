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
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Abstract class to Sign against Digifactin.
 */
public abstract class SignListener {

    /** Log. */
    private static final Log LOG = LogFactory.getLog(SignListener.class);

    /** Client. */
    protected DigifactinClient digifactinClient;

    /**
     * Get property from extended config.
     *
     * @param session
     * @param property
     * @return
     */
    protected Object getProperty(CoreSession session, String property) {
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
    protected Object getProperty(CoreSession session, String property, Object defaultValue) {
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
    protected String getAuthToken(String clientId, String username, String password) throws DigifactinException {
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
    protected Blob signBlob(CoreSession session, String authToken, String username, Blob blob, DocumentModel doc) throws IOException, DigifactinException {
        if (digifactinClient == null) {
            throw new DigifactinException("Client is not initializated");
        }
        String digiUniqueId = doc.getId() != null ? doc.getId() : UUID.randomUUID().toString();
        DigifactinResponse response = digifactinClient.signCertified(authToken, getPostValue(session, blob, digiUniqueId));
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
            if (!filename.toLowerCase().endsWith(".pdf")) {
                filename += ".pdf";
            }
            blob.setFilename(filename);
        }
        return blob;
    }

    /**
     * Generate a PostValue with default params.
     *
     * @param session is the core session
     * @param blob is the content
     * @param docId is the document id
     * @return
     * @throws IOException on blob error
     */
    private PostValue getPostValue(CoreSession session, Blob blob, String docId) throws IOException {
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
        image.setFilename(docId + "_" + blob.getFilename());
        image.setFile(blob.getFile());
        image.setMimetype(blob.getMimeType());
        postValue.setUploadedImage(image);
        return postValue;
    }


}
