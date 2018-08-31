package org.athento.nuxeo.digifactin.api.client;

/**
 * Login response.
 */
public class LoginUserResponse extends DigifactinResponse {

    String auth_token;
    String username;
    String cExplotacion;
    String folder;
    String perms;

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getcExplotacion() {
        return cExplotacion;
    }

    public void setcExplotacion(String cExplotacion) {
        this.cExplotacion = cExplotacion;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    @Override
    public String toString() {
        return "LoginUserResponse{" +
                "statusCode=" + status +
                "authToken='" + auth_token + '\'' +
                ", username='" + username + '\'' +
                ", folder='" + folder + '\'' +
                ", cExplotacion='" + cExplotacion + '\'' +
                '}';
    }
}
