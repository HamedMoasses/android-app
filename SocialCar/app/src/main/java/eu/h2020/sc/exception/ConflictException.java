/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.exception;

/**
 * Created by fminori on 19/09/16.
 */

public class ConflictException extends Exception {

    public ConflictException() {
        super();
    }

    public ConflictException(String detailMessage) {
        super(detailMessage);
    }

    public ConflictException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ConflictException(Throwable throwable) {
        super(throwable);
    }
}
