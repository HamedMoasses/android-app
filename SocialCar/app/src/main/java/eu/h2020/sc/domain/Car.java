/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.domain;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import eu.h2020.sc.SocialCarApplication;

import java.io.Serializable;
import java.util.List;

/**
 * @author fminori
 */
public class Car implements Serializable {

    @Expose(serialize = false)
    @SerializedName(value = "_id")
    private String id;

    @Expose
    @SerializedName(value = "owner_id")
    private String ownerID;

    @Expose
    private String model;
    @Expose
    private String colour;
    @Expose
    private String plate;
    @Expose
    private Integer seats;

    @Expose
    @SerializedName(value = "car_usage_preferences")
    private CarUsagePreferences carUsagePreferences;

    @Expose
    @SerializedName(value = "pictures")
    private List<CarPicture> carPictures;


    public Car(String plate) {
        this.plate = plate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getPlate() {
        return plate;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public void setCarUsagePreferences(CarUsagePreferences carUsagePreferences) {
        this.carUsagePreferences = carUsagePreferences;
    }

    public void addPicture(CarPicture carPicture) {
        this.carPictures.add(carPicture);
    }

    public List<CarPicture> getCarPictures() {
        return carPictures;
    }

    public boolean isPlateValid() {
        return !this.plate.isEmpty();
    }

    public boolean isModelValid() {
        return !this.model.isEmpty();
    }

    public boolean isSeatsValid() {
        return (this.seats != null && this.seats > 0);
    }

    public static Car fromJson(String jsonCar) {
        return SocialCarApplication.getGson().fromJson(jsonCar, Car.class);
    }

    public String toJson() {
        GsonBuilder gsonBuilder = SocialCarApplication.getGsonBuilder().excludeFieldsWithoutExposeAnnotation();
        return gsonBuilder.create().toJson(this);
    }

    @Override
    public String toString() {
        return "Car{" +
                "carUsagePreferences=" + carUsagePreferences +
                ", id='" + id + '\'' +
                ", ownerID='" + ownerID + '\'' +
                ", model='" + model + '\'' +
                ", colour='" + colour + '\'' +
                ", plate='" + plate + '\'' +
                ", seats=" + seats +
                '}';
    }
}
