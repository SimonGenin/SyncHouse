package be.simongenin.synchouse.utils;

/**
 * @author Simon Genin
 *
 * Contains utilities about the server.
 */
public class ServerUtils {

    /**
     * Server base URL
     */
    public static final String BASE_URL = "http://synchouse.toum.ovh/";

    /**
     * Address to reach when we want to persist a token
     */
    public static final String TOKEN_URL = BASE_URL  + "token";

    /**
     * Address to reach for the login
     */
    public static final String LOGIN_URL = BASE_URL + "house/login";

    /**
     * Address to reach to send a status code
     */
    public static final String STATUS = BASE_URL + "status";

}
