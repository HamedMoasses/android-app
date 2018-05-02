package eu.h2020.sc.transport;

/**
 * Created by fminori on 12/09/16.
 */
public interface PostTaskListener {

    /**
     * HTTP STATUS CODE 201
     */
    void onCreated(HttpTaskResult taskResult);


    /**
     * HTTP STATUS CODE 401
     */
    void onUnauthorized();

    /**
     * HTTP STATUS CODE 409
     */
    void onConflict();

    /*
     * HTTP STATUS CODE 500
     */
    void onInternalServerError();


    /**
     * NO INTERNET CONNECTION
     */
    void onConnectionError();


}
