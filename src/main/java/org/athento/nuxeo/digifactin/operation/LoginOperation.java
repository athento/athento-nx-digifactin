package org.athento.nuxeo.digifactin.operation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.athento.nuxeo.digifactin.api.client.DigifactinClient;
import org.athento.nuxeo.digifactin.api.client.DigifactinClientImpl;
import org.athento.nuxeo.digifactin.api.client.DigifactinResponse;
import org.athento.nuxeo.digifactin.api.client.LoginUserResponse;
import org.athento.nuxeo.digifactin.api.exception.DigifactinException;
import org.athento.nuxeo.digifactin.api.model.PostValue;
import org.athento.nuxeo.digifactin.api.util.FormDataFile;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.*;

import java.io.IOException;

/**
 * Login in Digifactin.
 *
 * @author victorsanchez
 */
@Operation(id = LoginOperation.ID, category = "Digifactin", label = "Login", description = "Login user",
        since = "8.10", addToStudio = false)
public class LoginOperation {

    private static final Log LOG = LogFactory.getLog(LoginOperation.class);

    /**
     * Operation ID.
     */
    public static final String ID = "Digifactin.Login";

    @Param(name = "CLIENT_ID", description = "Palabra de paso proporcionada por Notarnet")
    String clientId;

    @Param(name = "USUARIO", description = "Nombre de usuario")
    String username;

    @Param(name = "PASSWORD", description = "Contraseña de usuario")
    String password;

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
        DigifactinResponse response = client.loginUser(clientId, username, password);
        if (response != null) {
            // Login
            LOG.info("Response " + response);
            return ((LoginUserResponse) response).getAuth_token();
        } else {
            throw new DigifactinException("Login error, response is empty from Digifactin server.");
        }
    }


}
