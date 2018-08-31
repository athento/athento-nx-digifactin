package org.athento.nuxeo.digifactin.api.client;

/**
 * Status code.
 */
public interface StatusCode {

    int FIRMADO_CORRECTAMENTE = 200;
    int ERROR_CON_MENSAJE = 400;
    int ERROR_GENERICO = 501;
    int UNAUTHORIZED = 401;

    int USUARIO_O_PASSWORD_INCORRECTO = 404;
    int CLIENT_ID_INCORRECTO = 305;
    int LOGIN_OK = 200;


}
