package com.akamai.edgegrid.signer.apachehttpclient;

import java.net.ProxySelector;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;
import com.akamai.edgegrid.signer.exceptions.NoMatchingCredentialException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.protocol.HttpContext;
/**
 * Apache HTTP Client binding for EdgeGrid route planner for computing {@link HttpRoute}.
 *
 */
public class ApacheHttpClientEdgeGridRoutePlanner extends SystemDefaultRoutePlanner {

    private final ApacheHttpClientEdgeGridRequestSigner binding;

    /**
     * Creates an EdgeGrid route planner using {@link ClientCredential}.
     *
     * @param clientCredential a {@link ClientCredential}
     */
    public ApacheHttpClientEdgeGridRoutePlanner(ClientCredential clientCredential) {
        this(clientCredential, ProxySelector.getDefault());
    }

    /**
     * Creates an EdgeGrid route planner using {@link ClientCredential}.
     *
     * @param clientCredential a {@link ClientCredential}
     * @param proxySelector a {@link ProxySelector}
     */
    public ApacheHttpClientEdgeGridRoutePlanner(ClientCredential clientCredential, ProxySelector proxySelector) {
        super(proxySelector);
        this.binding = new ApacheHttpClientEdgeGridRequestSigner(clientCredential);
    }

    /**
     * Creates an EdgeGrid route planner using {@link ClientCredentialProvider}.
     *
     * @param clientCredentialProvider a {@link ClientCredentialProvider}
     */
    public ApacheHttpClientEdgeGridRoutePlanner(ClientCredentialProvider clientCredentialProvider) {
        this(clientCredentialProvider, ProxySelector.getDefault());
    }

    /**
     * Creates an EdgeGrid route planner using {@link ClientCredentialProvider}.
     *
     * @param clientCredentialProvider a {@link ClientCredentialProvider}
     * @param proxySelector a {@link ProxySelector}
     */
    public ApacheHttpClientEdgeGridRoutePlanner(ClientCredentialProvider clientCredentialProvider, ProxySelector proxySelector) {
        super(proxySelector);
        this.binding = new ApacheHttpClientEdgeGridRequestSigner(clientCredentialProvider);
    }

    @Override
    public HttpRoute determineRoute(HttpHost host, HttpRequest request, HttpContext context) throws HttpException {
        try {
            ClientCredential clientCredential = binding.getClientCredentialProvider().getClientCredential(binding.map(request));
            String hostname = clientCredential.getHost();
            int port = -1;
            final int pos = hostname.lastIndexOf(":");
            if (pos > 0) {
                try {
                    port = Integer.parseInt(hostname.substring(pos + 1));
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Host contains invalid port number: " + hostname);
                }
                hostname = hostname.substring(0, pos);
            }
            HttpHost target = new HttpHost(hostname, port, "https");
            return super.determineRoute(target, request, context);
        } catch (NoMatchingCredentialException e) {
            throw new RuntimeException(e);
        }
    }
}
