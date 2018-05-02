package eu.h2020.sc.utils;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class DimensionUtils {

    public static int obtainRideAvatarDimension(float density) {

        if (density >= 4.0) {
            //xxxhdpi
            return 160;
        }
        if (density >= 2.5) {
            //xxhdpi
            return 120;
        }
        if (density >= 2.0) {
            //xhdpi
            return 80;
        }
        if (density >= 1.5) {
            //hdpi
            return 60;
        }
        if (density >= 1.0) {
            //mdpi
            return 40;
        }
        //ldpi
        return 20;
    }

    public static int obtainNotificationWidthAvatarDimension(float density) {

        if (density >= 4.0) {
            //xxxhdpi
            return 256;
        }
        if (density >= 2.5) {
            //xxhdpi
            return 256;
        }
        if (density >= 2.0) {
            //xhdpi
            return 171;
        }
        if (density >= 1.5) {
            //hdpi
            return 128;
        }
        if (density >= 1.0) {
            //mdpi
            return 85;
        }
        //ldpi
        return 48;
    }

    public static int obtainNotificationHeightAvatarDimension(float density) {

        if (density >= 4.0) {
            //xxxhdpi
            return 192;
        }
        if (density >= 2.5) {
            //xxhdpi
            return 192;
        }
        if (density >= 2.0) {
            //xhdpi
            return 128;
        }
        if (density >= 1.5) {
            //hdpi
            return 96;
        }
        if (density >= 1.0) {
            //mdpi
            return 64;
        }
        //ldpi
        return 48;
    }

    public static int obtainCropWindowWidthDimension(float density) {

        if (density >= 4.0) {
            //xxxhdpi
            return 958;
        }
        if (density >= 2.5) {
            //xxhdpi
            return 715;
        }
        if (density >= 2.0) {
            //xhdpi
            return 480;
        }
        if (density >= 1.5) {
            //hdpi
            return 360;
        }
        if (density >= 1.0) {
            //mdpi
            return 240;
        }
        //ldpi
        return 122;
    }

    public static int obtainCropWindowHeightDimension(float density) {

        if (density >= 4.0) {
            //xxxhdpi
            return 719;
        }
        if (density >= 2.5) {
            //xxhdpi
            return 536;
        }
        if (density >= 2.0) {
            //xhdpi
            return 270;
        }
        if (density >= 1.5) {
            //hdpi
            return 270;
        }
        if (density >= 1.0) {
            //mdpi
            return 180;
        }
        //ldpi
        return 92;
    }

    public static int obtainToolbarMarginTop(float density) {

        if (density >= 4.0) {
            //xxxhdpi
            return 96;
        }
        if (density > 2.625) {
            //xxhdpi
            return 72;
        }
        if (density == 2.625) {
            //NEXUS 5X
            return 60;
        }
        if (density >= 2.0) {
            //xhdpi
            return 48;
        }
        if (density >= 1.5) {
            //hdpi
            return 36;
        }
        if (density >= 1.0) {
            //mdpi
            return 24;
        }
        //ldpi
        return 12;
    }
}
