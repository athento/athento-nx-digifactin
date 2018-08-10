package org.athento.nuxeo.digifactin.operation.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.athento.nuxeo.digifactin.api.entity.DigifactinExceptionEntity;
import org.athento.nuxeo.digifactin.api.exception.DigifactinException;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.jaxrs.io.operations.RestOperationContext;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Digifactin exception.
 */
@Operation(id = DigifactinExceptionOperation.ID, category = "Digifactin",
        label = "Digifactin Exception", description = "Throws an Digifactin Exception")
public class DigifactinExceptionOperation {

    private static final Log LOG = LogFactory
            .getLog(DigifactinExceptionOperation.class);

    public static final String ID = "Digifactin.Exception";

    @Context
    protected OperationContext ctx;

    @Context
    protected CoreSession session;

    @OperationMethod()
    public DigifactinExceptionEntity run() throws OperationException {
        DigifactinExceptionEntity retVal = null;
        Object excObject = ctx.get("exceptionObject");
        int returnCode = 500;
        if (excObject instanceof DigifactinException) {
            retVal = new DigifactinExceptionEntity(((DigifactinException) excObject).getMessage(),
                    ((DigifactinException) excObject).getCode());
        } else if (excObject instanceof Throwable) {
            Throwable t = (Throwable) excObject;
            retVal = new DigifactinExceptionEntity(t.getMessage(), "500");
        } else {
            retVal = new DigifactinExceptionEntity("Error in Digifactin operation", "500");
        }
        ((RestOperationContext) ctx).setHttpStatus(returnCode);
        return retVal;
    }

}