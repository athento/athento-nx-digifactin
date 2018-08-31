package org.athento.nuxeo.digifactin.api.client;

/**
 * Logout response.
 */
public class LogoutResponse extends DigifactinResponse {

    int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "statusCode=" + status +
                '}';
    }
}
