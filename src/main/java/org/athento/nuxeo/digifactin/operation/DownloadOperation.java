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
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;

import java.io.IOException;

/**
 * Download a document from Digifactin.
 *
 * @author victorsanchez
 */
@Operation(id = DownloadOperation.ID, category = "Digifactin", label = "Download signed file", description = "Download a signed file",
        since = "8.10", addToStudio = false)
public class DownloadOperation {

    private static final Log LOG = LogFactory.getLog(DownloadOperation.class);

    /**
     * Operation ID.
     */
    public static final String ID = "Digifactin.Download";

    @Param(name = "INVOICE", description = "Identificador del documento")
    String document;

    @Param(name = "TOKEN", description = "Token de autenticación")
    String token;

    @Param(name = "USER", description = "Nombre del usuario")
    String user;

    @Param(name = "TIPODOCUMENTO", description = "True para Factura y false para gasto.")
    boolean tipoDocumento = true;

    @Param(name = "INVOICE", description = "Ruta del archivo firmado.")
    String invoice;

    /**
     * Session.
     */
    @Context
    protected CoreSession session;

    /**
     * Run, download signed file form Digifacting server.
     *
     * @return
     * @throws DigifactinException error
     */
    @OperationMethod
    public Blob run() throws DigifactinException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Running Download operation ...");
        }
        try {
            DigifactinClient client = new DigifactinClientImpl(session);
            DigifactinResponse response = client.download(token, user, invoice, tipoDocumento);
            if (response != null) {
                LOG.info("Response " + response);
            }
        } catch (DocumentNotFoundException e) {
            throw new DigifactinException("Document is not found: " + document, e);
        }
        return new StringBlob("empty");
    }

}
