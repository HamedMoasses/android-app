package eu.h2020.sc.domain.google.direction;


import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * Created by Pietro on 01/09/16.
 */
public class DirectionRoute {

    @SerializedName("overview_polyline")
    private EncodedPolyline overviewPolyline;

    private List<DirectionLeg> legs;


    public EncodedPolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public List<DirectionLeg> getLegs() {
        return Collections.unmodifiableList(legs);
    }
}

