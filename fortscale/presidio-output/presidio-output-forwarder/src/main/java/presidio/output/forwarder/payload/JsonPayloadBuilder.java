package presidio.output.forwarder.payload;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JsonPayloadBuilder<T> implements PayloadBuilder<T> {

    public static final String INCLUDE_PROPERTIES_FILTER = "includePropertiesFilter";

    private ObjectMapper mapper;

    public JsonPayloadBuilder(Class<T> target, Class<?> mixinSource) {
        mapper = configureJackson();
        mapper.addMixIn(target, mixinSource);
    }

    public JsonPayloadBuilder(Class<T> target, Class<?> mixinSource, Class type, JsonSerializer ser) {
        this(target, mixinSource);
        SimpleModule module = new SimpleModule();
        module.addSerializer(type, ser);
        mapper.registerModule(module);
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
