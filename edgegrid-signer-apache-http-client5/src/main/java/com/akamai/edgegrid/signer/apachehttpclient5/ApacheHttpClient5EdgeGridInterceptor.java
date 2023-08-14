package com.akamai.edgegrid.signer.apachehttpclient5;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;
import com.akamai.edgegrid.signer.Request;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

/**
 * Apache HTTP Client5 Library interceptor that signs a request using EdgeGrid V1 signing algorithm.
 * Signing is a process of adding an Authorization header with a request signature. If signing fails then <code>RuntimeException</code> is thrown.
 */
public class ApacheHttpClient5EdgeGridInterceptor implements HttpRequestInterceptor {

    private final ApacheHttpClient5EdgeGridRequestSigner binding;

    /**
     * Creates an EdgeGrid signing interceptor using the same {@link ClientCredential} for each
     * request.
     *
     * @param credential a {@link ClientCredential}
     */
    public ApacheHttpClient5EdgeGridInterceptor(ClientCredential credential) {
        this.binding = new ApacheHttpClient5EdgeGridRequestSigner(credential);
    }

    /**
     * Creates an EdgeGrid signing interceptor selecting a {@link ClientCredential} via
     * {@link ClientCredentialProvider#getClientCredential(Request)} for each request.
     *
     * @param clientCredentialProvider a {@link ClientCredentialProvider}
     */
    public ApacheHttpClient5EdgeGridInterceptor(ClientCredentialProvider clientCredentialProvider) {
        this.binding = new ApacheHttpClient5EdgeGridRequestSigner(clientCredentialProvider);
    }

    @Override
    public void process(
            HttpRequest request,
            EntityDetails entityDetails,
            HttpContext httpContext
    ) {
        try {
            binding.sign(request, request);
        } catch (RequestSigningException e) {
            throw new RuntimeException(e);
        }
    }
}
