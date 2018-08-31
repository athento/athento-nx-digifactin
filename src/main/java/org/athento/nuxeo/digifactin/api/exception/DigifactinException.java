package org.athento.nuxeo.digifactin.api.exception;

import org.nuxeo.ecm.automation.server.jaxrs.RestOperationException;

/**
 * Default Digifactin exception.
 */
public class DigifactinException extends RestOperationException {

    private static final StackTraceElement[] EMPTY_TRACE = new StackTraceElement[] {};

    private static final long serialVersionUID = -57394576980234445L;

    public DigifactinException(String message) {
        super(message);
        DigifactinException.setEmptyStackTrace(this);
    }

    public DigifactinException(String message, Throwable cause) {
        super(message, cause);
        DigifactinException.setEmptyStackTrace(this);
    }

    public DigifactinException(Throwable cause) {
        super(cause);
        DigifactinException.setEmptyStackTrace(cause);
    }

    public static void setEmptyStackTrace(Throwable e) {
        if (e != null) {
            Throwable cause = getRootCause(e);
            e.setStackTrace(DigifactinException.EMPTY_TRACE);
            if (!e.equals(cause)) {
                DigifactinException.setEmptyStackTrace(cause);
            }
        }
    }

    /**
     * Get root cause.
     *
     * @param e
     * @return
     */
    public static Throwable getRootCause(Throwable e) {
        if (e == null)
            return null;
        Throwable cause = e.getCause();
        if (cause != null) {
            return getRootCause(cause);
        }
        return e;
    }


}
