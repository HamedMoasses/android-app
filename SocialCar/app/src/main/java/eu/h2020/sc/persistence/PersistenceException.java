/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.persistence;

/**
 * Created by fminori on 19/09/16.
 */

public class PersistenceException extends Exception {

    public PersistenceException() {
    }

    public PersistenceException(String detailMessage) {
        super(detailMessage);
    }

    public PersistenceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public PersistenceException(Throwable throwable) {
        super(throwable);
    }
}
