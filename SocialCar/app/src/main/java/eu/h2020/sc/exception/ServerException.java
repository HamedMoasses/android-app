/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.exception;

/**
 * Created by fminori on 16/09/16.
 */

public class ServerException extends Exception {

    public ServerException() {
        super();
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }


}
