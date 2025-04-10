package com.akamai.edgegrid.signer.restassured;


import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;
import com.akamai.edgegrid.signer.EdgeGridV1Signer;
import com.akamai.edgegrid.signer.Request;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * REST-assured filter that signs a request using EdgeGrid V1 signing algorithm. Signing is a
 * process of adding an Authorization header with a request signature. If signing fails then
 * <code>RuntimeException</code> is thrown.
 *
 * @see <a href="https://github.com/rest-assured/rest-assured/wiki/Usage#filters">REST-assured
 * filters</a>
 */
public class RestAssuredEdgeGridFilter implements Filter {

    protected RestAssuredEdgeGridRequestSigner binding;

    /**
     * Creates an EdgeGrid signing interceptor using the same {@link ClientCredential} for each
     * request.
     *
     * @param credential a {@link ClientCredential}
     */
    public RestAssuredEdgeGridFilter(ClientCredential credential) {
        this.binding = new RestAssuredEdgeGridRequestSigner(credential);
    }

    /**
     * Creates an EdgeGrid signing interceptor selecting a {@link ClientCredential} via {@link
     * ClientCredentialProvider#getClientCredential(Request)} for each request.
     *
     * @param clientCredentialProvider a {@link ClientCredentialProvider}
     */
    public RestAssuredEdgeGridFilter(ClientCredentialProvider clientCredentialProvider) {
        this.binding = new RestAssuredEdgeGridRequestSigner(clientCredentialProvider);
    }

    /**
     * Creates a REST-assured filter that will sign a request with a given credential using an
     * {@link EdgeGridV1Signer}.
     *
     * @param credential a client credential to sign a request
     * @return a REST-assured filter to be added to {@link io.restassured.specification.RequestSpecification}
     * definition.
     */
    public static RestAssuredEdgeGridFilter sign(ClientCredential credential) {
        return new RestAssuredEdgeGridFilter(credential);
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        try {
            binding.sign(requestSpec, requestSpec);
        } catch (RequestSigningException e) {
            throw new RuntimeException(e);
        }
        return ctx.next(requestSpec, responseSpec);
    }

}
