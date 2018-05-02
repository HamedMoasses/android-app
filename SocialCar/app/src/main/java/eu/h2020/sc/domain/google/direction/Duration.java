package eu.h2020.sc.domain.google.direction;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pietro on 02/09/16.
 */
public class Duration {

    @SerializedName("value")
    private long inSeconds;

    @SerializedName("text")
    private String humanReadable;

    public String getHumanReadable() {
        return humanReadable;
    }
}
