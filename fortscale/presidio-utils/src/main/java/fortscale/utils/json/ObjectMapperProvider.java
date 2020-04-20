package fortscale.utils.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Created by barak_schuster on 9/24/17.
 */
public class ObjectMapperProvider {

    private static ObjectMapperProvider instance = null;

    private ObjectMapper noModulesObjectMapper;
    private ObjectMapper defaultObjectMapper;

    private ObjectMapperProvider() {
        defaultObjectMapper = defaultJsonObjectMapper();
        noModulesObjectMapper = noModulesObjectMapper();
    }

    public static ObjectMapperProvider getInstance() {
        if (instance == null) {
            instance = new ObjectMapperProvider();
        }
        return instance;
    }

    public ObjectMapper getDefaultObjectMapper() {
        return defaultObjectMapper;
    }

    public ObjectMapper getNoModulesObjectMapper() {
        return noModulesObjectMapper;
    }

    public static ObjectMapper defaultJsonObjectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) //ISODate
                .modules(new JavaTimeModule())
                .build();
    }

    public static ObjectMapper noModulesObjectMapper() {
        return new ObjectMapper();
    }

    public static ObjectMapper customJsonObjectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL) // Don’t include null values
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) //ISODate
                .modules(new JavaTimeModule())
                .build();
    }


}
