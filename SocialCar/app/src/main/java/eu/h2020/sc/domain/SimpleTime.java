package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class SimpleTime implements Serializable {

    @Expose
    @SerializedName(value = "hour")
    private String hour;

    @Expose
    @SerializedName(value = "minute")
    private String minute;


    public String getHour() {
        if (this.hour != null)
            return this.hour;
        else
            return "--";
    }

    public String getMinute() {
        if (this.minute != null)
            return this.minute;
        else
            return "--";
    }

    @Override
    public String toString() {
        return this.getHour() + ":" + this.getMinute();
    }
}
