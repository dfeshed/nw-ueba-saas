package fortscale.utils.airflow;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.utils.airflow.message.DagState;
import fortscale.utils.airflow.service.AirflowApiClient;
import fortscale.utils.airflow.service.AirflowApiClientImpl;
import fortscale.utils.airflow.service.AirflowClientConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

/**
 * Created by barak_schuster on 9/13/17.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AirflowClientConfig.class)
public class AirflowApiClientImplTest {

    @Value("${presidio.airflow.restApi.url}")
    private String airflowRestApiBaseUrl;
    @Autowired
    public AirflowApiClient airflowApiClient;

    @Autowired
    public RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void test() {
        mockServer.expect(requestTo(airflowRestApiBaseUrl));
        airflowApiClient.getDagExecutionDatesByState( DagState.RUNNING);
    }




}