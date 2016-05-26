package com.rsa.asoc.sa.ui.common.web.client;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;

/**
 * Builder to create a {@link CloseableHttpClient} that allows connecting to Web servers with self-signed SSL
 * certificate and/or does not verify the host name on the SSL certificate.
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public final class SslSupportingHttpClientBuilder {

    private HttpClientBuilder clientBuilder;

    private SslSupportingHttpClientBuilder() {

        clientBuilder = HttpClients.custom();
    }

    public static SslSupportingHttpClientBuilder create() {
        return new SslSupportingHttpClientBuilder();
    }

    public SslSupportingHttpClientBuilder trustSelfSignedSslCerts() {
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
            clientBuilder.setSSLContext(sslContext);
        } catch (Exception exception) {
            throw new RuntimeException( exception );
        }
        return this;
    }

    public SslSupportingHttpClientBuilder doNotVerifyHostname() {
        clientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        return this;
    }

    public CloseableHttpClient build() {
        return clientBuilder.build();
    }
}
