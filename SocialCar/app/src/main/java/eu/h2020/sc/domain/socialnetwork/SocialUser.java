package eu.h2020.sc.domain.socialnetwork;

import eu.h2020.sc.domain.SocialProvider;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.domain.UserGender;

/**
 * Created by Pietro Ditta on 18/01/2017.
 */

public class SocialUser {

    private String name;

    private String email;

    private String gender;

    private String photoUrl;

    private SocialProvider socialProvider;


    public SocialUser(String name, String photoUrl, SocialProvider socialProvider) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.socialProvider = socialProvider;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public SocialProvider getSocialProvider() {
        return socialProvider;
    }

    public User toUser() {
        User user = new User(this.name);
        user.setSocialProvider(this.socialProvider);
        user.setEmail(this.email);
        if (this.gender != null) {
            user.setGender(UserGender.valueOf(this.gender.toUpperCase()));
        }
        return user;
    }

    @Override
    public String toString() {
        return "SocialUser{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", socialProvider=" + socialProvider +
                '}';
    }
}
