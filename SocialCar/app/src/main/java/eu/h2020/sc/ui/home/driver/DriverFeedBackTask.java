package eu.h2020.sc.ui.home.driver;

import android.os.AsyncTask;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.FeedbackDAO;
import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.domain.UserType;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;

import java.util.List;

/**
 * Created by Pietro on 27/09/16.
 */

public class DriverFeedBackTask extends AsyncTask<User, Void, Integer> {

    private static final int FEEDBACK_CONNECTION_ERROR_RESULT = 0;
    private static final int FEEDBACK_SERVER_ERROR_RESULT = 1;
    private static final int FEEDBACK_UNAUTHORIZED_RESULT = 2;
    private static final int FEEDBACK_NOT_FOUND_RESULT = 3;
    private static final int FEEDBACK_COMPLETED_RESULT = 4;

    private FeedbackDAO feedbackDAO;
    private DriverDetailsActivity driverDetailsActivity;
    private List<Feedback> driverFeedBackList;

    public DriverFeedBackTask(DriverDetailsActivity driverDetailsActivity) {
        this.feedbackDAO = new FeedbackDAO();
        this.driverDetailsActivity = driverDetailsActivity;
    }

    @Override
    protected Integer doInBackground(User... drivers) {

        User driver = drivers[0];

        try {
            this.driverFeedBackList = this.feedbackDAO.findFeedBack(driver.getId(), UserType.DRIVER);
        } catch (ServerException e) {
            return FEEDBACK_SERVER_ERROR_RESULT;
        } catch (ConnectionException e) {
            return FEEDBACK_CONNECTION_ERROR_RESULT;
        } catch (UnauthorizedException e) {
            return FEEDBACK_UNAUTHORIZED_RESULT;
        } catch (NotFoundException e) {
            return FEEDBACK_NOT_FOUND_RESULT;
        }

        return FEEDBACK_COMPLETED_RESULT;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        this.driverDetailsActivity.dismissDialog();

        switch (resultCode) {
            case FEEDBACK_SERVER_ERROR_RESULT:
                this.driverDetailsActivity.setErrorMessage(this.driverDetailsActivity.getResources().getString(R.string.generic_error));
                this.driverDetailsActivity.showErrorLayout();
                break;
            case FEEDBACK_CONNECTION_ERROR_RESULT:
                this.driverDetailsActivity.setErrorMessage(this.driverDetailsActivity.getResources().getString(R.string.no_connection_error));
                this.driverDetailsActivity.showErrorLayout();
                break;
            case FEEDBACK_NOT_FOUND_RESULT:
                this.driverDetailsActivity.showErrorLayout();
                break;
            case FEEDBACK_UNAUTHORIZED_RESULT:
                this.driverDetailsActivity.showUnauthorizedError();
                SocialCarApplication.getInstance().removeAllUserInfo();
                this.driverDetailsActivity.goToSignInActivity();
                break;
            case FEEDBACK_COMPLETED_RESULT:
                this.driverDetailsActivity.showDriverFeedback(this.driverFeedBackList);
                this.driverDetailsActivity.showActivityLayout();
                break;
        }
    }
}
