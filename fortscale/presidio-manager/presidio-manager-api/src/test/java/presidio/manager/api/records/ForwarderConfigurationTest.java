package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.json.ObjectMapperProvider;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ForwarderConfigurationTest {


    private final String FORWARDER_JSON_FILE_NAME = "forwarder_test.json";
    private final String BAD_FORWARDER_JSON_FILE_NAME = "too_many_params_forwarder.json";

    @Test
    public void createSyslogMessageSenderConfigurationTest() throws IOException {
        ObjectMapper mapper = ObjectMapperProvider.getInstance().getDefaultObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(FORWARDER_JSON_FILE_NAME).getFile());
        JsonNode jsonBody = mapper.readTree(file);
        OutputConfigurationCreation outputConfigurationCreation = new OutputConfigurationCreation(jsonBody);
        boolean isValid = outputConfigurationCreation.isStructureValid();
        List badParams = outputConfigurationCreation.getBadParams();
        Assert.assertEquals(true, isValid);
        Assert.assertEquals(0, badParams.size());
    }

    @Test
    public void createSyslogMessageSenderConfigurationWithBadJsonTest() throws IOException {
        ObjectMapper mapper = ObjectMapperProvider.getInstance().getDefaultObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(BAD_FORWARDER_JSON_FILE_NAME).getFile());
        JsonNode jsonBody = mapper.readTree(file);
        OutputConfigurationCreation outputConfigurationCreation = new OutputConfigurationCreation(jsonBody);
        boolean isValid = outputConfigurationCreation.isStructureValid();
        List badParams = outputConfigurationCreation.getBadParams();
        Assert.assertEquals(false, isValid);
        Assert.assertEquals(2, badParams.size());
    }

}
