package com.akamai.edgegrid.signer.apachehttpclient5;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class HttpClientSetup {

    public static HttpClientBuilder getHttpClientWithRelaxedSsl() {
        var sslConnectionSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(trustAllCertificates())
                .setHostnameVerifier(trustAllHosts())
                .build();
        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();
        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager);
    }

    private static HostnameVerifier trustAllHosts() {
        return (s, sslSession) -> true;
    }

    private static SSLContext trustAllCertificates() {
        // set up a TrustManager that trusts everything
        try {
            var sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }}, new SecureRandom());
            return sslContext;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
