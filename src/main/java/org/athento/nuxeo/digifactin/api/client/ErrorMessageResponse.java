package org.athento.nuxeo.digifactin.api.client;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Digifact error with message response.
 */
public class ErrorMessageResponse extends DigifactinResponse {

    @JsonProperty("Message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
