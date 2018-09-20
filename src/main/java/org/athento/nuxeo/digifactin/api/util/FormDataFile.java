package org.athento.nuxeo.digifactin.api.util;

import java.io.File;

/**
 * Form data file.
 */
public class FormDataFile {

    String filename;
    String mimetype;
    File file;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

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
