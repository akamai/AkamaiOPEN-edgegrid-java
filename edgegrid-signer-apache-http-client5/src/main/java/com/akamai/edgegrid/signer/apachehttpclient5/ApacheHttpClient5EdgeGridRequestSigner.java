package com.akamai.edgegrid.signer.apachehttpclient5;

import com.akamai.edgegrid.signer.AbstractEdgeGridRequestSigner;
import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;
import com.akamai.edgegrid.signer.Request;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.io.entity.BufferedHttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Apache HTTP Client5 binding for EdgeGrid signer for signing {@link HttpRequest}.
 */
public class ApacheHttpClient5EdgeGridRequestSigner extends AbstractEdgeGridRequestSigner<HttpRequest, HttpRequest> {

    /**
     * Creates an EdgeGrid signer using {@link ClientCredential}.
     *
     * @param clientCredential a {@link ClientCredential}
     */
    public ApacheHttpClient5EdgeGridRequestSigner(ClientCredential clientCredential) {
        super(clientCredential);
    }

    /**
     * Creates an EdgeGrid signer using {@link ClientCredentialProvider}.
     *
     * @param clientCredentialProvider a {@link ClientCredentialProvider}
     */
    public ApacheHttpClient5EdgeGridRequestSigner(ClientCredentialProvider clientCredentialProvider) {
        super(clientCredentialProvider);
    }

    @Override
    protected URI requestUri(HttpRequest request) {
        return getUri(request);
    }

    @Override
    protected Request map(HttpRequest request) {
        Request.RequestBuilder builder = Request.builder()
                .method(request.getMethod())
                .uri(getUri(request))
                .body(serializeContent(request));
        for (Header h : request.getHeaders()) {
            builder.header(h.getName(), h.getValue());
        }

        return builder.build();
    }

    private URI getUri(HttpRequest request) {
        try {
            return request.getUri();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private byte[] serializeContent(HttpRequest request) {
        if (!(request instanceof HttpEntityContainer)) {
            return new byte[]{};
        }

        var entityWithRequest = (HttpEntityContainer) request;
        var entity = entityWithRequest.getEntity();
        if (entity == null) {
            return new byte[]{};
        }

        try {
            // Buffer non-repeatable entities
            if (!entity.isRepeatable()) {
                entityWithRequest.setEntity(new BufferedHttpEntity(entity));
            }
            return EntityUtils.toByteArray(entityWithRequest.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void setAuthorization(HttpRequest request, String signature) {
        request.setHeader("Authorization", signature);
    }

    @Override
    protected void setHost(HttpRequest request, String host, URI uri) {
        request.setHeader("Host", host);
        setRequestUri(request, uri);
    }

    private void setRequestUri(HttpRequest request, URI uri) {
        // temporary workaround for https://issues.apache.org/jira/browse/HTTPCORE-742
        request.setPath(uri.getPath());
        request.setUri(uri);
    }
}
