package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import eu.h2020.sc.domain.socialnetwork.SocialNetwork;

import java.io.Serializable;

/**
 * Created by fabiolombardi on 25/01/17.
 */

public class SocialProvider implements Serializable {

    public static final String SOCIAL_ID = "social_id";
    public static final String SOCIAL_NETWORK = "social_network";

    @Expose
    @SerializedName(value = SOCIAL_ID)
    private String socialID;
    @Expose
    @SerializedName(value = SOCIAL_NETWORK)
    private SocialNetwork socialNetwork;


    public SocialProvider(String socialID, SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
        this.socialID = socialID;
    }

    public SocialNetwork getSocialNetwork() {
        return socialNetwork;
    }

    public String getSocialID() {
        return socialID;
    }


    @Override
    public String toString() {
        return "SocialProvider{" +
                "socialID='" + socialID + '\'' +
                ", socialNetwork=" + socialNetwork +
                '}';
    }
}
