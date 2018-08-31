package org.athento.nuxeo.digifactin.api.client;

/**
 * Digifacting response.
 */
public abstract class DigifactinResponse {

    String $id;

    public String get$id() {
        return $id;
    }

    public void set$id(String $id) {
        this.$id = $id;
    }
}
