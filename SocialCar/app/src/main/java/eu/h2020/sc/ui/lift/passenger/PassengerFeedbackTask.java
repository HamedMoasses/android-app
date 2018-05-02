package eu.h2020.sc.ui.lift.passenger;

import android.os.AsyncTask;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.FeedbackDAO;
import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.domain.UserType;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.ui.lift.AcceptanceLiftActivity;

import java.util.List;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class PassengerFeedbackTask extends AsyncTask<String, Void, Integer> {

    private static final int CONNECTION_ERROR_RESULT = 0;
    private static final int SERVER_ERROR_RESULT = 1;
    private static final int UNAUTHORIZED_RESULT = 2;
    private static final int NOT_FOUND_RESULT = 3;
    private static final int COMPLETED_RESULT = 4;

    private AcceptanceLiftActivity acceptanceLiftActivity;
    private FeedbackDAO feedbackDAO;
    private List<Feedback> feedbackList;

    public PassengerFeedbackTask(AcceptanceLiftActivity acceptanceLiftActivity) {
        this.feedbackDAO = new FeedbackDAO();
        this.acceptanceLiftActivity = acceptanceLiftActivity;
    }

    @Override
    protected Integer doInBackground(String... id) {

        String userID = id[0];

        try {
            this.feedbackList = this.feedbackDAO.findFeedBack(userID, UserType.PASSENGER);
        } catch (ServerException e) {
            return SERVER_ERROR_RESULT;
        } catch (ConnectionException e) {
            return CONNECTION_ERROR_RESULT;
        } catch (UnauthorizedException e) {
            return UNAUTHORIZED_RESULT;
        } catch (NotFoundException e) {
            return NOT_FOUND_RESULT;
        }
        return COMPLETED_RESULT;
    }

    @Override
    protected synchronized void onPostExecute(Integer resultCode) {

        switch (resultCode) {
            case SERVER_ERROR_RESULT:
            case NOT_FOUND_RESULT:
                this.acceptanceLiftActivity.showServerGenericError();
                break;
            case CONNECTION_ERROR_RESULT:
                this.acceptanceLiftActivity.showConnectionError();
                break;
            case UNAUTHORIZED_RESULT:
                SocialCarApplication.getInstance().removeAllUserInfo();
                this.acceptanceLiftActivity.showUnauthorizedError();
                break;
            case COMPLETED_RESULT:
                this.acceptanceLiftActivity.showFeedback(this.feedbackList);
                this.acceptanceLiftActivity.dismissDialog();
                break;
        }
    }
}
