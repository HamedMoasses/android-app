package eu.h2020.sc;

import android.os.AsyncTask;

import eu.h2020.sc.dao.UserDAO;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.socialnetwork.facebook.FacebookManager;
import eu.h2020.sc.socialnetwork.googleplus.GooglePlusManager;

/**
 * Created by Pietro on 22/09/16.
 */

public class LogoutTask extends AsyncTask<Void, Void, Integer> {

    private static final int LOGOUT_GENERIC_ERROR_RESULT = 0;
    private static final int LOGOUT_CONNECTION_ERROR_RESULT = 1;
    private static final int LOGOUT_SERVER_ERROR_RESULT = 2;
    private static final int LOGOUT_UNAUTHORIZED = 3;
    private static final int LOGOUT_NOT_FOUND = 4;
    private static final int LOGOUT_COMPLETED_RESULT = 5;

    private static final String FCM_TOKE_EMPTY = "NOT_INITIALIZED";

    private GeneralActivity generalActivity;
    private UserDAO userDAO;
    private SocialCarStore socialCarStore;

    public LogoutTask(GeneralActivity generalActivity) {
        this.generalActivity = generalActivity;
        this.userDAO = new UserDAO();
        this.socialCarStore = SocialCarApplication.getInstance();
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        Integer resultCode = LOGOUT_COMPLETED_RESULT;

        User user = this.socialCarStore.getUser();
        user.setFcmToken(FCM_TOKE_EMPTY);

        try {
            this.userDAO.updateUser(user);
            this.socialCarStore.removeAllUserInfo();
            this.doSocialLogout(user);
        } catch (ServerException e) {
            resultCode = LOGOUT_SERVER_ERROR_RESULT;
        } catch (ConnectionException e) {
            resultCode = LOGOUT_CONNECTION_ERROR_RESULT;
        } catch (UnauthorizedException e) {
            resultCode = LOGOUT_UNAUTHORIZED;
        } catch (NotFoundException e) {
            resultCode = LOGOUT_NOT_FOUND;
        }
        return resultCode;
    }

    private void doSocialLogout(User user) {
        if (user.getSocialProvider() != null && user.getSocialProvider().getSocialNetwork() != null) {
            switch (user.getSocialProvider().getSocialNetwork()) {
                case FACEBOOK:
                    FacebookManager.signOut();
                    break;
                case GOOGLE_PLUS:
                    GooglePlusManager.signOut();
            }
        }
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        this.generalActivity.dismissDialog();

        switch (resultCode) {
            case LOGOUT_GENERIC_ERROR_RESULT:
                this.generalActivity.showServerGenericError();
                break;
            case LOGOUT_CONNECTION_ERROR_RESULT:
                this.generalActivity.showConnectionError();
                break;
            case LOGOUT_SERVER_ERROR_RESULT:
                this.generalActivity.showServerGenericError();
                break;
            case LOGOUT_NOT_FOUND:
                this.generalActivity.showServerGenericError();
                break;
            case LOGOUT_UNAUTHORIZED:
                this.socialCarStore.removeAllUserInfo();
                this.generalActivity.showUnauthorizedError();
                this.generalActivity.goToSignInActivity();
                break;
            case LOGOUT_COMPLETED_RESULT:
                this.generalActivity.goToSocialSignIn();
                break;
            default:
                this.generalActivity.showServerGenericError();
        }
    }
}
