package eu.h2020.sc.transport;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.config.Globals;
import eu.h2020.sc.domain.Credentials;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.utils.Utils;

/**
 * Created by khairul.alam on 29/08/17.
 */

public abstract class HttpsConnectionSetUp {

    protected static HttpsURLConnection setUpConnection(URL url) throws IOException {

        try {
            return getHttpsURLConnection(url);

        } catch (Exception ex) {
            Log.e("", "Failed to establish SSL connection to server: " + ex.toString());
            return null;
        }

    }

    private static HttpsURLConnection getHttpsURLConnection(URL url) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestProperty(HttpConstants.CONNECTION, HttpConstants.CONNECTION_VALUE);
        urlConnection.setRequestProperty(HttpConstants.CACHE_CONTROL, HttpConstants.CACHE_CONTROL_VALUE);
        urlConnection.setUseCaches(false);
        urlConnection.setConnectTimeout(HttpConstants.TIMEOUT_TIME);
        urlConnection.setRequestProperty(HttpConstants.USER_AGENT, String.format("%s %s", Globals.SOCIALCAR_APP_VERSION, Utils.getAndroidVersion()));

        setCredentials(urlConnection);

        return urlConnection;
    }

    private static HttpsURLConnection getHttpsURLConnectionSelfSigned(URL url) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        InputStream caInput = new BufferedInputStream(SocialCarApplication.getContext().getAssets().open("socialcar-project.eu.cer"));
        Certificate ca = cf.generateCertificate(caInput);
        Log.i("HttpsConnectionSetUp", "ca=" + ((X509Certificate) ca).getSubjectDN());

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, tmf.getTrustManagers(), null);

        // Tell the URLConnection to use a SocketFactory from our SSLContext
        //URL url = new URL(urlString);


        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setSSLSocketFactory(context.getSocketFactory());
        urlConnection.setRequestProperty(HttpConstants.CONNECTION, HttpConstants.CONNECTION_VALUE);
        urlConnection.setRequestProperty(HttpConstants.CACHE_CONTROL, HttpConstants.CACHE_CONTROL_VALUE);
        urlConnection.setUseCaches(false);
        urlConnection.setConnectTimeout(HttpConstants.TIMEOUT_TIME);
        urlConnection.setRequestProperty(HttpConstants.USER_AGENT, String.format("%s %s", Globals.SOCIALCAR_APP_VERSION, Utils.getAndroidVersion()));

        setCredentials(urlConnection);

        return urlConnection;
    }


    protected static void setCredentials(HttpsURLConnection conn) {
        SocialCarStore socialCarStore = SocialCarApplication.getInstance();
        Credentials credentials = socialCarStore.retrieveCredentials();
        conn.setRequestProperty(HttpConstants.AUTHORIZATION, credentials.getBasicAuthenticationCredentials());
    }
}


