package eu.h2020.sc.ui.lift.passenger.lift.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.FeedbackDAO;
import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.ui.lift.CreateFeedbackObserver;

import java.util.Date;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */

public class CreateFeedbackTask extends AsyncTask<Void, Void, Integer> {

    private static final int CONNECTION_ERROR_RESULT = 0;
    private static final int SERVER_ERROR_RESULT = 1;
    private static final int UNAUTHORIZED_RESULT = 3;
    private static final int CONFLICT_RESULT = 4;
    private static final int GENERIC_ERROR = 5;

    private static final int COMPLETED_RESULT = 6;

    private FeedbackDAO feedbackDAO;
    private CreateFeedbackObserver observer;
    private Feedback feedbackCreated;
    private int rating;

    public CreateFeedbackTask(CreateFeedbackObserver observer) {
        this.feedbackDAO = new FeedbackDAO();
        this.observer = observer;
    }

    @Override
    protected Integer doInBackground(Void... voids) {

        Feedback feedback = retrieveFeedback();

        if (feedback != null) {
            try {
                this.feedbackCreated = this.feedbackDAO.createFeedback(feedback);
            } catch (ServerException e) {
                return SERVER_ERROR_RESULT;
            } catch (ConnectionException e) {
                return CONNECTION_ERROR_RESULT;
            } catch (UnauthorizedException e) {
                return UNAUTHORIZED_RESULT;
            } catch (ConflictException e) {
                return CONFLICT_RESULT;
            }
            return COMPLETED_RESULT;

        } else
            return GENERIC_ERROR;
    }


    @Override
    protected synchronized void onPostExecute(Integer resultCode) {

        this.observer.dismissDialog();

        switch (resultCode) {
            case SERVER_ERROR_RESULT:
            case GENERIC_ERROR:
            case CONFLICT_RESULT:
                this.observer.showGenericError();
                break;
            case CONNECTION_ERROR_RESULT:
                this.observer.showNoConnection();
                break;
            case UNAUTHORIZED_RESULT:
                SocialCarApplication.getInstance().removeAllUserInfo();
                this.observer.showUnauthorizedError();
                this.observer.goToSignInActivity();
                break;
            case COMPLETED_RESULT:
                this.observer.onFeedbackSuccess(this.feedbackCreated);
                break;
        }
    }

    private Feedback retrieveFeedback() {

        String role = this.getRole();
        String reviewerID = this.getReviewerID(role);

        Feedback feedback = new Feedback(reviewerID, this.observer.getReviewed().getId(), this.observer.getLift().getID(), this.rating, new Date(), role);
        feedback.setReview(this.observer.getFeedbackText());

        return feedback;
    }

    @NonNull
    private String getRole() {
        if (this.observer.getLift().getDriverID().equals(this.observer.getReviewed().getId()))
            return Feedback.ROLE_PASSENGER;
        else
            return Feedback.ROLE_DRIVER;
    }

    @NonNull
    private String getReviewerID(String role) {
        if (role.equals(Feedback.ROLE_PASSENGER))
            return this.observer.getLift().getPassengerID();
        else
            return this.observer.getLift().getDriverID();
    }

    public void setFeedbackRating(int rating) {
        this.rating = rating;
    }
}
