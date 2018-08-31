package org.athento.nuxeo.digifactin.api.util;

/**
 * Form data file.
 */
public class FormDataFile {

    String mimetype;
    byte [] content;

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
