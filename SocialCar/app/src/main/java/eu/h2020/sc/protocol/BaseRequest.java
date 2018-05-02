/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.protocol;

import java.net.URL;

/**
 * Created by fminori on 09/09/16.
 */

public abstract class BaseRequest {

    public abstract String getHttpMethod();

    public abstract URL urllize();
}
