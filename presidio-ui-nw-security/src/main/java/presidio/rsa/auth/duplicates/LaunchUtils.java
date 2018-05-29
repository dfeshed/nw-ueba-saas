package presidio.rsa.auth.duplicates;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Copied from LunchUtils - need to decide how to reuse code with being depended on all launch project
 * TODO: https://bedfordjira.na.rsa.net/browse/ASOC-55722
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
