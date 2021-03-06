package org.athento.nuxeo.digifactin.api.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.athento.nuxeo.digifactin.api.client.DigifactinClientImpl;
import org.athento.nuxeo.digifactin.api.exception.DigifactinException;
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
public class SignContentListener extends SignListener implements EventListener {

    /** Log. */
    private static final Log LOG = LogFactory.getLog(SignContentListener.class);

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
                    Blob signedBlob = signBlob(session, authToken, username, content, doc);
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
