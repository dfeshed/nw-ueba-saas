package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.utils.reflection.PresidioReflectionUtils;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class TransformerJsonTest {

    private static final String TRANSFORMERS_UTIL_PACKAGE_LOCATION = "fortscale.utils.transform";
    private static final String TRANSFORMERS_INPUT_PACKAGE_LOCATION = "presidio.input.core.services.transformation.transformer";
    private static final String END_DATE = "endDate";
    private ClassLoader classLoader = getClass().getClassLoader();

    abstract String getResourceFilePath();
    abstract Class getTransformerClass();

    @Test
    public void testDeserializingTransformer() throws IOException {
        Assert.assertTrue(getTransformerClass().isInstance(loadTransformer(getResourceFilePath())));
    }

    private AbstractJsonObjectTransformer loadTransformer(String resourceFilePath) throws IOException {
        File file = new File(Objects.requireNonNull(classLoader.getResource(resourceFilePath)).getFile());
        ObjectMapper objectMapper = createObjectMapper();
        String json = FileUtils.readFileToString(file);
        Collection<Class<? extends AbstractJsonObjectTransformer>> subTypes = PresidioReflectionUtils.getSubTypes(
                new String[]{TRANSFORMERS_UTIL_PACKAGE_LOCATION, TRANSFORMERS_INPUT_PACKAGE_LOCATION},
                AbstractJsonObjectTransformer.class);
        Collection<Class<?>> collect = subTypes.stream().map(x -> (Class<?>) x).collect(Collectors.toList());
        objectMapper.registerSubtypes(collect);
        return objectMapper.readValue(json, AbstractJsonObjectTransformer.class);
    }


    protected ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue(END_DATE, Instant.now());
        objectMapper.setInjectableValues(injectableValues);
        return objectMapper;
    }

    protected AbstractInputDocument transformEvent(AbstractInputDocument rawEvent,
                                                 AbstractJsonObjectTransformer transformer,
                                                 Class<? extends AbstractInputDocument> clazz) throws IOException {
        ObjectMapper mapper = createObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        JSONObject jsonObject = new JSONObject(mapper.writeValueAsString(rawEvent));
        JSONObject transformed = transformer.transform(jsonObject);
        return mapper.readValue(transformed.toString(), clazz);
    }
}
