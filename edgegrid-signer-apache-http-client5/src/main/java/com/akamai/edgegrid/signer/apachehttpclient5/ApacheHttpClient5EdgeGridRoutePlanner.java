package com.akamai.edgegrid.signer.apachehttpclient5;

import com.akamai.edgegrid.signer.ClientCredential;
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.net.ProxySelector;

public class ApacheHttpClient5EdgeGridRoutePlanner extends SystemDefaultRoutePlanner {

    private final ClientCredential clientCredential;

    public ApacheHttpClient5EdgeGridRoutePlanner(ClientCredential clientCredential) {
        super(ProxySelector.getDefault());
        this.clientCredential = clientCredential;
    }

    @Override
    protected HttpHost determineProxy(HttpHost target, HttpContext context) {
        var hostname = clientCredential.getHost();
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
        return new HttpHost("https", hostname, port);
    }
}
