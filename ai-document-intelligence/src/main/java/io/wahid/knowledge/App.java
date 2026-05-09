package io.wahid.knowledge;

import com.nimbusds.jose.util.DefaultResourceRetriever;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, URISyntaxException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        System.out.println( "Hello World!" );
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        InputStream jksInput = App.class.getResourceAsStream("truststore/keycloak-truststore-1.jks");

        InputStream caInput = App.class.getResourceAsStream("/keycloak.crt");

        X509Certificate caCert = (X509Certificate) cf.generateCertificate(caInput);

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("keycloak-jwk", caCert);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

// Configure Nimbus to use this SSLContext
        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever(2000, 2000);
//        resourceRetriever.setSSLContext(sslContext);

        System.out.println("finish");
    }
}
