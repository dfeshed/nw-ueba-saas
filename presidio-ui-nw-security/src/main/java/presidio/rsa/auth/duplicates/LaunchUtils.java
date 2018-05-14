package presidio.rsa.auth.duplicates;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Framework utility methods for working with Launch components, configuration and other support
 * classes.
 *
 * @author Abram Thielke
 * @since 0.9
 */
public final class LaunchUtils {

    /**
     * A reusable mapper for JSON serialization.
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
