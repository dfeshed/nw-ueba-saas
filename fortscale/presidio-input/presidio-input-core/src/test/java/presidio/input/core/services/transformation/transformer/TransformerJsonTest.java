package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class TransformerJsonTest {

    private static final String TRANSFORMERS_UTIL_PACKAGE_LOCATION = "fortscale.utils.transform";
    private static final String TRANSFORMERS_INPUT_PACKAGE_LOCATION = "presidio.input.core.services.transformation.transformer";
    private static final String END_DATE = "endDate";

    private ClassLoader classLoader = getClass().getClassLoader();
    private ObjectMapper objectMapper = createObjectMapper();

    abstract String getResourceFilePath();
    abstract Class getTransformerClass();

    @Test
    public void testDeserializingTransformer() throws IOException {
        Assert.assertTrue(getTransformerClass().isInstance(loadTransformer(getResourceFilePath())));
    }

    protected List<AbstractJsonObjectTransformer> loadTransformers(String resourceFilePath) throws IOException {
        String json = readFileToStr(resourceFilePath);
        return objectMapper.readValue(json, new TypeReference<List<AbstractJsonObjectTransformer>>(){});
    }


    protected ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue(END_DATE, Instant.now());
        objectMapper.setInjectableValues(injectableValues);
        Collection<Class<? extends AbstractJsonObjectTransformer>> subTypes = PresidioReflectionUtils.getSubTypes(
                new String[]{TRANSFORMERS_UTIL_PACKAGE_LOCATION, TRANSFORMERS_INPUT_PACKAGE_LOCATION},
                AbstractJsonObjectTransformer.class);
        Collection<Class<?>> collect = subTypes.stream().map(x -> (Class<?>) x).collect(Collectors.toList());
        objectMapper.registerSubtypes(collect);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    protected AbstractInputDocument transformEvent(AbstractInputDocument rawEvent,
                                                 AbstractJsonObjectTransformer transformer,
                                                 Class<? extends AbstractInputDocument> clazz) throws IOException {
        JSONObject jsonObject = new JSONObject(objectMapper.writeValueAsString(rawEvent));
        JSONObject transformed = transformer.transform(jsonObject);
        return objectMapper.readValue(transformed.toString(), clazz);
    }

    private AbstractJsonObjectTransformer loadTransformer(String resourceFilePath) throws IOException {
        String json = readFileToStr(resourceFilePath);
        return objectMapper.readValue(json, AbstractJsonObjectTransformer.class);
    }

    private String readFileToStr(String resourceFilePath) throws IOException {
        File file = new File(Objects.requireNonNull(classLoader.getResource(resourceFilePath)).getFile());
        return FileUtils.readFileToString(file);
    }
}
