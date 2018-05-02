package eu.h2020.sc.domain;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import eu.h2020.sc.SocialCarApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * Fabio Lombardi <fabio.lombardi@movenda.com> on 11/10/2016.
 * © All rights reserved by Movenda S.p.A..
 */

public class UserPicture extends Picture {

    @SerializedName(value = "user_id")
    @Expose(serialize = false)
    private String userID;

    public UserPicture(String userID) {
        super();
        this.userID = userID;
    }

    public static UserPicture fromJson(String json) {
        return SocialCarApplication.getGson().fromJson(json, UserPicture.class);
    }

    public static UserPicture fromJsonList(String json) throws JSONException {

        JSONObject jsonResponse = new JSONObject(json);
        JSONArray usersJson = jsonResponse.getJSONArray(UserPicture.PICTURES);

        ArrayList<UserPicture> list = SocialCarApplication.getGson().fromJson(usersJson.toString(), new TypeToken<List<UserPicture>>() {
        }.getType());

        return list.get(0);
    }

    public String toJson() {
        GsonBuilder gsonBuilder = SocialCarApplication.getGsonBuilder().excludeFieldsWithoutExposeAnnotation();
        return gsonBuilder.create().toJson(this);
    }

    @Override
    public String toString() {
        return "UserPicture{" +
                "id='" + id + '\'' +
                ", mediaUri='" + mediaUri + '\'' +
                ", userID='" + userID + '\'' +
                "} ";
    }
}
