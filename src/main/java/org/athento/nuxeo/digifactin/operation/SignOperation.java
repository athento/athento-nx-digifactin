package org.athento.nuxeo.digifactin.operation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.athento.nuxeo.digifactin.api.exception.DigifactinException;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Save a Invoice into Digifactin.
 *
 * @author victorsanchez
 */
@Operation(id = SignOperation.ID, category = "Quiter", label = "Save a Invoice document into Digifactin", description = "Save a Invoice document into Digifactin",
        since = "6.0", addToStudio = false)
public class SignOperation {

    private static final Log LOG = LogFactory.getLog(SignOperation.class);

    /**
     * Operation ID.
     */
    public static final String ID = "Digifactin.Sign";

    /**
     * Success result.
     */
    private static final String SUCCESS = "SUCCESS";

    @Param(name = "dealerCode", required = true, description = "Dealer code")
    String dealerCode;

    @Param(name = "user", required = true, description = "Username")
    String user;

    @Param(name = "password", required = true, description = "Password")
    String password;

    /**
     * Param to save document.
     */
    @Param(name = "save", required = false)
    boolean save = true;

    /**
     * Param to debugging.
     */
    @Param(name = "debug", required = false)
    boolean debug = false;

    /**
     * Param to simulate.
     */
    @Param(name = "mockup", required = false)
    boolean mockup = false;

    /** Not send. */
    @Param(name = "ignoreSend", required = false)
    boolean ignoreSend = false;

    /**
     * Context.
     */
    @Context
    protected OperationContext ctx;

    /**
     * Session.
     */
    @Context
    protected CoreSession session;

    /**
     * Run, save the Factura into Quitter tool.
     *
     * @return get document to update with service result information
     * @throws DigifactinException error
     */
    @OperationMethod
    public DocumentModel run(DocumentModel doc) throws DigifactinException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Running Sign operation ...");
        }

        return doc;
    }

}
