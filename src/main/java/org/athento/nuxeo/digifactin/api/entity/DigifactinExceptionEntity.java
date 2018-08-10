package org.athento.nuxeo.digifactin.api.entity;

/**
 * Quiter exception entity.
 */
public class DigifactinExceptionEntity {

    String message;
    String code;

    public DigifactinExceptionEntity(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
