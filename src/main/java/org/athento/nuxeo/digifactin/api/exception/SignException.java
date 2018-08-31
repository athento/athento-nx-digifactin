package org.athento.nuxeo.digifactin.api.exception;

/**
 * Sign exception.
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
        super(message);
    }

    /**
     *
     * @param cause
     */
    public SignException(Throwable cause) {
        super(cause);
    };

}
