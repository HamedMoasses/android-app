package eu.h2020.sc.config;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class Globals {
    public static final String EMAIL_KEY = "EMAIL";
    public static final String PASSWORD_KEY = "PASSWORD";

    public static final int PASSWORD_LENGTH = 6;
    public static final String USER_LOGGED_KEY = "USER_LOGGED_KEY";
    public static final String USER_CAR_KEY = "USER_CAR_KEY";
    public static final String FCM_TOKEN = "FCM_TOKEN";
    public static final String SOCIALCAR_APP_VERSION = "SocialCar/1.0";

    public static final String KEY_PRESENT_COUNTRY_LATLNG_BOUNDS = "KEY_PRESENT_COUNTRY_LATLNG_BOUNDS";

    public static final double EU_BOUNDS_NORTHEAST_LAT = 82.1673907;
    public static final double EU_BOUNDS_NORTHEAST_LNG = 74.3555001;
    public static final double EU_BOUNDS_SOUTHWEST_LAT = 34.5428;
    public static final double EU_BOUNDS_SOUTHWEST_LNG = -31.4647999;

    public static final int MULTIPLE_CLICK_TIME_SPAN_MILLIS = 500;

    public static final float SMALLEST_DISPLACEMENT_METERS = 500;
    public static final long INTERVAL_SEC = 60;
    public static final long FASTEST_INTERVAL_SEC = 30;
}
