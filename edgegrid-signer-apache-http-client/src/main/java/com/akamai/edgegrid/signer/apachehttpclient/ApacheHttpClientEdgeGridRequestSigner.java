package com.akamai.edgegrid.signer.apachehttpclient;

import com.akamai.edgegrid.signer.AbstractEdgeGridRequestSigner;
import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;
import com.akamai.edgegrid.signer.Request;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Apache HTTP Client binding for EdgeGrid signer for signing {@link HttpRequest}.
 *
 * @author mgawinec@akamai.com
 */
public class ApacheHttpClientEdgeGridRequestSigner extends AbstractEdgeGridRequestSigner<HttpRequest> {

    public ApacheHttpClientEdgeGridRequestSigner(ClientCredential clientCredential) {
        super(clientCredential);
    }

    public ApacheHttpClientEdgeGridRequestSigner(ClientCredentialProvider clientCredentialProvider) {
        super(clientCredentialProvider);
    }

    @Override
    protected Request map(HttpRequest request) {
        Request.RequestBuilder builder = Request.builder()
                .method(request.getRequestLine().getMethod())
                .uri(request.getRequestLine().getUri())
                .body(serializeContent(request));
        for (Header h : request.getAllHeaders()) {
            builder.header(h.getName(), h.getValue());
        }

        return builder.build();
    }

    private byte[] serializeContent(HttpRequest request) {
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            return new byte[]{};
        }

        final HttpEntityEnclosingRequest entityWithRequest = (HttpEntityEnclosingRequest) request;
        HttpEntity entity = entityWithRequest.getEntity();
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
    protected void setHost(HttpRequest request, String host) {
        request.setHeader("Host", host);

        if (request instanceof HttpRequestWrapper) {
            setHost(((HttpRequestWrapper) request).getOriginal(), host);
        } else if (request instanceof RequestWrapper) {
            setHost(((RequestWrapper) request).getOriginal(), host);
        } else {
            URI oldUri = ((HttpRequestBase) request).getURI();
            try {
                URI newUri = replaceHost(oldUri, host);
                ((HttpRequestBase) request).setURI(newUri);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static URI replaceHost(URI oldUri, String host) throws URISyntaxException {
        return new URIBuilder()
                .setScheme(oldUri.getScheme())
                .setHost(host)
                .setPort(oldUri.getPort())
                .setPath(oldUri.getPath())
                .setParameters(URLEncodedUtils.parse(oldUri, "UTF-8"))
                .setFragment(oldUri.getFragment())
                .build();
    }

}
