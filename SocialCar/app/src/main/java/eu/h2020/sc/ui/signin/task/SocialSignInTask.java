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
import eu.h2020.sc.domain.socialnetwork.SocialUser;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.persistence.PersistenceException;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.ui.signin.SocialSigninActivity;
import eu.h2020.sc.ui.signup.task.SocialUserPhotoTask;
import eu.h2020.sc.utils.ImageUtils;

import java.util.List;

/**
 * Created by fminori on 16/09/16.
 */

public class SocialSignInTask extends AsyncTask<SocialUser, Void, Integer> {

    protected final String TAG = this.getClass().getName();

    private static final int SIGNIN_GENERIC_ERROR_RESULT = 0;
    private static final int SIGNIN_CONNECTION_ERROR_RESULT = 1;
    private static final int SIGNIN_SERVER_ERROR_RESULT = 2;
    private static final int SIGNIN_NOT_FOUND_RESULT = 3;
    private static final int SIGNIN_COMPLETED_RESULT = 4;
    private static final int SIGNIN_COMPLETED_WITHOUT_PICTURE_RETRIEVED = 5;
    private static final int SIGNIN_UNAUTHORIZED_RESULT = 6;

    private SocialSigninActivity socialSignInActivity;
    private UserDAO userDAO;
    private CarDAO carDAO;
    private PictureMultipartDAO pictureDAO;
    private SocialCarStore socialCarStore;
    private SocialUser socialUser;


    public SocialSignInTask(SocialSigninActivity socialSignInActivity) {
        this.socialSignInActivity = socialSignInActivity;
        this.userDAO = new UserDAO();
        this.carDAO = new CarDAO();
        this.pictureDAO = new PictureUserMultipartDAO();
        this.socialCarStore = SocialCarApplication.getInstance();
    }

    @Override
    protected Integer doInBackground(SocialUser... socialUsers) {

        this.socialUser = socialUsers[0];

        try {
            User user = this.userDAO.findBySocialProvider(socialUser.getSocialProvider());
            String fcmToken = this.socialCarStore.retrieveFCMToken();

            Credentials credentials = new Credentials(user.getEmail(), user.getPassword());
            this.socialCarStore.store(credentials);

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
        } catch (NotFoundException e) {
            return SIGNIN_NOT_FOUND_RESULT;
        } catch (UnauthorizedException e) {
            return SIGNIN_UNAUTHORIZED_RESULT;
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
            e.printStackTrace();
        }
        return SIGNIN_COMPLETED_RESULT;
    }

    @Override
    protected void onPostExecute(final Integer resultCode) {
        this.socialSignInActivity.dismissDialog();

        switch (resultCode) {
            case SIGNIN_GENERIC_ERROR_RESULT:
                this.socialSignInActivity.showServerGenericError();
                break;
            case SIGNIN_CONNECTION_ERROR_RESULT:
                this.socialSignInActivity.showConnectionError();
                break;
            case SIGNIN_SERVER_ERROR_RESULT:
                this.socialSignInActivity.showServerGenericError();
                break;
            case SIGNIN_UNAUTHORIZED_RESULT:
                Log.e(TAG, "Unauthorized error...check basic auth.");
                this.socialSignInActivity.showUnauthorizedError();
                break;
            case SIGNIN_NOT_FOUND_RESULT:
                Log.i(TAG, "Impossible to complete login by social network...");
                SocialUserPhotoTask socialUserPhotoTask = new SocialUserPhotoTask(this.socialSignInActivity);
                socialUserPhotoTask.execute(this.socialUser.getPhotoUrl(), this.socialUser.getSocialProvider().getSocialID());
                break;
            case SIGNIN_COMPLETED_RESULT:
                this.socialSignInActivity.goToHome();
                break;
            default:
                this.socialSignInActivity.showServerGenericError();
        }
    }
}
