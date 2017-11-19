package presidio.webapp.swagger;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.jackson.AbstractModelConverter;
import io.swagger.models.Model;
import io.swagger.models.properties.AbstractProperty;
import io.swagger.models.properties.Property;
import io.swagger.util.Json;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Iterator;

public class DateTimeModelConverter extends AbstractModelConverter {

    public DateTimeModelConverter() {
        super(new ObjectMapper());
    }

    @Override
    public Property resolveProperty(Type type, ModelConverterContext context, Annotation[] annotations, Iterator<ModelConverter> chain) {
        JavaType _type = Json.mapper().constructType(type);
        if (_type != null) {
            Class<?> cls = _type.getRawClass();
            if (Instant.class.isAssignableFrom(cls)) {
                return new InstantProperty();
            }
        }
        if (chain.hasNext()) {
            return chain.next().resolveProperty(type, context, annotations, chain);
        } else {
            return null;
        }
    }

    public static class InstantProperty extends AbstractProperty {

        public static final String PROPERTY_TYPE = "string";
        public static final String PROPERTY_FORMAT = "date-time";
        public static final String PROPERTY_DESC = "Date time in ISO 8601 format UTC (yyyy-MM-ddTHH:mm:ssZ)";
        public static final String PROPERTY_EXAMPLE = "2007-12-03T10:00:00.00Z";

        public InstantProperty() {
            this.setType(PROPERTY_TYPE);
            this.setFormat(PROPERTY_FORMAT);
            this.setExample(PROPERTY_EXAMPLE); //date in ISO8601 format
            this.setDefault(PROPERTY_DESC);
        }
    }
}


