package presidio.output.forwarder.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

public class JsonPayloadBuilder<T> implements PayloadBuilder<T> {

    public static final String INCLUDE_PROPERTIES_FILTER = "includePropertiesFilter";

    private ObjectMapper mapper;

    public JsonPayloadBuilder(Class<T> target, Class<?> mixinSource) {
        mapper = configureJackson();
        mapper.addMixIn(target, mixinSource);
    }


    @Override
    public String buildPayload(T object) throws Exception {
        return mapper.writeValueAsString(object);
    }

    private ObjectMapper configureJackson() {
        ObjectMapper mapper = new ObjectMapper();

        //IncludePropertiesFilter
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter(INCLUDE_PROPERTIES_FILTER, new IncludePropertiesFilter());
        mapper.setFilters(filterProvider);

        //ISODate
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);

        return  mapper;
    }

    class IncludePropertiesFilter extends SimpleBeanPropertyFilter {

        @Override
        protected boolean include(final PropertyWriter writer) {
            final JsonIncludeProperties includeProperties =
                    writer.getContextAnnotation(JsonIncludeProperties.class);
            if (includeProperties != null) {
                return Arrays.asList(includeProperties.value()).contains(writer.getName());
            }
            return super.include(writer);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface JsonIncludeProperties {
        String[] value();
    }

}
