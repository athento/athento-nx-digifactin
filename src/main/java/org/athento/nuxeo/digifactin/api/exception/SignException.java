package org.athento.nuxeo.digifactin.api.exception;

/**
 * Factura exception.
 */
public class SignException extends DigifactinException {

    /**
     *
     * @param message
     */
    public SignException(String message) {
        super(message, null);
    }

    /**
     *
     * @param message
     * @param code
     */
    public SignException(String message, String code) {
        super(message, code);
    }

    /**
     *
     * @param message
     * @param code
     * @param cause
     */
    public SignException(String message, String code, Throwable cause) {
        super(message, code, cause);
    }

    /**
     *
     * @param cause
     */
    public SignException(Throwable cause) {
        super(cause);
    };

}
