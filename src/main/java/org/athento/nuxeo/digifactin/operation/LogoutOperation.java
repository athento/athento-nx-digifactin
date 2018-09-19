package org.athento.nuxeo.digifactin.operation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.athento.nuxeo.digifactin.api.client.DigifactinClient;
import org.athento.nuxeo.digifactin.api.client.DigifactinClientImpl;
import org.athento.nuxeo.digifactin.api.client.DigifactinResponse;
import org.athento.nuxeo.digifactin.api.client.LogoutResponse;
import org.athento.nuxeo.digifactin.api.exception.DigifactinException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Logout Digifactin.
 *
 * @author victorsanchez
 */
@Operation(id = LogoutOperation.ID, category = "Digifactin", label = "Logout", description = "Logout",
        since = "8.10", addToStudio = false)
public class LogoutOperation {

    private static final Log LOG = LogFactory.getLog(LogoutOperation.class);

    /**
     * Operation ID.
     */
    public static final String ID = "Digifactin.Logout";

    @Param(name = "USUARIO", description = "Usuario para logout")
    protected String username;

    @Param(name = "TOKEN", description = "Authentication token")
    protected String token;

    /**
     * Session.
     */
    @Context
    protected CoreSession session;

    /**
     * Run, login Digifacting server.
     *
     * @return
     * @throws DigifactinException error
     */
    @OperationMethod
    public String run() throws DigifactinException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Running Login operation ...");
        }
        DigifactinClient client = new DigifactinClientImpl(session);
        DigifactinResponse response = client.logout(username, token);
        if (response != null) {
            return String.valueOf(((LogoutResponse) response).getStatus());
        } else {
            throw new DigifactinException("Logout error, response is empty from Digifactin server.");
        }
    }


}
