package org.athento.nuxeo.digifactin.operation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.athento.nuxeo.digifactin.api.client.DigifactinClient;
import org.athento.nuxeo.digifactin.api.client.DigifactinClientImpl;
import org.athento.nuxeo.digifactin.api.client.DigifactinResponse;
import org.athento.nuxeo.digifactin.api.exception.DigifactinException;
import org.athento.nuxeo.digifactin.api.model.PostValue;
import org.athento.nuxeo.digifactin.api.util.FormDataFile;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.*;

import java.io.IOException;

/**
 * Sign certified a document with Digifactin.
 *
 * @author victorsanchez
 */
@Operation(id = SignOperation.ID, category = "Digifactin", label = "Sign certified", description = "Sign certified a document and save",
        since = "8.10", addToStudio = false)
public class SignOperation {

    private static final Log LOG = LogFactory.getLog(SignOperation.class);

    /**
     * Operation ID.
     */
    public static final String ID = "Digifactin.SignCertified";

    @Param(name = "document", description = "Identificador del documento")
    String document;

    @Param(name = "token", description = "Token de autenticación")
    String token;

    @Param(name = "USER", description = "Nombre del usuario")
    String user;

    @Param(name = "FOLDER", required = false, description = "Carpeta para dejar el pdf firmado (opt)")
    String folder;

    @Param(name = "NAME", required = false, description = "Nombre del archivo firmado (opt)")
    String name;

    @Param(name = "CERTIFICATE", required = false, description = "Numero de serie del certificado (opt)")
    String certificate;

    @Param(name = "CERTRUTA", required = false, description = "Ruta al certificado (opt)")
    String certruta;

    @Param(name = "MESDESDE", required = false, description = "Mes desde que empieza el periodo (Solo para XML)")
    Integer mesdesde;

    @Param(name = "ANYODESDE", required = false, description = "Año del periodo (Solo para XML)")
    Integer anyodesde;

    @Param(name = "FV", required = false, description = "Firma Visible")
    boolean fv;

    @Param(name = "IMAGEN", required = false, description = "Ruta a la imagen para colocar junto a la firma (opt)")
    String imagen;

    @Param(name = "PFV", required = false, description = "Firma visible en pagina X")
    Integer pfv;

    @Param(name = "TP", required = false, description = "Texto Personalizado")
    boolean tp;

    @Param(name = "OIVFV", required = false, description = "Ocultar Informacion Validez En Firma Visible ")
    boolean oivfv;

    @Param(name = "UTP", required = false, description = "Texto Personalizado")
    String utp;

    @Param(name = "E", required = false, description = "Encriptar")
    boolean e;

    @Param(name = "EUSER", required = false, description = "Password de encriptacion para usuario")
    String euser;

    @Param(name = "EADMIN", required = false, description = "Password de encriptacion para administrador")
    String eadmin;

    @Param(name = "ST", required = false, description = "Sellado de tiempo")
    boolean st;

    @Param(name = "STURL", required = false, description = "URL de sellado de tiempo")
    String sturl;

    @Param(name = "STUSER", required = false, description = "Usuario para sellado de tiempo")
    String stuser;

    @Param(name = "STPASS", required = false, description = "Contraseña para sellado de tiempo")
    String stpass;

    @Param(name = "PDFA", required = false, description = "Convertir a PDF/A")
    boolean pdfa;

    @Param(name = "IIVPFT", required = false, description = "Incluir Informacion Visible Firma Todas Paginas")
    boolean iivpft;

    @Param(name = "AM", required = false, description = "Añadir Metadatos")
    boolean am;

    @Param(name = "AMENI", required = false, description = "Añadir Metadatos para ENI")
    boolean ameni;

    @Param(name = "FVP", required = false, description = "Firma Visible en Pagina")
    boolean fvp;

    @Param(name = "FVPP", required = false, description = "Firma visible en la primera pagina")
    boolean fvpp;

    @Param(name = "SHA", required = false, description = "Tipo de algoritmo de firma (1, 256 ,512)")
    String sha;

    @Param(name = "CHFV", required = false, description = "Coordenada Horizontal Firma Visible")
    String chfv;

