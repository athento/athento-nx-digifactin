package org.athento.nuxeo.digifactin.api.client;

import java.io.File;

/**
 * Download response.
 */
public class DownloadResponse extends DigifactinResponse {

    File signedFile;
    int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public File getSignedFile() {
        return signedFile;
    }

    public void setSignedFile(File signedFile) {
        this.signedFile = signedFile;
    }
}
