package eu.h2020.sc.transport;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class HttpConstants {

    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";


    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_IMAGE_PNG = "image/jpeg";

    public static final int UNPROCESSABLE_ENTITY = 422;

    public static final String CONNECTION = "Connection";
    public static final String CONNECTION_VALUE = "Keep-Alive";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CACHE_CONTROL_VALUE = "no-cache";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String AUTHORIZATION = "Authorization";
    public static final String USER_AGENT = "User-Agent";

    //public static final int TIMEOUT_TIME = 10 * 1000;
    public static final int TIMEOUT_TIME = 40 * 1000;

}