    @Param(name = "CVFV", required = false, description = "Coordenada Vertical Firma Visible")
    String cvfv;

    @Param(name = "ANCHOFV", required = false, description = "Ancho Firma Visible")
    String anchofv;

    @Param(name = "ALTOFV", required = false, description = "Alto Firma Visible")
    String altofv;

    @Param(name = "PTPFV", required = false, description = "Posicion Tamaño Personalizado Firma Visible")
    boolean ptpfv;

    @Param(name = "FVUP", required = false, description = "Firma visible en la ultima pagina")
    boolean fvup;

    @Param(name = "TIPOPERIODO", required = false, description = " 0 mensual, 2 trimestral (Solo para XML)")
    Integer tipoPeriodo;

    /**
     * Param to save document.
     */
    @Param(name = "save", required = false)
    boolean save = true;

    /**
     * Param to debugging.
     */
    @Param(name = "debug", required = false)
    boolean debug = false;

    /**
     * Param to simulate.
     */
    @Param(name = "mockup", required = false)
    boolean mockup = false;

    /** Not send. */
    @Param(name = "ignoreSend", required = false)
    boolean ignoreSend = false;

    /**
     * Context.
     */
    @Context
    protected OperationContext ctx;

    /**
     * Session.
     */
    @Context
    protected CoreSession session;

    /**
     * Run, sign certified into Digifacting server.
     *
     * @return
     * @throws DigifactinException error
     */
    @OperationMethod
    public DocumentModel run(Blob blob) throws DigifactinException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Running SignCertified operation ...");
        }
        // Get document from param
        DocumentRef docRef;
        if (document.startsWith("/")) {
            docRef = new PathRef(document);
        } else {
            docRef = new IdRef(document);
        }
        DocumentModel doc;
        try {
            doc = session.getDocument(docRef);
            DigifactinClient client = new DigifactinClientImpl(session);
            DigifactinResponse response = client.signCertified(token, getPostValue(blob));
            if (response != null) {
                // Save response into document metadata
                LOG.info("Response " + response);
            }
        } catch (DocumentNotFoundException e) {
            throw new DigifactinException("Document is not found: " + document, e);
        } catch (IOException e) {
            throw new DigifactinException("Blob has a problem", e);
        }
        return doc;
    }

    /**
     * Generate a PostValue class from operation params.
     *
     * @param blob is the content
     * @return
     * @throws IOException on blob error
     */
    private PostValue getPostValue(Blob blob) throws IOException {
        PostValue postValue = new PostValue();
        postValue.setUser(user);
        postValue.setCertificate(certificate);
        postValue.setCertruta(certruta);
        postValue.setFolder(folder);
        postValue.setName(name);
        postValue.setSha(sha);
        postValue.setFv(fv);
        postValue.setImagen(imagen);
        postValue.setOivfv(oivfv);
        postValue.setIivftp(iivpft);
        postValue.setAm(am);
        postValue.setAmeni(ameni);
        postValue.setPtpfv(ptpfv);
        postValue.setCvfv(cvfv);
        postValue.setChfv(chfv);
        postValue.setAltofv(altofv);
        postValue.setAnchofv(anchofv);
        postValue.setTp(tp);
        postValue.setUtp(utp);
        postValue.setE(e);
        postValue.setEuser(euser);
        postValue.setEadmin(eadmin);
        postValue.setSt(st);
        postValue.setStuser(stuser);
        postValue.setStpass(stpass);
        postValue.setSturl(sturl);
        postValue.setPdfa(pdfa);
        postValue.setFvp(fvp);
        postValue.setPfv(pfv);
        postValue.setFvpp(fvpp);
        postValue.setFvup(fvup);
        postValue.setMesdesde(mesdesde);
        postValue.setAnyodesde(anyodesde);
        postValue.setTipoperiodo(tipoPeriodo);
        FormDataFile image = new FormDataFile();
        image.setFile(blob.getFile());
        image.setFilename(blob.getFilename());
        image.setMimetype(blob.getMimeType());
        postValue.setUploadedImage(image);
        return postValue;
    }

}
