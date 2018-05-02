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

public class CarPicture extends Picture {

    @SerializedName(value = "car_id")
    @Expose
    private String carID;

    public CarPicture(String carID) {
        super();
        this.carID = carID;
    }

    public static CarPicture fromJson(String json) {
        return SocialCarApplication.getGson().fromJson(json, CarPicture.class);
    }

    public static CarPicture fromJsonList(String json) throws JSONException {

        JSONObject jsonResponse = new JSONObject(json);
        JSONArray usersJson = jsonResponse.getJSONArray(CarPicture.PICTURES);

        ArrayList<CarPicture> list = SocialCarApplication.getGson().fromJson(usersJson.toString(), new TypeToken<List<CarPicture>>() {
        }.getType());

        return list.get(0);
    }

    public String toJson() {
        GsonBuilder gsonBuilder = SocialCarApplication.getGsonBuilder().excludeFieldsWithoutExposeAnnotation();
        return gsonBuilder.create().toJson(this);
    }

    @Override
    public String toString() {
        return "CarPicture{" +
                "id='" + id + '\'' +
                ", mediaUri='" + mediaUri + '\'' +
                ", carID='" + carID + '\'' +
                "} ";
    }
}
