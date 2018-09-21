package org.athento.nuxeo.digifactin.api.client;

/**
 * Download response.
 */
public class DownloadResponse extends DigifactinResponse {

    String fileContents;
    String fileDownloadName;

    public String getFileContents() {
        return fileContents;
    }

    public void setFileContents(String fileContents) {
        this.fileContents = fileContents;
    }

    public String getFileDownloadName() {
        return fileDownloadName;
    }

    public void setFileDownloadName(String fileDownloadName) {
        this.fileDownloadName = fileDownloadName;
    }
}
