package com.akamai.edgegrid.signer.apachehttpclient;


import org.apache.http.impl.client.HttpClientBuilder;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpClientSetup {


    public static HttpClientBuilder getHttpClientWithRelaxedSsl() {
        return HttpClientBuilder.create()
                .setSSLContext(trustAllCertificates())
                .setSSLHostnameVerifier(trustAllHosts());
    }

    private static HostnameVerifier trustAllHosts() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
    }

    private static SSLContext trustAllCertificates() {
        // set up a TrustManager that trusts everything
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs,
                                               String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs,
                                               String authType) {
                }
            }}, new SecureRandom());
            return sslContext;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
