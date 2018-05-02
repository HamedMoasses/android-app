package eu.h2020.sc.ui.lift;

import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.domain.User;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */

public interface CreateFeedbackObserver {

    void showGenericError();

    void dismissDialog();

    void showNoConnection();

    void showUnauthorizedError();

    void goToSignInActivity();

    void onFeedbackSuccess(Feedback feedbackCreated);

    Lift getLift();

    User getReviewed();

    String getFeedbackText();

}
