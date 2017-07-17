package source.presidioHttpSource;

/**
 * Created by tomerd on 7/3/2017.
 */
public class ResponseCodes {


    /**
     * Status code (200) indicating the request succeeded normally.
     */

    public static final int OK = 200;

    /**
     * Status code (401) indicating that the request requires HTTP
     * authentication.
     */

    public static final int UNAUTHORIZED = 401;

    /**
     * Status code (429) indicating the request wasn't processed successfully due to system overload .
     */

    public static final int TOO_MANY_REQUESTS = 429;

    /**
     * Status code (500) indicating an error inside the HTTP server
     * which prevented it from fulfilling the request.
     */

    public static final int INTERNAL_SERVER_ERROR = 500;


}
