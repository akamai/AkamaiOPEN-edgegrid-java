package com.akamai.edgegrid.signer.apachehttpclient5;

import com.akamai.edgegrid.signer.ClientCredential;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;

/**
 * Apache HTTP Client binding for EdgeGrid route planner for computing {@link HttpRoute}.
 *
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public class ApacheHttpClient5EdgeGridRoutePlanner implements HttpRoutePlanner {

    private final ClientCredential clientCredential;

    /**
     * Creates an EdgeGrid route planner using {@link ClientCredential}.
     *
     * @param clientCredential a {@link ClientCredential}
     */
    public ApacheHttpClient5EdgeGridRoutePlanner(ClientCredential clientCredential) {
        this.clientCredential = clientCredential;
    }

    @Override
    public HttpRoute determineRoute(HttpHost target, HttpContext context) throws HttpException {
        var hostname = clientCredential.getHost();
        int port = 443;
        final int pos = hostname.lastIndexOf(":");
        if (pos > 0) {
            try {
                port = Integer.parseInt(hostname.substring(pos + 1));
                if (port <= 0 || port > 65535) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Host contains invalid port number: " + hostname);
            }
            hostname = hostname.substring(0, pos);
        }
        HttpHost host = new HttpHost("https", hostname, port);
        return new HttpRoute(host, null, true);
    }
}


