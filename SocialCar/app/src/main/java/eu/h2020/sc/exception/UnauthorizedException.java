/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.exception;

/**
 * Created by fminori on 16/09/16.
 */

public class UnauthorizedException extends Exception {

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
    }
}
