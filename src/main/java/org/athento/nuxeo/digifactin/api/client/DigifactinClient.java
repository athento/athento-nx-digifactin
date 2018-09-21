package org.athento.nuxeo.digifactin.api.client;

import org.athento.nuxeo.digifactin.api.exception.DigifactinException;
import org.athento.nuxeo.digifactin.api.model.PostValue;

/**
 * DigifactinClient interface.
 */
public interface DigifactinClient {

    /**
     * Login user.
     *
     * @param clientId passphrase, it is given by Notarnet
     * @param username is the username
     * @param password is the password
     * @return response is the response
     * @throws DigifactinException on login error
     */
    DigifactinResponse loginUser(String clientId, String username, String password) throws DigifactinException;


    /**
     * Logout.
     *
     * @param authToken is the token
     * @return response is the response
     * @throws DigifactinException on logout error
     */
    DigifactinResponse logout(String username, String authToken) throws DigifactinException;

    /**
     * Sing certified.
     *
     * @param authToken is the token
     * @param postValue is the form data value
     * @return response is the response
     * @throws DigifactinException on sign error
     */
    DigifactinResponse signCertified(String authToken, PostValue postValue) throws DigifactinException;

    /**
     * Donwload a signed file.
     *
     * @param token is the token
     * @param user
     * @param invoice is the invoice path
     * @param tipoDocumento is the document type True factura, False Gasto
     * @return
     */
    DigifactinResponse download(String token, String user, String invoice, Boolean tipoDocumento) throws DigifactinException;
}
