/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * @author fminori
 */
public enum TravelMode implements Parcelable {
    CAR_POOLING, METRO, BUS, RAIL, FEET, TRAM;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    public static final Creator<TravelMode> CREATOR = new Creator<TravelMode>() {
        @Override
        public TravelMode createFromParcel(Parcel source) {
            return TravelMode.values()[source.readInt()];
        }

        @Override
        public TravelMode[] newArray(int size) {
            return new TravelMode[size];
        }
    };

    public static String[] sortedNames() {
        TravelMode[] travelModes = values();
        String[] names = new String[travelModes.length];
        for (int i = 0; i < travelModes.length; i++) {
            names[i] = travelModes[i].name();
        }
        Arrays.sort(names);
        return names;
    }
}
