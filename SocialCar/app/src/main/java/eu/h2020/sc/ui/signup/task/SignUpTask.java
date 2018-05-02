/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.ui.signup.task;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.UserDAO;
import eu.h2020.sc.dao.multipart.PictureMultipartDAO;
import eu.h2020.sc.dao.multipart.PictureUserMultipartDAO;
import eu.h2020.sc.domain.Credentials;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.domain.UserPicture;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.persistence.PersistenceException;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.ui.signup.SignUpActivity;
import eu.h2020.sc.utils.ImageUtils;

/**
 * Created by fminori on 19/09/16.
 */

public class SignUpTask extends AsyncTask<User, Void, Integer> {

    private static final int SIGNUP_GENERIC_ERROR_RESULT = 0;
    private static final int SIGNUP_CONNECTION_ERROR_RESULT = 1;
    private static final int SIGNUP_SERVER_ERROR_RESULT = 2;
    private static final int SIGNUP_USER_ALREADY_EXISTS_RESULT = 3;
    private static final int SIGNUP_COMPLETED_RESULT = 4;
    private static final int SIGNUP_COMPLETED_WITHOUT_PICTURE_UPLOAD = 5;

    protected final String TAG = this.getClass().getName();
    private final SignUpActivity signupActivity;
    private UserDAO userDAO;
    private PictureMultipartDAO multipartDAO;
    private SocialCarStore socialCarStore;

    public SignUpTask(SignUpActivity signUpActivity) {
        this.signupActivity = signUpActivity;
        this.userDAO = new UserDAO();
        this.multipartDAO = new PictureUserMultipartDAO();
        this.socialCarStore = SocialCarApplication.getInstance();
    }

    @Override
    protected Integer doInBackground(User... users) {

        Integer resultCode = SIGNUP_COMPLETED_RESULT;

        User user = users[0];

        // retrieve fcm token
        String fcmToken = this.socialCarStore.retrieveFCMToken();
        user.setFcmToken(fcmToken);

        try {
            user = this.userDAO.createUser(user);
        } catch (ServerException e) {
            return SIGNUP_SERVER_ERROR_RESULT;
        } catch (ConnectionException e) {
            return SIGNUP_CONNECTION_ERROR_RESULT;
        } catch (ConflictException e) {
            return SIGNUP_USER_ALREADY_EXISTS_RESULT;
        }

        Credentials credentials = new Credentials(user.getEmail(), user.getPassword());
        this.socialCarStore.store(credentials);
        this.socialCarStore.store(user);

        Bitmap userPictureBitmap = this.signupActivity.getUserPicture();

        try {

            if (userPictureBitmap != null) {

                Bitmap userPictureToUpload = ImageUtils.generateBitmapToUpload(userPictureBitmap, this.signupActivity);

                //FIXME: REMOVE MULTIPART POST!!
                Uri userPictureUri = ImageUtils.getUriFromBitmap(userPictureToUpload, this.signupActivity);
                UserPicture userPicture = (UserPicture) this.multipartDAO.createOrUpdate(user.getId(), userPictureUri);

                Log.i(TAG, userPicture.toString());

                user.addPicture(userPicture);
                this.socialCarStore.store(user);
                this.socialCarStore.storeUserPicture(userPictureToUpload);

            }

        } catch (ServerException | ConnectionException | UnauthorizedException | NotFoundException | PersistenceException | IOException | ConflictException e) {
            Log.e(TAG, "Error during uploading user picture...", e);
            resultCode = SIGNUP_COMPLETED_WITHOUT_PICTURE_UPLOAD;
        }

        return resultCode;
    }


    @Override
    protected void onPostExecute(final Integer resultCode) {
        this.signupActivity.dismissDialog();

        switch (resultCode) {
            case SIGNUP_GENERIC_ERROR_RESULT:
                this.signupActivity.showServerGenericError();
                break;
            case SIGNUP_CONNECTION_ERROR_RESULT:
                this.signupActivity.showConnectionError();
                break;
            case SIGNUP_SERVER_ERROR_RESULT:
                this.signupActivity.showServerGenericError();
                break;
            case SIGNUP_USER_ALREADY_EXISTS_RESULT:
                this.signupActivity.showUserAlreadyExists();
                break;
            case SIGNUP_COMPLETED_RESULT:
                this.signupActivity.goToHome();
                break;
            case SIGNUP_COMPLETED_WITHOUT_PICTURE_UPLOAD:
                this.signupActivity.showUploadProblem();
                this.signupActivity.goToProfile();
                break;
            default:
                this.signupActivity.showServerGenericError();
        }
    }


    public boolean validateSignUpInput(User user) {

        boolean valid = true;
        if (!user.isEmailValid()) {
            this.signupActivity.showEmailMarkerError();
            valid = false;
        }

        if (!this.signupActivity.passwordsAreValid()) {
            this.signupActivity.showPasswordMarkerError();
            valid = false;
        }
        if (!user.isUsernameValid()) {
            this.signupActivity.showUsernameMarkerError();
            valid = false;
        }
        if (!user.isPhoneValid()) {
            this.signupActivity.showPhoneMarkerError();
            valid = false;
        }

        if (!valid) {
            this.signupActivity.showMissingFieldsMessage();
        } else if (!user.isAcceptedTerms()) {
            this.signupActivity.showNotAcceptTermsError();
            valid = false;
        }

        return valid;
    }

    public void fillUserForm(User user) {
        if (user.getName() != null)
            this.signupActivity.showUsername(user.getName());

        if (user.getEmail() != null) {
            this.signupActivity.showUserEmail(user.getEmail());
            this.signupActivity.disableEditTextEmail();
        }

        Bitmap userPhotoData = SocialCarApplication.getInstance().retrieveCacheImage(user.getSocialProvider().getSocialID());

        if (userPhotoData != null) {
            this.signupActivity.showProfilePicture(userPhotoData);
            SocialCarApplication.getInstance().removeCacheImage(user.getSocialProvider().getSocialID());
        }
    }
}
