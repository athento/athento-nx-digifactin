package org.athento.nuxeo.digifactin.api.client;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Digifact response.
 */
public class SignCertifiedResponse extends DigifactinResponse {

    String folder;
    String nPages;
    String sError;
    int elapsed_time;

    @JsonProperty("statusCode")
    @Override
    public int getStatus() {
        return super.getStatus();
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getnPages() {
        return nPages;
    }

    public void setnPages(String nPages) {
        this.nPages = nPages;
    }

    public String getsError() {
        return sError;
    }

    public void setsError(String sError) {
        this.sError = sError;
    }

    public int getElapsed_time() {
        return elapsed_time;
    }

    public void setElapsed_time(int elapsed_time) {
        this.elapsed_time = elapsed_time;
    }

    @Override
    public String toString() {
        return "SignCertifiedResponse{" +
                "statusCode=" + status +
                ", folder='" + folder + '\'' +
                ", nPages='" + nPages + '\'' +
                ", sError='" + sError + '\'' +
                ", elapsedTime=" + elapsed_time +
                '}';
    }
}
