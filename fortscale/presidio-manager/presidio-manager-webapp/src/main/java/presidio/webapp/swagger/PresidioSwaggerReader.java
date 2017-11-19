package presidio.webapp.swagger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.kongchen.swagger.docgen.reader.SpringMvcApiReader;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import org.apache.maven.plugin.logging.Log;

public class PresidioSwaggerReader extends SpringMvcApiReader {

    public PresidioSwaggerReader(Swagger swagger, Log log) {
        super(swagger, log);
        Json.mapper().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        Json.mapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Json.mapper().registerModule(new JavaTimeModule());
    }

}
