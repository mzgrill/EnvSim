package de.htw.saar.env.sim.mqtt;

import de.htw.saar.env.sim.io.IOManager;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


/**
 * Authenticator class used to create SSL Sockets for MQTTClients
 */
public class Authenticator {

    private final String keyStoreEntryName = "cert";
    private String sslProtocol;
    private String path;
    private String certificate;
    private String certificateType;

    public Authenticator(){
        IOManager ioManager = IOManager.getInstance();
        sslProtocol = ioManager.properties.getProperty(IOManager.AUTHENTICATOR_PROTOCOL);
        certificate = ioManager.properties.getProperty(IOManager.AUTHENTICATOR_CERTIFICATE_NAME);
        certificateType = ioManager.properties.getProperty(IOManager.AUTHENTICATOR_CERTIFICATE_TYPE);
        path = IOManager.getInstance().path;
    }

    /**
     * Method to create a new SSLSocketFactory using certificates provided from the IOManager
     * @return SSLSocketFactory containing a keyStore with certificates
     */
    public SSLSocketFactory getSSLSocketFactory() throws Exception{
        CertificateFactory cFactory = CertificateFactory.getInstance(certificateType);
        File file = new File(path + certificate);
        InputStream is = new FileInputStream(file);
        InputStream input = new BufferedInputStream(is);
        Certificate cert;
        try {
            cert = cFactory.generateCertificate(input);
        } finally {
            input.close();
        }
        KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
        store.load(null, null);
        store.setCertificateEntry(keyStoreEntryName, cert);
        TrustManagerFactory tFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tFactory.init(store);
        SSLContext context = SSLContext.getInstance(sslProtocol);
        context.init(null, tFactory.getTrustManagers(), null);
        return context.getSocketFactory();
    }
}
