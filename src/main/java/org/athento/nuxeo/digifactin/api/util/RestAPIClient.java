package org.athento.nuxeo.digifactin.api.util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.impl.provider.entity.FileProvider;
import com.sun.jersey.core.impl.provider.entity.StringProvider;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Generic Rest API Client.
 *
 * Based on Jersey client.
 */
public final class RestAPIClient {

    /** Timeout. */
    private static final Integer DEFAULT_TIMEOUT = 1000 * 60 * 5; // 5min

    /** Log. */
    private static final Log LOG = LogFactory.getLog(RestAPIClient.class);

    /**
     * Do post. Using multipart-formadata content-type.
     *
     * @param url
     * @param headers
     * @param formData
     * @throws IOException on error
     */
    public static ClientResponse doPost(String url, Map<String, Object> headers, Map<String, Object> formData) throws IOException {
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(MultiPartWriter.class);
        config.getClasses().add(StringProvider.class);
        config.getClasses().add(FileProvider.class);
        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                new HTTPSProperties(null, getSSLContext()));
        Client client = Client.create(config);
        client.setConnectTimeout(DEFAULT_TIMEOUT);
        client.setReadTimeout(DEFAULT_TIMEOUT);
        // Add form data parts
        FormDataMultiPart formMultipart = new FormDataMultiPart();
        for (Map.Entry<String, Object> data : formData.entrySet()) {
            Object value = data.getValue();
            if (value instanceof FormDataFile) {
                FileDataBodyPart bodyFile = new FileDataBodyPart(((FormDataFile) value).getFile().getName(), ((FormDataFile) value).getFile());
                formMultipart.bodyPart(bodyFile);
            } else {
                formMultipart.field(data.getKey(), String.valueOf(value));
            }
        }
        WebResource wr = client.resource(url);
        WebResource.Builder invocationBuilder = wr.type(MediaType.MULTIPART_FORM_DATA_TYPE);
        // Add headers
        for (Map.Entry<String, Object> header : headers.entrySet()) {
            invocationBuilder.header(header.getKey(), header.getValue());
        }
        return invocationBuilder.post(ClientResponse.class, formMultipart);
    }

    /**
     * Do post. Using json content-type.
     *
     * @param url
     * @param headers
     * @param jsonData
     * @throws IOException on error
     */
    public static ClientResponse doPost(String url, Map<String, Object> headers, String jsonData) throws IOException {
        ClientConfig config = new DefaultClientConfig();
        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                new HTTPSProperties(null, getSSLContext()));
        Client client = Client.create(config);
        client.setConnectTimeout(DEFAULT_TIMEOUT);
        client.setReadTimeout(DEFAULT_TIMEOUT);
        WebResource wr = client.resource(url);
        WebResource.Builder invocationBuilder = wr.accept(MediaType.APPLICATION_JSON);
        wr.type(MediaType.APPLICATION_JSON_TYPE);
        // Add headers
        for (Map.Entry<String, Object> header : headers.entrySet()) {
            invocationBuilder.header(header.getKey(), header.getValue());
        }
        LOG.info("DoPost " + url);
        return invocationBuilder.post(ClientResponse.class, jsonData);
    }

    /**
     * Get SSL Context.
     *
     * @return
     */
    private static SSLContext getSSLContext() {
        try {
            TrustManager[] noopTrustManager = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sc = SSLContext.getInstance("ssl");
            sc.init(null, noopTrustManager, null);
            return sc;
        } catch (Exception e) {
            return null;
        }
    }
}
