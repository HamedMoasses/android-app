/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.exception;

/**
 * Created by fminori on 16/09/16.
 */

public class ConnectionException extends Exception {

    public ConnectionException() {
        super();
    }

    public ConnectionException(String detailMessage) {
        super(detailMessage);
    }

    public ConnectionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ConnectionException(Throwable throwable) {
        super(throwable);
    }
}
