package eu.h2020.sc.transport;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.config.Globals;
import eu.h2020.sc.domain.Credentials;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.utils.Utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Pietro on 26/09/16.
 */
public abstract class HttpConnectionSetUp {

    protected static HttpURLConnection setUpConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty(HttpConstants.CONNECTION, HttpConstants.CONNECTION_VALUE);
        conn.setRequestProperty(HttpConstants.CACHE_CONTROL, HttpConstants.CACHE_CONTROL_VALUE);
        conn.setUseCaches(false);
        conn.setConnectTimeout(HttpConstants.TIMEOUT_TIME);
        conn.setRequestProperty(HttpConstants.USER_AGENT, String.format("%s %s", Globals.SOCIALCAR_APP_VERSION, Utils.getAndroidVersion()));
        return conn;
    }

    protected static void setCredentials(HttpURLConnection conn) {
        SocialCarStore socialCarStore = SocialCarApplication.getInstance();
        Credentials credentials = socialCarStore.retrieveCredentials();
        conn.setRequestProperty(HttpConstants.AUTHORIZATION, credentials.getBasicAuthenticationCredentials());
    }

}
