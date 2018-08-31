package org.athento.nuxeo.digifactin.api.client;

/**
 * Digifacting response.
 */
public abstract class DigifactinResponse {

    String $id;

    /** Based on {@link org.athento.nuxeo.digifactin.api.client.StatusCode} */
    int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String get$id() {
        return $id;
    }

    public void set$id(String $id) {
        this.$id = $id;
    }
}
