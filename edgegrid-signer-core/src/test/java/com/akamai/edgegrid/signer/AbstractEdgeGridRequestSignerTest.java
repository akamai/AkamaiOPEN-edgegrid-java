package com.akamai.edgegrid.signer;

import com.akamai.edgegrid.signer.exceptions.NoMatchingCredentialException;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;

import org.testng.annotations.Test;

import java.net.URI;

/**
 * Unit tests for {@link AbstractEdgeGridRequestSigner}.
 *
 */

public class AbstractEdgeGridRequestSignerTest {

    @Test(expectedExceptions = NoMatchingCredentialException.class)
    public void shouldTerminateSigningForMissingCredential() throws RequestSigningException {
        AbstractEdgeGridRequestSigner mockedSigner = mockedSigner(new ClientCredentialProvider() {
            @Override
            public ClientCredential getClientCredential(Request request) throws NoMatchingCredentialException {
                throw new NoMatchingCredentialException();
            }
        });
        mockedSigner.sign(new Object(), new Object());
    }

    @Test(expectedExceptions = NoMatchingCredentialException.class)
    public void shouldTerminateSigningForNullCredential() throws RequestSigningException {
        AbstractEdgeGridRequestSigner mockedSigner = mockedSigner(new ClientCredentialProvider() {
            @Override
            public ClientCredential getClientCredential(Request request) throws NoMatchingCredentialException {
                return null;
            }
        });
        mockedSigner.sign(new Object(), new Object());
    }

    @Test
    public void shouldNotTerminateSigningForValidCredentialAndRequest() throws RequestSigningException {
        AbstractEdgeGridRequestSigner mockedSigner = mockedSigner(new ClientCredentialProvider() {
            @Override
            public ClientCredential getClientCredential(Request request) throws NoMatchingCredentialException {
                return ClientCredential.builder()
                    .accessToken("accessToken")
                    .clientSecret("clientSecret")
                    .clientToken("clientToken")
                    .host("host")
                    .build();
            }
        });
        mockedSigner.sign(new Object(), new Object());
    }

    public AbstractEdgeGridRequestSigner mockedSigner(ClientCredentialProvider clientCredentialProvider) {

        return new AbstractEdgeGridRequestSigner(clientCredentialProvider) {


            @Override
            protected URI requestUri(Object request) {
                return URI.create("http://request/test");
            }

            @Override
            protected Request map(Object request) {
                return Request.builder()
                    .method("GET")
                    .uri("http://request/test/")
                    .body("".getBytes())
                    .build();
            }

            @Override
            protected void setAuthorization(Object request, String signature) {
            }

            @Override
            protected void setHost(Object request, String host, URI uri) {
            }

        };
    }

}
