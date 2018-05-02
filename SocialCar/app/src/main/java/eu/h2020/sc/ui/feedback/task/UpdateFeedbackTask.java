package eu.h2020.sc.ui.feedback.task;

import android.os.AsyncTask;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.FeedbackDAO;
import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.domain.Question;
import eu.h2020.sc.domain.RatingFeature;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.ui.feedback.RatingSurveyActivity;
import eu.h2020.sc.ui.lift.LiftActivity;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.WidgetHelper;

import java.util.Map;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */

public class UpdateFeedbackTask extends AsyncTask<Void, Void, Integer> {

    private static final int UPDATE_FEEDBACK_GENERIC_ERROR_RESULT = 0;
    private static final int UPDATE_FEEDBACK_CONNECTION_ERROR_RESULT = 1;
    private static final int UPDATE_SERVER_ERROR_RESULT = 2;
    private static final int UPDATE_FEEDBACK_UNAUTHORIZED = 3;
    private static final int UPDATE_FEEDBACK_NOT_FOUND = 4;
    private static final int UPDATE_FEEDBACK_COMPLETED = 5;

    //TODO: REMOVE AFTER TEST A!
    private static final int FEEDBACK_IS_NOT_COMPLETE = 6;

    private RatingSurveyActivity activity;
    private FeedbackDAO feedbackDAO;

    public UpdateFeedbackTask(RatingSurveyActivity activity) {
        this.activity = activity;
        this.feedbackDAO = new FeedbackDAO();
    }

    @Override
    protected Integer doInBackground(Void... voids) {

        Feedback feedback = retrieveFeedback();

        if (!this.verifyAnswers(feedback))
            return FEEDBACK_IS_NOT_COMPLETE;

        try {
            this.feedbackDAO.update(feedback);
        } catch (ConnectionException e) {
            return UPDATE_FEEDBACK_CONNECTION_ERROR_RESULT;
        } catch (NotFoundException e) {
            return UPDATE_FEEDBACK_NOT_FOUND;
        } catch (UnauthorizedException e) {
            return UPDATE_FEEDBACK_UNAUTHORIZED;
        } catch (ServerException e) {
            return UPDATE_FEEDBACK_GENERIC_ERROR_RESULT;
        }
        return UPDATE_FEEDBACK_COMPLETED;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        this.activity.dismissDialog();

        switch (resultCode) {
            case UPDATE_FEEDBACK_COMPLETED:
                WidgetHelper.showToast(this.activity, this.activity.getString(R.string.thanks_survey));
                ActivityUtils.openActivityNoBack(this.activity, LiftActivity.class);
                break;
            case UPDATE_FEEDBACK_GENERIC_ERROR_RESULT:
                this.activity.showServerGenericError();
                break;
            case UPDATE_FEEDBACK_CONNECTION_ERROR_RESULT:
                this.activity.showConnectionError();
                break;
            case UPDATE_SERVER_ERROR_RESULT:
                this.activity.showServerGenericError();
                break;
            case UPDATE_FEEDBACK_UNAUTHORIZED:
                SocialCarApplication.getInstance().removeAllUserInfo();
                this.activity.showUnauthorizedError();
                this.activity.goToSignInActivity();
                break;
            case UPDATE_FEEDBACK_NOT_FOUND:
                this.activity.showServerGenericError();
                break;

            //TODO : REMOVE THIS METHOD AFTER TEST A!
            case FEEDBACK_IS_NOT_COMPLETE:
                this.activity.showNoCompleteFeedback();
                break;
        }
    }

    private Feedback retrieveFeedback() {
        Map<String, Question> ratings = this.activity.getQuestions();
        RatingFeature ratingFeature = new RatingFeature();
        for (String keyQuestion : ratings.keySet()) {
            switch (keyQuestion) {
                case RatingSurveyActivity.COMFORT_LEVEL_KEY:
                    ratingFeature.setComfortLevel(ratings.get(keyQuestion).getRating());
                    break;
                case RatingSurveyActivity.ROUTE_KEY:
                    ratingFeature.setRoute(ratings.get(keyQuestion).getRating());
                    break;
                case RatingSurveyActivity.DRIVING_BEHAVIOUR_KEY:
                    ratingFeature.setDrivingBehaviour(ratings.get(keyQuestion).getRating());
                    break;
                case RatingSurveyActivity.DURATION_KEY:
                    ratingFeature.setDuration(ratings.get(keyQuestion).getRating());
                    break;
                case RatingSurveyActivity.SATISFACTION_BEHAVIOUR_KEY:
                    ratingFeature.setSatisfactionLevel(ratings.get(keyQuestion).getRating());
                    break;
                case RatingSurveyActivity.PUNCTUATION_KEY:
                    ratingFeature.setPunctuation(ratings.get(keyQuestion).getRating());
                    break;
                case RatingSurveyActivity.CARPOOLER_BEHAVIOUR_KEY:
                    ratingFeature.setCarpoolerBehaviour(ratings.get(keyQuestion).getRating());
                    break;
            }
        }
        Feedback feedback = this.activity.getFeedback();
        feedback.setRatings(ratingFeature);
        return feedback;
    }

    //TODO : REMOVE THIS METHOD AFTER TEST A!
    private boolean verifyAnswers(Feedback feedback) {
        if (feedback.getRole().equals(Feedback.ROLE_DRIVER)) {
            return feedback.getRatings().getPunctuation() != null && feedback.getRatings().getSatisfactionLevel() != null && feedback.getRatings().getCarpoolerBehaviour() != null;
        } else {
            return feedback.getRatings().getComfortLevel() != null && feedback.getRatings().getRoute() != null && feedback.getRatings().getDuration() != null
                    && feedback.getRatings().getDrivingBehaviour() != null && feedback.getRatings().getSatisfactionLevel() != null && feedback.getRatings().getPunctuation() != null;
        }
    }

}
