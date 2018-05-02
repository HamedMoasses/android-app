package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by khairul alam on 13/03/17.
 */

public class TravelPreferences implements Serializable {

    @Expose
    @SerializedName(value = "preferred_transport")
    private List<TravelMode> travelModes;
    @Expose
    @SerializedName(value = "optimisation")
    private List<TravelSolutionsOptimizations> listTravelSolutionsOptimizations;
    @Expose
    @SerializedName(value = "carpooler_preferred_gender")
    private UserGender carpoolerGender;
    @Expose
    @SerializedName(value = "carpooler_preferred_age_group")
    private String carpoolerAgeRange;
    @Expose
    @SerializedName(value = "special_request")
    private List<SpecialNeeds> listSpecialNeeds;
    @Expose
    @SerializedName(value = "gps_tracking")
    private boolean allowGpsTracking;
    @Expose
    @SerializedName(value = "max_transfers")
    private Integer maxTransfers;
    @Expose
    @SerializedName(value = "max_cost")
    private Integer maxCost;
    @Expose
    @SerializedName(value = "max_walk_distance")
    private Integer maxWalkDistance;

    @Expose
    @SerializedName(value = "smoking")
    private boolean smokingPreferred;
    @Expose
    @SerializedName(value = "music")
    private boolean musicPreferred;
    @Expose
    @SerializedName(value = "food")
    private boolean foodPreferred;
    @Expose
    @SerializedName(value = "pets")
    private boolean petsPreferred;
    @Expose
    @SerializedName(value = "luggage")
    private boolean luggagePreferred;


    public TravelPreferences() {
        travelModes = new ArrayList<>();
        listTravelSolutionsOptimizations = new ArrayList<>();
        listSpecialNeeds = new ArrayList<>();
    }

    public List<TravelMode> getTravelModes() {
        return travelModes;
    }

    public List<TravelSolutionsOptimizations> getListTravelSolutionsOptimizations() {
        return listTravelSolutionsOptimizations;
    }

    public void setCarpoolerGender(UserGender carpoolerGender) {
        this.carpoolerGender = carpoolerGender;
    }

    public List<SpecialNeeds> getListSpecialNeeds() {
        return listSpecialNeeds;
    }

    public void setCarpoolerAgeRange(String carpoolerAgeRange) {
        this.carpoolerAgeRange = carpoolerAgeRange;
    }

    public void setAllowGpsTracking(boolean allowGpsTracking) {
        this.allowGpsTracking = allowGpsTracking;
    }

    public void setTravelModes(List<TravelMode> travelModes) {
        this.travelModes = travelModes;
    }

    public void setListTravelSolutionsOptimizations(List<TravelSolutionsOptimizations> listTravelSolutionsOptimizations) {
        this.listTravelSolutionsOptimizations = listTravelSolutionsOptimizations;
    }

    public void setListSpecialNeeds(List<SpecialNeeds> listSpecialNeeds) {
        this.listSpecialNeeds = listSpecialNeeds;
    }

    public void setSmokingPreferred(boolean smokingPreferred) {
        this.smokingPreferred = smokingPreferred;
    }

    public void setMusicPreferred(boolean musicPreferred) {
        this.musicPreferred = musicPreferred;
    }

    public void setFoodPreferred(boolean foodPreferred) {
        this.foodPreferred = foodPreferred;
    }

    public void setPetsPreferred(boolean petsPreferred) {
        this.petsPreferred = petsPreferred;
    }

    public void setLuggagePreferred(boolean luggagePreferred) {
        this.luggagePreferred = luggagePreferred;
    }

    public void setMaxTransfers(Integer maxTransfers) {
        this.maxTransfers = maxTransfers;
    }

    public void setMaxCost(Integer maxCost) {
        this.maxCost = maxCost;
    }

    public void setMaxWalkDistance(Integer maxWalkDistance) {
        this.maxWalkDistance = maxWalkDistance;
    }

    public UserGender getCarpoolerGender() {
        return carpoolerGender;
    }

    public String getCarpoolerAgeRange() {
        return carpoolerAgeRange;
    }

    public boolean isAllowGpsTracking() {
        return allowGpsTracking;
    }

    public Integer getMaxTransfers() {
        return (maxTransfers != null ? maxTransfers : 0);
    }

    public Integer getMaxCost() {
        return (maxCost != null ? maxCost : 0);
    }

    public Integer getMaxWalkDistance() {
        return (maxWalkDistance != null ? maxWalkDistance : 0);
    }

    public boolean isSmokingPreferred() {
        return smokingPreferred;
    }

    public boolean isMusicPreferred() {
        return musicPreferred;
    }

    public boolean isFoodPreferred() {
        return foodPreferred;
    }

    public boolean isPetsPreferred() {
        return petsPreferred;
    }

    public boolean isLuggagePreferred() {
        return luggagePreferred;
    }

    public Integer[] findIndicesOfSelectedTravelSolutionsOrder() {
        List<Integer> indices = new ArrayList<>();

        List listSortedTravelSolutionsOrders = Arrays.asList(TravelSolutionsOptimizations.sortedNames());

        for (int i = 0; i < listTravelSolutionsOptimizations.size(); i++) {
            indices.add(listSortedTravelSolutionsOrders.indexOf(listTravelSolutionsOptimizations.get(i).toString()));
        }

        if (indices.isEmpty())
            return null;
        return indices.toArray(new Integer[indices.size()]);
    }

    public Integer[] findIndicesOfSelectedTravelModes() {
        List<Integer> indices = new ArrayList<>();

        List listSortedTravelModes = Arrays.asList(TravelMode.sortedNames());

        for (int i = 0; i < travelModes.size(); i++) {
            indices.add(listSortedTravelModes.indexOf(travelModes.get(i).toString()));
        }

        if (indices.isEmpty())
            return null;
        return indices.toArray(new Integer[indices.size()]);
    }

    public String formattedStringOfPreferredTravelModes() {
        StringBuilder sb = new StringBuilder();
        for (Iterator<TravelMode> i = travelModes.iterator(); i.hasNext(); ) {
            sb.append(i.next().toString());
            if (i.hasNext()) sb.append(", ");
        }
        return sb.toString();
    }

    public String formattedStringOfSelectedTravelSolutionsOrder() {
        StringBuilder sb = new StringBuilder();
        for (Iterator<TravelSolutionsOptimizations> i = listTravelSolutionsOptimizations.iterator(); i.hasNext(); ) {
            sb.append(i.next().toString());
            if (i.hasNext()) sb.append(", ");
        }
        return sb.toString();
    }

    public Integer[] findIndicesOfSelectedSpecialNeeds() {

        List<Integer> indices = new ArrayList<>();

        List listSortedTravelModes = Arrays.asList(SpecialNeeds.sortedNames());

        for (int i = 0; i < listSpecialNeeds.size(); i++) {
            indices.add(listSortedTravelModes.indexOf(listSpecialNeeds.get(i).toString()));
        }

        if (indices.isEmpty())
            return null;
        return indices.toArray(new Integer[indices.size()]);
    }

    public String formattedStringOfSelectedSpecialNeeds() {
        StringBuilder sb = new StringBuilder();
        for (Iterator<SpecialNeeds> i = listSpecialNeeds.iterator(); i.hasNext(); ) {
            sb.append(i.next().toString());
            if (i.hasNext()) sb.append(", ");
        }
        return sb.toString();
    }
}
