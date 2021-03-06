package org.athento.nuxeo.digifactin.api.client;

import com.sun.jersey.api.client.ClientResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.athento.nuxeo.digifactin.api.exception.DigifactinException;
import org.athento.nuxeo.digifactin.api.model.PostValue;
import org.athento.nuxeo.digifactin.api.util.DigifactinUtils;
import org.athento.nuxeo.digifactin.api.util.FormDataFile;
import org.athento.nuxeo.digifactin.api.util.RestAPIClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;

import java.io.IOException;
import java.text.ParseException;
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
    private CoreSession session;

    /**
     * Constructor.
     */
    public DigifactinClientImpl(CoreSession session) {
        this.session = session;
        // Get Digifactin API URL
        digifactinURL = (String) DigifactinUtils.readConfigValue(this.session, "digifactinconfig:url");
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
        LogoutResponse digifactinResponse = new LogoutResponse();
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

        LOG.info("Signing filename: " + postValue.getUploadedImage().getFilename());

        // Headers
        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", postValue.getUser() + " " + authToken);

        // Form data
        Map<String, Object> data = DigifactinUtils.parsePostValue(postValue);

        // Execute
        SignCertifiedResponse digifactinResponse = new SignCertifiedResponse();
        try {
            LOG.info("Map data:" + data);
            LOG.info("File: " + ((FormDataFile) data.get("UploadedImage")).getFilename() +
                    ", " + ((FormDataFile) data.get("UploadedImage")).getFile().getAbsolutePath());
            ClientResponse apiResponse = RestAPIClient.doPost(digifactinURL + "/api/signcertified", headers, data);
            if (apiResponse != null) {
                ObjectMapper mapper = new ObjectMapper();
                String result = apiResponse.getEntity(String.class);
                LOG.info("Response: " + result);
                digifactinResponse.setStatusCode(apiResponse.getStatus());
                if (apiResponse.getStatus() == StatusCode.UNAUTHORIZED) {
                    digifactinResponse.setsError("Unauthorized");
                } else if (apiResponse.getStatus() == StatusCode.ERROR_CON_MENSAJE) {
                    ErrorMessageResponse tmpMsg = mapper.readValue(result, ErrorMessageResponse.class);
                    digifactinResponse.setsError(tmpMsg.getMessage());
                } else if (apiResponse.getStatus() == StatusCode.FIRMADO_CORRECTAMENTE) {
                    digifactinResponse = mapper.readValue(result, SignCertifiedResponse.class);
                } else {
                    digifactinResponse.setsError("Generic error");
                }
                return digifactinResponse;
            }
        } catch (IOException | ParseException e) {
            throw new DigifactinException(e);
        }
        return digifactinResponse;
    }

    /**
     * Sing certified.
     *
     * @param authToken
     * @param user
     * @param invoice
     * @param tipoDocumento
     * @throws DigifactinException on error
     * @return
     */
    @Override
    public DigifactinResponse download(String authToken, String user, String invoice, Boolean tipoDocumento) throws DigifactinException {
        if (invoice == null) {
            throw new DigifactinException("Invoice must be not null");
        }

        if (digifactinURL == null) {
            throw new DigifactinException("Please check the Digifactin API url in the configuration file");
        }

        if (tipoDocumento == null) {
            tipoDocumento = Boolean.TRUE;
        }
        LOG.info("Downloading file " + invoice + ", " + tipoDocumento);
        // Headers
        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", user + " " + authToken);

        // Form data
        Map<String, Object> data = new HashMap<>();
        data.put("INVOICE", invoice);
        data.put("TIPODOCUMENTO", tipoDocumento);
        data.put("USER", user);


        // Execute
        DownloadResponse digifactinResponse = new DownloadResponse();
        try {
            ClientResponse apiResponse = RestAPIClient.doPost(digifactinURL + "/api/invoice", headers, data);
            if (apiResponse != null) {
                digifactinResponse.setStatus(apiResponse.getStatus());
                FileBlob signedFile = new FileBlob(apiResponse.getEntityInputStream());
                if (LOG.isInfoEnabled()) {
                    LOG.info("Response: " + signedFile.getFile().getAbsolutePath());
                }
                digifactinResponse.setSignedFile(signedFile.getFile());
                return digifactinResponse;
            }
        } catch (IOException | ParseException e) {
            throw new DigifactinException(e);
        }
        return digifactinResponse;
    }
}
