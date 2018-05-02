package eu.h2020.sc.domain;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by khairul.alam on 15/03/17.
 */

public enum SpecialNeeds implements Serializable {

    WHEELCHAIR, BLIND, DEAF, ELDERLY;

    public static String[] sortedNames() {
        SpecialNeeds[] array = values();
        String[] names = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            names[i] = array[i].name();
        }
        Arrays.sort(names);
        return names;
    }
}
