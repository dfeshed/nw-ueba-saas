package fortscale.utils.airflow;

import com.google.common.collect.Lists;
import fortscale.utils.airflow.message.DagState;
import fortscale.utils.airflow.service.AirflowApiClient;
import fortscale.utils.airflow.service.AirflowClientConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by barak_schuster on 9/13/17.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AirflowClientConfig.class)
public class AirflowApiClientImplTest {

    private final String DAG_EXECUTION_DATES_BY_STATE_URL ="http://localhost:8000/admin/rest_api/api?api=dag_execution_dates_for_state&state=running";
    private final String DAG_EXECUTION_DATES_BY_STATE_RESPONSE ="\n" +
            "{\n" +
            "  \"arguments\": {\n" +
            "    \"api\": \"dag_execution_dates_for_state\", \n" +
            "    \"state\": \"running\"\n" +
            "  }, \n" +
            "  \"call_time\": \"2017-09-13T12:20:02.881Z\", \n" +
            "  \"http_response_code\": 200, \n" +
            "  \"output\": [\n" +
            "    {\n" +
            "      \"dag_id\": \"DAG2\", \n" +
            "      \"execution_dates\": [\n" +
            "        \"2018-07-24T09:00:00.000Z\" \n" +
            "      ]\n" +
            "    },\n" +
            "\t    {\n" +
            "      \"dag_id\": \"DAG1\", \n" +
            "      \"execution_dates\": [\n" +
            "        \"2017-07-24T11:00:00.000Z\", \n" +
            "        \"2017-07-24T12:00:00.000Z\", \n" +
            "        \"2017-07-24T13:00:00.000Z\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ], \n" +
            "  \"post_arguments\": {}, \n" +
            "  \"response_time\": \"2017-09-14T10:27:34.075Z\", \n" +
            "  \"status\": \"OK\"\n" +
            "}\n";
    private final String DAG_EXECUTION_DATES_BY_STATE_DAG_ID1_URL ="http://localhost:8000/admin/rest_api/api?api=dag_execution_dates_for_state&state=running&dag_id=DAG1";
    private final String DAG_EXECUTION_DATES_BY_STATE_DAG_ID1_RESPONSE ="\n" +
            "{\n" +
            "  \"arguments\": {\n" +
            "    \"api\": \"dag_execution_dates_for_state\", \n" +
            "    \"state\": \"running\"\n" +
            "  }, \n" +
            "  \"call_time\": \"2017-09-13T12:20:02.881Z\", \n" +
            "  \"http_response_code\": 200, \n" +
            "  \"output\": [\n" +
            "\t    {\n" +
            "      \"dag_id\": \"DAG1\", \n" +
            "      \"execution_dates\": [\n" +
            "        \"2017-07-24T11:00:00.000Z\", \n" +
            "        \"2017-07-24T12:00:00.000Z\", \n" +
            "        \"2017-07-24T13:00:00.000Z\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ], \n" +
            "  \"post_arguments\": {}, \n" +
            "  \"response_time\": \"2017-09-14T10:27:34.075Z\", \n" +
            "  \"status\": \"OK\"\n" +
            "}\n";

    @Autowired
    private AirflowApiClient airflowApiClient;

    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void shouldReturnDagsByState() {
        mockServer.expect(requestTo(DAG_EXECUTION_DATES_BY_STATE_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(DAG_EXECUTION_DATES_BY_STATE_RESPONSE, MediaType.APPLICATION_JSON_UTF8));
        Map<String, List<Instant>> dagExecutionDatesByState = airflowApiClient.getDagExecutionDatesByState(DagState.RUNNING);
        Map<String, List<Instant>> expectedMap = new HashMap<>();
        expectedMap.put("DAG1",
                Lists.newArrayList(
                        Instant.parse("2017-07-24T11:00:00.000Z"),
                        Instant.parse("2017-07-24T12:00:00.000Z"),
                        Instant.parse("2017-07-24T13:00:00.000Z")));
        expectedMap.put("DAG2",
                Lists.newArrayList(
                        Instant.parse("2018-07-24T09:00:00.000Z")));
        Assert.assertTrue(expectedMap.equals(dagExecutionDatesByState));
    }

    @Test
    public void shouldReturnDagsByStateAndDagId() {
        mockServer.expect(requestTo(DAG_EXECUTION_DATES_BY_STATE_DAG_ID1_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(DAG_EXECUTION_DATES_BY_STATE_DAG_ID1_RESPONSE, MediaType.APPLICATION_JSON_UTF8));
        Map<String, List<Instant>> dagExecutionDatesByState = airflowApiClient.getDagExecutionDatesByState("DAG1",DagState.RUNNING);
        Map<String, List<Instant>> expectedMap = new HashMap<>();
        expectedMap.put("DAG1",
                Lists.newArrayList(
                        Instant.parse("2017-07-24T11:00:00.000Z"),
                        Instant.parse("2017-07-24T12:00:00.000Z"),
                        Instant.parse("2017-07-24T13:00:00.000Z")));
        Assert.assertTrue(expectedMap.equals(dagExecutionDatesByState));
    }




}