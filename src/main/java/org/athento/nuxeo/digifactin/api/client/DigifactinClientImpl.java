package org.athento.nuxeo.digifactin.api.client;

import com.sun.jersey.api.client.ClientResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.athento.nuxeo.digifactin.api.exception.DigifactinException;
import org.athento.nuxeo.digifactin.api.model.PostValue;
import org.athento.nuxeo.digifactin.api.util.DigifactinUtils;
import org.athento.nuxeo.digifactin.api.util.RestAPIClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nuxeo.runtime.api.Framework;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * DigifactinClient class.
 *
 * It connects to an API Rest.
 */
public class DigifactinClientImpl implements DigifactinClient {

    /** Log. */
    private static final Log LOG = LogFactory.getLog(DigifactinClientImpl.class);

    private String digifactinURL;

    /**
     * Constructor.
     */
    public DigifactinClientImpl() {
        // Get Digifactin API URL
        digifactinURL = Framework.getProperty("athento.digifactin.url");
    }

    /**
     * Login user.
     *
     * @param clientId is the cliend id
     * @param username is the username
     * @param password is the password
     * @return response is the response
     * @throws DigifactinException on login error
     */
    @Override
    public DigifactinResponse loginUser(String clientId, String username, String password) throws DigifactinException {
        if (clientId == null) {
            throw new DigifactinException("Client Id must be not null");
        }
        if (username == null) {
            throw new DigifactinException("Username must be not null");
        }
        if (password == null) {
            throw new DigifactinException("Password must be not null");
        }
        String loginDataFormat = " { \"client_id\": \"%s\", " +
                              "\"grant_type\": \"password\", " +
                              "\"username\": \"%s\", " +
                              "\"password\": \"%s\" }";

        String loginData = String.format(loginDataFormat, clientId, username, password);

        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        // Execute
        LoginUserResponse digifactinResponse = new LoginUserResponse();
        try {
            ClientResponse apiResponse = RestAPIClient.doPost(digifactinURL + "/login", headers, loginData);
            if (apiResponse != null) {
                String result = apiResponse.getEntity(String.class);
                ObjectMapper mapper = new ObjectMapper();
                digifactinResponse = mapper.readValue(result, LoginUserResponse.class);
                return digifactinResponse;
            }
        } catch (IOException e) {
            throw new DigifactinException(e);
        }
        return digifactinResponse;
    }

    /**
     * Logout.
     *
     * @param username
     * @param authToken
     * @return response is the response
     * @throws DigifactinException on logout error
     */
    @Override
    public DigifactinResponse logout(String username, String authToken) throws DigifactinException {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", username + " " + authToken);

        // Execute
        DigifactinResponse digifactinResponse = new LogoutResponse();
        try {
            ClientResponse apiResponse = RestAPIClient.doPost(digifactinURL + "/logout", headers, "{}");
            if (apiResponse != null) {
                digifactinResponse.setStatus(apiResponse.getStatus());
            }
        } catch (IOException e) {
            throw new DigifactinException(e);
        }
        return digifactinResponse;
    }

    /**
     * Sing certified.
     *
     * @param authToken
     * @param postValue
     * @throws DigifactinException on error
     * @return
     */
    @Override
    public DigifactinResponse signCertified(String authToken, PostValue postValue) throws DigifactinException {
        if (postValue == null) {
            throw new DigifactinException("PostValue must be not null");
        }

        if (digifactinURL == null) {
            throw new DigifactinException("Please check the Digifactin API url in the configuration file");
        }

        // Headers
        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", postValue.getUser() + " " + authToken);

        // Form data
        Map<String, Object> data = DigifactinUtils.parsePostValue(postValue);

        // Execute
        SignCertifiedResponse digifactinResponse = new SignCertifiedResponse();
        try {
            ClientResponse apiResponse = RestAPIClient.doPost(digifactinURL + "/api/signcertified", headers, data);
            LOG.info("Api " + apiResponse.getStatus());
            if (apiResponse != null) {
                ObjectMapper mapper = new ObjectMapper();
                String result = apiResponse.getEntity(String.class);
                if (result.contains("Message")) {
                    SignCertifiedErrorResponse errorResponse = mapper.readValue(result, SignCertifiedErrorResponse.class);
                    return errorResponse;
                } else {
                    digifactinResponse = mapper.readValue(result, SignCertifiedResponse.class);
                }
                return digifactinResponse;
            }
        } catch (IOException e) {
            throw new DigifactinException(e);
        }
        return digifactinResponse;
    }
}
