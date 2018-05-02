package eu.h2020.sc;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Date;

import eu.h2020.sc.config.Globals;
import eu.h2020.sc.domain.Car;
import eu.h2020.sc.domain.Credentials;
import eu.h2020.sc.domain.Point;
import eu.h2020.sc.domain.Price;
import eu.h2020.sc.domain.Ride;
import eu.h2020.sc.domain.Transport;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.domain.UserGender;
import eu.h2020.sc.domain.messages.ChatController;
import eu.h2020.sc.location.PositionManager;
import eu.h2020.sc.persistence.PersistenceException;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.protocol.gson.adapter.DateAdapter;
import eu.h2020.sc.protocol.gson.adapter.PriceAdapter;
import eu.h2020.sc.protocol.gson.adapter.RideAdapter;
import eu.h2020.sc.protocol.gson.adapter.TransportAdapter;
import eu.h2020.sc.utils.StorageUtility;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class SocialCarApplication extends Application implements SocialCarStore {

    private static Context context;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static Gson gson;
    private static GsonBuilder gsonBuilder;
    private static StorageUtility storageUtility;
    private static SocialCarApplication socialCarApplication;

    public static Context getContext() {
        return context;
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static SharedPreferences.Editor getEditor() {
        return editor;
    }

    public static Gson getGson() {
        return gson;
    }

    public static GsonBuilder getGsonBuilder() {
        return gsonBuilder;
    }

    public static SocialCarApplication getInstance() {
        return SocialCarApplication.socialCarApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SocialCarApplication.socialCarApplication = this;

        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Price.class, new PriceAdapter());
        gsonBuilder.registerTypeAdapter(Transport.class, new TransportAdapter());
        gsonBuilder.registerTypeAdapter(Date.class, new DateAdapter());
        gsonBuilder.registerTypeAdapter(Ride.class, new RideAdapter());
        gson = gsonBuilder.create();

        storageUtility = new StorageUtility(context);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void store(Credentials credentials) {
        editor.putString(Globals.EMAIL_KEY, credentials.getEmail());
        editor.putString(Globals.PASSWORD_KEY, credentials.getPassword());
        editor.commit();
    }

    public void storeBounds(String latLngBounds) {
        editor.putString(Globals.KEY_PRESENT_COUNTRY_LATLNG_BOUNDS, latLngBounds);
        editor.commit();
    }

    public LatLngBounds retrieveBounds() {
        String presentCountryLatLngBoundsJson = sharedPreferences.getString(Globals.KEY_PRESENT_COUNTRY_LATLNG_BOUNDS, null);
        return new Gson().fromJson(presentCountryLatLngBoundsJson, LatLngBounds.class);
    }

    public void storeLastKnownLocation(double latitude, double longitude, String address) {
        Point point = new Point(latitude, longitude, address);
        editor.putString(PositionManager.KEY_LAST_KNOWN_POINT, point.toJson());
        editor.commit();
    }

    public Point retrieveLastKnowLocation() {
        Point point = null;
        String jsonLastLocationPoint = sharedPreferences.getString(PositionManager.KEY_LAST_KNOWN_POINT, null);
        if (jsonLastLocationPoint != null)
            point = Point.fromJson(jsonLastLocationPoint);
        return point;
    }

    @Override
    public Credentials retrieveCredentials() {
        String email = sharedPreferences.getString(Globals.EMAIL_KEY, null);
        String password = sharedPreferences.getString(Globals.PASSWORD_KEY, null);

        return new Credentials(email, password);
    }

    @Override
    public void storeFCMToken(String token) {
        editor.putString(Globals.FCM_TOKEN, token);
        editor.commit();
    }

    @Override
    public String retrieveFCMToken() {
        return sharedPreferences.getString(Globals.FCM_TOKEN, null);

    }

    @Override
    public void store(User user) {
        editor.putString(Globals.USER_LOGGED_KEY, user.toJson());
        editor.commit();
    }

    @Override
    public void store(String tripList) {
        editor.putString(context.getString(R.string.trip_solution_current_trips), tripList).commit();
    }

    @Override
    public String retrieveTripSolutions() {
        return sharedPreferences.getString(context.getString(R.string.trip_solution_current_trips), null);
    }

    @Override
    public void store(Car car) {
        editor.putString(Globals.USER_CAR_KEY, car.toJson());
        editor.commit();
    }

    @Override
    public User getUser() {
        String userJson = sharedPreferences.getString(Globals.USER_LOGGED_KEY, null);

        if (userJson != null) {
            return User.fromJson(userJson);
        }
        return null;
    }

    @Override
    public Car getCar() {
        String carJson = sharedPreferences.getString(Globals.USER_CAR_KEY, null);

        if (carJson != null) {
            return Car.fromJson(carJson);
        }
        return null;
    }

    @Override
    public void removeAllUserInfo() {
        editor.remove(Globals.PASSWORD_KEY)
                .remove(Globals.EMAIL_KEY)
                .remove(Globals.USER_LOGGED_KEY)
                .remove(PositionManager.KEY_LAST_KNOWN_POINT)
                .remove(Globals.USER_CAR_KEY)
                .remove(ChatController.CONTACT_MESSAGES)
                .commit();
        storageUtility.removeAllImage();
    }


    @Override
    public void storeUserPicture(Bitmap picture) throws PersistenceException {
        User user = getUser();
        try {
            storageUtility.saveImageToStorage(picture, user.getId());
        } catch (IOException e) {
            throw new PersistenceException("Unable to persist user picture for user: " + user.getId(), e);
        }
    }

    @Override
    public Bitmap retrieveUserPicture() {

        User user = getUser();

        try {
            return storageUtility.loadImageFromStorage(user.getId());
        } catch (IOException e) {

            if (user.getGender() == UserGender.FEMALE)
                return BitmapFactory.decodeResource(SocialCarApplication.getContext().getResources(), R.mipmap.profile_female_avatar);
            else
                return BitmapFactory.decodeResource(SocialCarApplication.getContext().getResources(), R.mipmap.profile_male_avatar);
        }
    }


    @Override
    public void cacheImage(Bitmap bitmap, String key) throws PersistenceException {
        try {
            storageUtility.saveImageToCache(bitmap, key);
        } catch (IOException e) {
            throw new PersistenceException("Unable to persist image for key: " + key, e);
        }
    }

    @Override
    public Bitmap retrieveCacheImage(String key) {
        try {
            return storageUtility.loadImageFromCache(key);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void removeCacheImage(String key) {
        storageUtility.removeFromCache(key);
    }

}
