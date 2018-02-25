package presidio.manager.airlfow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.manager.airlfow.spring.AirflowConfiguration;
import presidio.manager.api.records.DataPipeLineConfiguration;
import presidio.manager.api.records.OutputConfigurationCreator;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.PresidioSystemConfiguration;
import presidio.manager.api.records.ValidationResults;

import java.io.IOException;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AirflowConfiguration.class})
public class ConfigurationAirflowServiceTest {

    @Autowired
    ConfigurationAirflowService configurationAirflowService;

    @Before
    public void before() {

    }

    @Test
    public void validConfiguration() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        Instant startTime = Instant.parse("2007-12-03T10:15:30Z");
        JsonNode jsonNode = mapper.readTree("{\"schemas\": [\"FILE\"],\"startTime\": \"" + startTime.toString() + "\"}");
        JsonNode jsonNode2 = mapper.readTree("{\"syslog\": {\"alert\": {\"host\": \"test\",\"port\": \"1\"},\"user\": {\"host\": \"testTest\",\"port\": \"2\"}}}");
        DataPipeLineConfiguration dataPipeline = new DataPipeLineConfiguration(jsonNode);
        PresidioSystemConfiguration systemConf = new PresidioSystemConfiguration();
        OutputConfigurationCreator outputConfigurationCreator = new OutputConfigurationCreator(jsonNode2);
        PresidioManagerConfiguration presidioManagerConfiguration = new PresidioManagerConfiguration(dataPipeline, systemConf, outputConfigurationCreator);
        ValidationResults validationResults = configurationAirflowService.validateConfiguration(presidioManagerConfiguration);

        assertTrue(CollectionUtils.isEmpty(validationResults.getErrorsList()));
        assertEquals(startTime.toString(), dataPipeline.getStartTime());
    }
}
