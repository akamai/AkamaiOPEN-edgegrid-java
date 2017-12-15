package com.akamai.edgegrid.signer.apachehttpclient;



import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;
import com.akamai.edgegrid.signer.exceptions.NoMatchingCredentialException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.protocol.HttpContext;

public class ApacheHttpClientEdgeGridRoutePlanner extends DefaultRoutePlanner {

    private final ApacheHttpClientEdgeGridRequestSigner binding;

    public ApacheHttpClientEdgeGridRoutePlanner(ClientCredential clientCredential) {
        super(DefaultSchemePortResolver.INSTANCE);
        this.binding = new ApacheHttpClientEdgeGridRequestSigner(clientCredential);
    }

    public ApacheHttpClientEdgeGridRoutePlanner(ClientCredentialProvider clientCredentialProvider) {
        super(DefaultSchemePortResolver.INSTANCE);
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
