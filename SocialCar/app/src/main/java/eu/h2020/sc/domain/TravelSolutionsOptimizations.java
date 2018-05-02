package eu.h2020.sc.domain;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by khairul.alam on 14/03/17.
 */

public enum TravelSolutionsOptimizations implements Serializable {
    // todo refactor if need compared to 'TransferMode'
    FASTEST, SHORTEST, CHEAPEST;

    public static String[] sortedNames() {
        TravelSolutionsOptimizations[] array = values();
        String[] names = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            names[i] = array[i].name();
        }
        Arrays.sort(names);
        return names;
    }
}
