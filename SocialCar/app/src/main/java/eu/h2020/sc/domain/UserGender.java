package eu.h2020.sc.domain;

import java.io.Serializable;
import java.util.Arrays;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;

/**
 * Created by Pietro on 03/08/16.
 */
public enum UserGender implements Serializable {
    MALE, FEMALE;

    public static String[] sortedNames() {
        UserGender[] array = values();
        String[] names = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            names[i] = array[i].name();
        }
        Arrays.sort(names);
        return names;
    }

    public static String getGenderString(UserGender userGender) {
        if (userGender == UserGender.FEMALE)
            return SocialCarApplication.getContext().getString(R.string.female);

        return SocialCarApplication.getContext().getString(R.string.male);
    }
}
