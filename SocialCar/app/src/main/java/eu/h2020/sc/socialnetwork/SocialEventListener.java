package eu.h2020.sc.socialnetwork;

import eu.h2020.sc.domain.socialnetwork.SocialUser;

/**
 * Created by fabiolombardi on 25/01/17.
 */

public interface SocialEventListener {

    void onAuthenticationSuccess(SocialUser socialUser);
    void onAuthenticationError();
    void onAuthenticationCancel();
}
