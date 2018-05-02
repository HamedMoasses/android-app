package eu.h2020.sc.domain;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;

import eu.h2020.sc.config.Globals;

import java.io.UnsupportedEncodingException;

/**
 * Created by fminori on 13/06/16.
 */
public class Credentials {

    private String email;
    private String password;


    public Credentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getBasicAuthenticationCredentials() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(email).append(":").append(password);
            byte[] data = sb.toString().getBytes("UTF-8");
            String encodedUsernamePassword = Base64.encodeToString(data, Base64.DEFAULT);
            return "Basic " + encodedUsernamePassword;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Fatal exception, UTF-8 not supported...", e);
        }

    }
    
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isPasswordTooShort() {
        return (this.password.length() < Globals.PASSWORD_LENGTH);
    }

    public static boolean isEmailValid(String vEmail) {
        return !TextUtils.isEmpty(vEmail) && Patterns.EMAIL_ADDRESS.matcher(vEmail).matches();
    }

}
