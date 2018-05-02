package eu.h2020.sc.socialnetwork.facebook;

import com.facebook.FacebookException;
import com.facebook.share.Sharer;

public interface ShareEventListener {

    void onSuccess(Sharer.Result result);

    void onError(FacebookException error);

    void onCancel();
}
