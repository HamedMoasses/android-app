package eu.h2020.sc.location;

/**
 * Created by pietro on 13/03/2018.
 */

public interface PositionTrackingCallback {

    void onStarted();

    void onStopped();

    void networkError();

    void genericError(int errorType);


}
