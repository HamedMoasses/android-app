package eu.h2020.sc.persistence;

import android.graphics.Bitmap;

import eu.h2020.sc.domain.Car;
import eu.h2020.sc.domain.Credentials;
import eu.h2020.sc.domain.User;

/**
 * Created by fminori on 15/09/16.
 */
public interface SocialCarStore {

    void store(Credentials credentials);

    /**
     * Retrieve previously stored credentials.
     *
     * @return credentials or null if not present
     */
    Credentials retrieveCredentials();


    void storeFCMToken(String token);

    /**
     * Retrieve stored FCM token
     *
     * @return the token of null if not present
     */
    String retrieveFCMToken();


    /**
     * Store User
     *
     * @param user
     */
    void store(User user);

    /**
     * Store trip solutions found
     *
     * @param tripList
     */
    void store(String tripList);

    /**
     * Retrieve stored trip solutions
     *
     * @return the token of null if not present
     */
    String retrieveTripSolutions();

    /**
     * Store Car
     *
     * @param car
     */
    void store(Car car);

    /**
     * Retrieve User stored
     *
     * @return the User of null if not present
     */
    User getUser();

    /**
     * Retrieve Car stored
     *
     * @return the Car of null if not present
     */
    Car getCar();

    void removeAllUserInfo();


    /**
     * Store User picture by userID
     *
     * @return the User picture
     * @throws PersistenceException if an error has Occured
     */
    void storeUserPicture(Bitmap picture) throws PersistenceException;

    /**
     * Retrieve User picture by user id
     *
     * @return Bitmap the User picture
     * @throws PersistenceException if User Picture is null
     */
    Bitmap retrieveUserPicture();


    /**
     * Store picture by key
     *
     * @return the picture
     * @throws PersistenceException if an error has Occured
     */
    void cacheImage(Bitmap bitmap, String key) throws PersistenceException;

    /**
     * Retrieve picture by key
     *
     * @return Bitmap the picture or null
     */
    Bitmap retrieveCacheImage(String key);

    /**
     * remove picture by key
     *
     */
    void removeCacheImage(String key);

}
