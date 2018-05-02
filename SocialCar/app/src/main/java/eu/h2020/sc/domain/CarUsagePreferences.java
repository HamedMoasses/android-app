package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fminori on 17/06/16.
 */
public class CarUsagePreferences implements Serializable {

    public static final String FOOD_ALLOWED = "food_allowed";
    public static final String SMOKING_ALLOWED = "smoking_allowed";
    public static final String AIR_CONDITIONING = "air_conditioning";
    public static final String PET_ALLOWED = "pets_allowed";
    public static final String CHILD_SEAT = "child_seat";
    public static final String LUGGAGE_TYPE = "luggage_type";
    public static final String MUSIC_ALLOWED = "music_allowed";

    @Expose
    @SerializedName(value = FOOD_ALLOWED)
    private boolean foodAllowed;

    @Expose
    @SerializedName(value = SMOKING_ALLOWED)
    private boolean smokingAllowed;

    @Expose
    @SerializedName(value = AIR_CONDITIONING)
    private boolean airConditioning;

    @Expose
    @SerializedName(value = PET_ALLOWED)
    private boolean petsAllowed;

    @Expose
    @SerializedName(value = LUGGAGE_TYPE)
    private LuggageType luggageType;

    @Expose
    @SerializedName(value = CHILD_SEAT)
    private boolean childSeat;

    @Expose
    @SerializedName(value = MUSIC_ALLOWED)
    private boolean musicAllowed;

    public void setFoodAllowed(Boolean foodAllowed) {
        this.foodAllowed = foodAllowed;
    }

    public void setSmokingAllowed(Boolean smokingAllowed) {
        this.smokingAllowed = smokingAllowed;
    }

    public void setAirConditioning(Boolean airConditioning) {
        this.airConditioning = airConditioning;
    }

    public void setPetsAllowed(Boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    public void setLuggageType(LuggageType luggageType) {
        this.luggageType = luggageType;
    }

    public void setChildSeat(Boolean childSeat) {
        this.childSeat = childSeat;
    }

    public void setMusicAllowed(boolean musicAllowed) {
        this.musicAllowed = musicAllowed;
    }

    @Override
    public String toString() {
        return "CarUsagePreferences{" +
                "airConditioning=" + airConditioning +
                ", foodAllowed=" + foodAllowed +
                ", smokingAllowed=" + smokingAllowed +
                ", petsAllowed=" + petsAllowed +
                ", luggageType=" + luggageType +
                ", childSeat=" + childSeat +
                ", musicAllowed=" + musicAllowed +
                '}';
    }
}
