package org.athento.nuxeo.digifactin.api.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.athento.nuxeo.digifactin.api.client.DigifactinClientImpl;
import org.athento.nuxeo.digifactin.api.exception.DigifactinException;
import org.nuxeo.ecm.automation.core.util.DocumentHelper;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import java.io.IOException;

/**
 * Listener to Sign the xpath content metadata when a new blob is attached.
 */
public class SignBlobListener extends SignListener implements EventListener {

    /** Log. */
    private static final Log LOG = LogFactory.getLog(SignBlobListener.class);

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
            // Check if content must be signed
            String toSign = (String) ctxt.getProperties().get("firma:digifactin");
            if (Boolean.valueOf(toSign)) {
                // Prepare content to sign
                Blob content = (Blob) ctxt.getProperties().get("blob");
                if (content == null) {
                    LOG.warn("Document has firma:digifactin=true, but it doesn't have an attach content.");
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
                    DocumentHelper.addBlob(doc.getProperty("files"), signedBlob);
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

}
