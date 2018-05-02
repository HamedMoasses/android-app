package eu.h2020.sc.transport;

/**
 * Created by fminori on 24/09/15.
 */
public interface HttpTaskListener {

    /**
     * HTTP STATUS CODE 200
     */
    void onSuccess(HttpTaskResult taskResult);

    /**
     * HTTP STATUS CODE 201
     */
    void onCreated(HttpTaskResult taskResult);


    /**
     * HTTP STATUS CODE 204
     */
    void onNoContent();

    /**
     * HTTP STATUS CODE 409
     */
    void onConflict();

    /*
     * HTTP STATUS CODE 500
     */
    void onInternalServerError();

    /**
     * HTTP STATUS CODE 404
     */
    void onNotFound();

    /**
     * NO INTERNET CONNECTION
     */
    void onConnectionError();

    /**
     * HTTP STATUS CODE 401
     */
    void onUnauthorized();

}
