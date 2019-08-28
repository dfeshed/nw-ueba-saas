package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.utils.reflection.PresidioReflectionUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

abstract class TransformerJsonTest {

    private static final String TRANSFORMERS_PACKAGE_LOCATION = "presidio.input.core.services.transformation.transformer";
    private ClassLoader classLoader = getClass().getClassLoader();

    abstract String getResourceFilePath();
    abstract Class getTransformerClass();

    @Test
    public void testDeserializingTransformer() throws IOException {
        Assert.assertTrue(getTransformerClass().isInstance(loadTransformer(getResourceFilePath())));
    }

    private InputDocumentTransformer loadTransformer(String resourceFilePath) throws IOException {
        File file = new File(Objects.requireNonNull(classLoader.getResource(resourceFilePath)).getFile());
        ObjectMapper objectMapper = createObjectMapper();
        String json = FileUtils.readFileToString(file);
        Set<Class<? extends InputDocumentTransformer>> subTypes = PresidioReflectionUtils.getSubTypes(
                new String[]{TRANSFORMERS_PACKAGE_LOCATION},
                InputDocumentTransformer.class);
        Collection collect = subTypes.stream().map(x -> (Class) x).collect(Collectors.toList());
        objectMapper.registerSubtypes(collect);
        return objectMapper.readValue(json, InputDocumentTransformer.class);
    }


    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        return objectMapper;
    }
}
