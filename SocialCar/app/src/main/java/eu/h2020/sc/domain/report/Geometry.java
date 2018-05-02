package eu.h2020.sc.domain.report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pietro on 25/07/2017.
 */

public class Geometry implements Serializable {

    @Expose
    @SerializedName("type")
    private Type type;

    @Expose
    @SerializedName("coordinates")
    private List<Double> coordinateList;

    public Geometry() {
        this.type = Type.POINT;
        this.coordinateList = new ArrayList<>();
    }

    public List<Double> getCoordinateList() {
        return coordinateList;
    }

    public void addCoordinate(Double coordinate) {
        this.coordinateList.add(coordinate);
    }

    @Override
    public String toString() {
        return "Geometry{" +
                "type=" + type +
                ", coordinateList=" + coordinateList +
                '}';
    }
}
