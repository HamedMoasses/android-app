package eu.h2020.sc.ui.profile;

import android.os.AsyncTask;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.UserDAO;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.persistence.SocialCarStore;

/**
 * Created by Pietro on 22/09/16.
 */

public class UpdateUserProfileTask extends AsyncTask<User, Void, Integer> {

    private static final int EDIT_PROFILE_GENERIC_ERROR_RESULT = 0;
    private static final int EDIT_PROFILE_CONNECTION_ERROR_RESULT = 1;
    private static final int EDIT_PROFILE_SERVER_ERROR_RESULT = 2;
    private static final int EDIT_PROFILE_UNAUTHORIZED = 3;
    private static final int EDIT_PROFILE_NOT_FOUND = 4;
    private static final int EDIT_PROFILE_COMPLETED_RESULT = 5;

    private ProfileActivity profileActivity;
    private UserDAO userDAO;
    private SocialCarStore socialCarStore;

    public UpdateUserProfileTask(ProfileActivity profileActivity) {
        this.profileActivity = profileActivity;
        this.userDAO = new UserDAO();
        this.socialCarStore = SocialCarApplication.getInstance();
    }

    @Override
    protected Integer doInBackground(User... users) {

        Integer resultCode = EDIT_PROFILE_COMPLETED_RESULT;

        User user = users[0];

        try {
            this.userDAO.updateUser(user);
            this.socialCarStore.store(user);
        } catch (ServerException e) {
            return EDIT_PROFILE_SERVER_ERROR_RESULT;
        } catch (ConnectionException e) {
            return EDIT_PROFILE_CONNECTION_ERROR_RESULT;
        } catch (UnauthorizedException e) {
            return EDIT_PROFILE_UNAUTHORIZED;
        } catch (NotFoundException e) {
            return EDIT_PROFILE_NOT_FOUND;
        }
        return resultCode;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        switch (resultCode) {
            case EDIT_PROFILE_GENERIC_ERROR_RESULT:
            case EDIT_PROFILE_SERVER_ERROR_RESULT:
            case EDIT_PROFILE_NOT_FOUND:
                this.profileActivity.showServerGenericError();
                break;
            case EDIT_PROFILE_CONNECTION_ERROR_RESULT:
                this.profileActivity.showConnectionError();
                break;
            case EDIT_PROFILE_UNAUTHORIZED:
                this.socialCarStore.removeAllUserInfo();
                this.profileActivity.showUnauthorizedError();
                this.profileActivity.goToSignInActivity();
                break;
            case EDIT_PROFILE_COMPLETED_RESULT:
                this.profileActivity.refreshActivity();
                break;
            default:
                this.profileActivity.showServerGenericError();
        }

        this.profileActivity.dismissDialog();
    }

    public boolean validate(User user) {

        boolean valid = true;

        if (!user.isUsernameValid()) {
            this.profileActivity.showUsernameMarkerError();
            valid = false;
        }
        if (!user.isPhoneValid()) {
            this.profileActivity.showPhoneMarkerError();
            valid = false;
        }
        if (!valid)
            this.profileActivity.showMissingFieldsMessage();

        return valid;
    }
}
