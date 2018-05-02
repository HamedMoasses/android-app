package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.protocol.FindStopsAroundRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class Stop extends Point {

    @Expose
    @SerializedName(value = "stop_code")
    private String stopCode;

    @Expose
    @SerializedName(value = "stop_name")
    private String name;

    @Expose
    @SerializedName(value = "transits")
    private List<Transit> transits;

    public Stop() {
        this.transits = new ArrayList<>();
    }

    public Stop(String stopCode, String name, List<Transit> transits) {
        super();
        this.stopCode = stopCode;
        this.name = name;
        this.transits = transits;
    }

    public String getStopCode() {
        return stopCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Transit> getTransits() {
        return transits;
    }

    public static Stop fromJson(String jsonStop) {
        return SocialCarApplication.getGson().fromJson(jsonStop, Stop.class);
    }

    @Override
    public String toString() {
        return "Stop [stopCode=" + stopCode + ", name=" + name + ", transits=" + transits + "]";
    }

    public static List<Stop> fromJsonToStopList(String jsonStopList) {
        try {
            JSONObject objectStopList = new JSONObject(jsonStopList);
            JSONArray arrayStopList = objectStopList.getJSONArray(FindStopsAroundRequest.JSON_KEY_STOPS);
            return SocialCarApplication.getGson()
                    .fromJson(arrayStopList.toString(), new TypeToken<List<Stop>>() {
                    }.getType());

        } catch (JSONException e) {
            throw new RuntimeException("Stop around JSON is not compliant with the protocol. Check it! " + jsonStopList, e);
        }


    }
}
