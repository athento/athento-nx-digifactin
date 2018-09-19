package org.athento.nuxeo.digifactin.api.util;

import java.io.File;

/**
 * Form data file.
 */
public class FormDataFile {

    String mimetype;
    File file;

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
