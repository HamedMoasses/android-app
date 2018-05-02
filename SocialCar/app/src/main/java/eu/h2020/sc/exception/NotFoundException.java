/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.exception;

/**
 * Created by fminori on 19/09/16.
 */

public class NotFoundException extends Exception {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String detailMessage) {
        super(detailMessage);
    }

    public NotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NotFoundException(Throwable throwable) {
        super(throwable);
    }
}
