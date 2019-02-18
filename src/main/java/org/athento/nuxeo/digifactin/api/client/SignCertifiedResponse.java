package org.athento.nuxeo.digifactin.api.client;

/**
 * Sign certified response.
 */
public class SignCertifiedResponse extends DigifactinResponse {

    String folder;
    String nPages;
    String sError;
    String nPagesTotal;
    String nPagesMax;

    int elapsed_time;
    /** Based on {@link org.athento.nuxeo.digifactin.api.client.StatusCode} */
    int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
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

    public String getnPagesTotal() {
        return nPagesTotal;
    }

    public void setnPagesTotal(String nPagesTotal) {
        this.nPagesTotal = nPagesTotal;
    }

    public String getnPagesMax() {
        return nPagesMax;
    }

    public void setnPagesMax(String nPagesMax) {
        this.nPagesMax = nPagesMax;
    }

    @Override
    public String toString() {
        return "SignCertifiedResponse{" +
                "statusCode=" + statusCode +
                ", folder='" + folder + '\'' +
                ", nPages='" + nPages + '\'' +
                ", sError='" + sError + '\'' +
                ", elapsedTime=" + elapsed_time +
                '}';
    }
}
