/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.ui.signin.task;

import android.os.AsyncTask;
import android.util.Log;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.CarDAO;
import eu.h2020.sc.dao.UserDAO;
import eu.h2020.sc.dao.multipart.PictureMultipartDAO;
import eu.h2020.sc.dao.multipart.PictureUserMultipartDAO;
import eu.h2020.sc.domain.Car;
import eu.h2020.sc.domain.Credentials;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.persistence.PersistenceException;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.ui.signin.SignInActivity;
import eu.h2020.sc.utils.ImageUtils;

import java.util.List;

/**
 * Created by fminori on 16/09/16.
 */

public class SignInTask extends AsyncTask<Credentials, Void, Integer> {

    protected final String TAG = this.getClass().getName();

    private static final int SIGNIN_GENERIC_ERROR_RESULT = 0;
    private static final int SIGNIN_CONNECTION_ERROR_RESULT = 1;
    private static final int SIGNIN_SERVER_ERROR_RESULT = 2;
    private static final int SIGNIN_UNAUTHORIZED_RESULT = 3;
    private static final int SIGNIN_COMPLETED_RESULT = 4;
    private static final int SIGNIN_COMPLETED_WITHOUT_PICTURE_RETRIEVED = 5;
    private static final int SIGNIN_NOT_FOUND_ERRROR = 6;

    private SignInActivity signInActivity;
    private UserDAO userDAO;
    private CarDAO carDAO;
    private SocialCarStore socialCarStore;
    private PictureMultipartDAO pictureDAO;


    public SignInTask(SignInActivity signInActivity) {
        this.signInActivity = signInActivity;
        this.userDAO = new UserDAO();
        this.carDAO = new CarDAO();
        this.pictureDAO = new PictureUserMultipartDAO();
        this.socialCarStore = SocialCarApplication.getInstance();
    }

    @Override
    protected Integer doInBackground(Credentials... credentialses) {

        Credentials credentials = credentialses[0];

        this.socialCarStore.store(credentials);

        try {
            User user = this.userDAO.findUserByEmail(credentials.getEmail());

            String fcmToken = this.socialCarStore.retrieveFCMToken();

            if (!user.getFcmToken().equals(fcmToken)) {
                Log.i(TAG, String.format("The FCM Token was changed... %s", fcmToken));
                user.setFcmToken(fcmToken);
                this.userDAO.updateUser(user);
            }

            List<String> carsID = user.getCarsID();

            if (!carsID.isEmpty()) {
                for (String carID : carsID) {
                    Car car = this.carDAO.findCarByID(carID);
                    this.socialCarStore.store(car);
                }
            }

            this.socialCarStore.store(user);

            if (!user.getUserPictures().isEmpty())
                return this.retrieveAndPersistUserPicture(user);

            return SIGNIN_COMPLETED_RESULT;

        } catch (ServerException e) {
            return SIGNIN_SERVER_ERROR_RESULT;
        } catch (ConnectionException e) {
            return SIGNIN_CONNECTION_ERROR_RESULT;
        } catch (UnauthorizedException e) {
            return SIGNIN_UNAUTHORIZED_RESULT;
        } catch (NotFoundException e) {
            return SIGNIN_NOT_FOUND_ERRROR;
        }
    }

    private int retrieveAndPersistUserPicture(User user) throws ServerException, ConnectionException, UnauthorizedException {
        try {

            byte[] userPicture = this.pictureDAO.findPictureByMediaUri(user.getUserPictures().get(0).getMediaUri());

            if (userPicture != null) {
                Log.i(TAG, String.format("Picture retrieved for userID : %s", user.getId()));
                this.socialCarStore.storeUserPicture(ImageUtils.fromByteArrayToBitmap(userPicture));
            }

        } catch (NotFoundException e) {
            return SIGNIN_COMPLETED_WITHOUT_PICTURE_RETRIEVED;

        } catch (PersistenceException e) {
            Log.e(TAG, "Unable to persist user picture during signin process...", e);
        }
        return SIGNIN_COMPLETED_RESULT;
    }


    @Override
    protected void onPostExecute(final Integer resultCode) {
        this.signInActivity.dismissDialog();

        switch (resultCode) {
            case SIGNIN_GENERIC_ERROR_RESULT:
                this.signInActivity.showServerGenericError();
                break;
            case SIGNIN_CONNECTION_ERROR_RESULT:
                this.signInActivity.showConnectionError();
                break;
            case SIGNIN_NOT_FOUND_ERRROR:
                Log.i(TAG, "Users not found or generic 404 status code...");
                this.signInActivity.showUnauthorizedError();
                break;
            case SIGNIN_SERVER_ERROR_RESULT:
                this.signInActivity.showServerGenericError();
                break;
            case SIGNIN_UNAUTHORIZED_RESULT:
                Log.e(TAG, "Unauthorized error...check basic auth.");
                this.signInActivity.showUnauthorizedError();
                break;
            case SIGNIN_COMPLETED_RESULT:
            case SIGNIN_COMPLETED_WITHOUT_PICTURE_RETRIEVED:
                this.signInActivity.goToHome();
                break;
            default:
                this.signInActivity.showServerGenericError();
        }
    }

    public boolean validate(Credentials credentials) {
        if (credentials.getEmail().isEmpty()) {
            this.signInActivity.showInsertEmail();
            return false;
        }
        if (credentials.getPassword().isEmpty()) {
            this.signInActivity.showInsertPassword();
            return false;
        }

        if (credentials.isPasswordTooShort() || !Credentials.isEmailValid(credentials.getEmail())) {
            this.signInActivity.showInvalidEmailPassword();
            return false;
        }

        this.signInActivity.hideSoftKeyboard();
        return true;
    }

}
