package org.athento.nuxeo.digifactin.api.client;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Digifact response.
 */
public class SignCertifiedErrorResponse extends DigifactinResponse {

    String Message;

    @JsonProperty("Message")
    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
