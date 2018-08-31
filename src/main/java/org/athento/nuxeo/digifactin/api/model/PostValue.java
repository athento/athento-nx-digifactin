package org.athento.nuxeo.digifactin.api.model;

import org.athento.nuxeo.digifactin.api.util.FormDataFile;

/**
 * PostValue class.
 *
 * - Used in SignCertified.
 */
public class PostValue {

    String user;
    String certificate;
    String certruta;
    String folder;
    String name;
    String sha;
    boolean fv;
    String imagen;
    boolean oivfv;
    boolean iivftp;
    boolean am;
    boolean ameni;
    boolean ptpfv;
    String cvfv;
    String chfv;
    String altofv;
    String anchofv;
    boolean tp;
    String utp;
    boolean e;
    String euser;
    String eadmin;
    boolean st;
    String stuser;
    String stpass;
    String sturl;
    boolean pdfa;
    boolean fvp;
    int pfv;
    boolean fvpp;
    boolean fvup;
    int mesdesde;
    int anyiodesde;
    int tipoperiodo;
    FormDataFile uploadedImage;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getCertruta() {
        return certruta;
    }

    public void setCertruta(String certruta) {
        this.certruta = certruta;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public boolean isFv() {
        return fv;
    }

    public void setFv(boolean fv) {
        this.fv = fv;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public boolean isOivfv() {
        return oivfv;
    }

    public void setOivfv(boolean oivfv) {
        this.oivfv = oivfv;
    }

    public boolean isIivftp() {
        return iivftp;
    }

    public void setIivftp(boolean iivftp) {
        this.iivftp = iivftp;
    }

    public boolean isAm() {
        return am;
    }

    public void setAm(boolean am) {
        this.am = am;
    }

    public boolean isAmeni() {
        return ameni;
    }

    public void setAmeni(boolean ameni) {
        this.ameni = ameni;
    }

    public boolean isPtpfv() {
        return ptpfv;
    }

    public void setPtpfv(boolean ptpfv) {
        this.ptpfv = ptpfv;
    }

    public String getCvfv() {
        return cvfv;
    }

    public void setCvfv(String cvfv) {
        this.cvfv = cvfv;
    }

    public String getChfv() {
        return chfv;
    }

    public void setChfv(String chfv) {
        this.chfv = chfv;
    }

    public String getAltofv() {
        return altofv;
    }

    public void setAltofv(String altofv) {
        this.altofv = altofv;
    }

    public String getAnchofv() {
        return anchofv;
    }

    public void setAnchofv(String anchofv) {
        this.anchofv = anchofv;
    }

    public boolean isTp() {
        return tp;
    }

    public void setTp(boolean tp) {
        this.tp = tp;
    }

    public String getUtp() {
        return utp;
    }

    public void setUtp(String utp) {
        this.utp = utp;
    }

    public boolean isE() {
        return e;
    }

    public void setE(boolean e) {
        this.e = e;
    }

    public String getEuser() {
        return euser;
    }

    public void setEuser(String euser) {
        this.euser = euser;
    }

    public String getEadmin() {
        return eadmin;
    }

    public void setEadmin(String eadmin) {
        this.eadmin = eadmin;
    }

    public boolean isSt() {
        return st;
    }

    public void setSt(boolean st) {
        this.st = st;
    }

    public String getStuser() {
        return stuser;
    }

    public void setStuser(String stuser) {
        this.stuser = stuser;
    }

    public String getStpass() {
        return stpass;
    }

    public void setStpass(String stpass) {
        this.stpass = stpass;
    }

    public String getSturl() {
        return sturl;
    }

    public void setSturl(String sturl) {
        this.sturl = sturl;
    }

    public boolean isPdfa() {
        return pdfa;
    }

    public void setPdfa(boolean pdfa) {
        this.pdfa = pdfa;
    }

    public boolean isFvp() {
        return fvp;
    }

    public void setFvp(boolean fvp) {
        this.fvp = fvp;
    }

    public int getPfv() {
        return pfv;
    }

    public void setPfv(int pfv) {
        this.pfv = pfv;
    }

    public boolean isFvpp() {
        return fvpp;
    }

    public void setFvpp(boolean fvpp) {
        this.fvpp = fvpp;
    }

    public boolean isFvup() {
        return fvup;
    }

    public void setFvup(boolean fvup) {
        this.fvup = fvup;
    }

    public int getMesdesde() {
        return mesdesde;
    }

    public void setMesdesde(int mesdesde) {
        this.mesdesde = mesdesde;
    }

    public int getAnyiodesde() {
        return anyiodesde;
    }

    public void setAnyiodesde(int anyiodesde) {
        this.anyiodesde = anyiodesde;
    }

    public int getTipoperiodo() {
        return tipoperiodo;
    }

    public void setTipoperiodo(int tipoperiodo) {
        this.tipoperiodo = tipoperiodo;
    }

    public FormDataFile getUploadedImage() {
        return uploadedImage;
    }

    public void setUploadedImage(FormDataFile uploadedImage) {
        this.uploadedImage = uploadedImage;
    }
}
