package org.athento.nuxeo.digifactin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;

import java.io.Serializable;
import java.util.Map;

import static org.jboss.seam.ScopeType.CONVERSATION;

/**
 * Digifactin Admin.
 */
@Name("digifactinAdmin")
@Scope(CONVERSATION)
public class DigifactinManager implements Serializable {

    private static final Log LOG = LogFactory.getLog(DigifactinManager.class);

    private static final long serialVersionUID = 1L;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected FacesMessages facesMessages;

    @In(create = true)
    protected Map<String, String> messages;

    protected DocumentModel digifactinConfigDoc;

    public DocumentModel getDigifactinConfigDoc() {
        if (digifactinConfigDoc == null) {
            digifactinConfigDoc = documentManager.getDocument(new PathRef("/DigifactinConfig"));
        }
        return digifactinConfigDoc;
    }

    /**
     * Save configuration. It is used to ignore events and other things about updateDocument.
     */
    public void save() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Saving configuration in digifacting...");
        }
        documentManager.saveDocument(digifactinConfigDoc);
        documentManager.save();
        facesMessages.add(StatusMessage.Severity.INFO, messages.get("label.parameters.saved"));
        digifactinConfigDoc = null;
    }

}
