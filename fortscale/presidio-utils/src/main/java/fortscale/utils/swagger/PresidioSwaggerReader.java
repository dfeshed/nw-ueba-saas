package fortscale.utils.swagger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.kongchen.swagger.docgen.reader.SpringMvcApiReader;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import org.apache.maven.plugin.logging.Log;

/**
 * Custom swagger reader
 * This reader can be used to custom the generated swagger output spec
 */
public class PresidioSwaggerReader extends SpringMvcApiReader {

    public PresidioSwaggerReader(Swagger swagger, Log log) {
        super(swagger, log);

        // configure jackson to serialize objects that do not have a getter or setters
        Json.mapper().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // configure Jackson to show JSR 310 instants in ISO 8601 format
        Json.mapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Json.mapper().registerModule(new JavaTimeModule());
    }

}
