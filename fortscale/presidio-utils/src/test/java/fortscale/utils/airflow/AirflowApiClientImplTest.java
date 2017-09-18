package fortscale.utils.airflow;

import com.google.common.collect.Lists;
import fortscale.utils.airflow.message.DagState;
import fortscale.utils.airflow.service.AirflowApiClient;
import fortscale.utils.airflow.service.AirflowApiClientConfig;
import fortscale.utils.time.TimeRange;
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
@ContextConfiguration(classes = AirflowApiClientConfig.class)
public class AirflowApiClientImplTest {

    private final String DAG_EXECUTION_DATES_BY_STATE_URL ="http://localhost:8000/admin/rest_api/api?api=dag_execution_dates_for_state&state=running";
    private final String DAG_EXECUTION_DATES_BY_STATE_RESPONSE ="\n" +
            "{\n" +
            "  \"arguments\": {\n" +
            "    \"api\": \"dag_execution_dates_for_state\", \n" +
            "    \"state\": \"running\"\n" +
            "  }, \n" +
            "  \"call_time\": \"2017-09-18T10:10:11.962Z\", \n" +
            "  \"http_response_code\": 200, \n" +
            "  \"output\": [\n" +
            "    {\n" +
            "      \"dag_id\": \"DAG1\", \n" +
            "      \"execution_dates\": [\n" +
            "        {\n" +
            "          \"end\": \"2017-07-24T20:00:00.000Z\", \n" +
            "          \"start\": \"2017-07-24T19:00:00.000Z\"\n" +
            "        }, \n" +
            "        {\n" +
            "          \"end\": \"2017-07-24T21:00:00.000Z\", \n" +
            "          \"start\": \"2017-07-24T20:00:00.000Z\"\n" +
            "        }, \n" +
            "        {\n" +
            "          \"end\": \"2017-07-24T22:00:00.000Z\", \n" +
            "          \"start\": \"2017-07-24T21:00:00.000Z\"\n" +
            "        } \n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"dag_id\": \"DAG2\", \n" +
            "      \"execution_dates\": [\n" +
            "        {\n" +
            "          \"end\": \"2017-07-24T20:00:00.000Z\", \n" +
            "          \"start\": \"2017-07-24T19:00:00.000Z\"\n" +
            "        } \n" +
            "      ]\n" +
            "    }\n" +
            "  ], \n" +
            "  \"post_arguments\": {}, \n" +
            "  \"response_time\": \"2017-09-18T10:10:27.952Z\", \n" +
            "  \"status\": \"OK\"\n" +
            "}\n";
    private final String DAG_EXECUTION_DATES_BY_STATE_DAG_ID1_URL ="http://localhost:8000/admin/rest_api/api?api=dag_execution_dates_for_state&state=running&dag_id=DAG1";
    private final String DAG_EXECUTION_DATES_BY_STATE_DAG_ID1_RESPONSE ="\n" +
            "{\n" +
            "  \"arguments\": {\n" +
            "    \"api\": \"dag_execution_dates_for_state\", \n" +
            "    \"state\": \"running\"\n" +
            "  }, \n" +
            "  \"call_time\": \"2017-09-18T10:10:11.962Z\", \n" +
            "  \"http_response_code\": 200, \n" +
            "  \"output\": [\n" +
            "    {\n" +
            "      \"dag_id\": \"DAG1\", \n" +
            "      \"execution_dates\": [\n" +
            "        {\n" +
            "          \"end\": \"2017-07-24T20:00:00.000Z\", \n" +
            "          \"start\": \"2017-07-24T19:00:00.000Z\"\n" +
            "        }, \n" +
            "        {\n" +
            "          \"end\": \"2017-07-24T21:00:00.000Z\", \n" +
            "          \"start\": \"2017-07-24T20:00:00.000Z\"\n" +
            "        }, \n" +
            "        {\n" +
            "          \"end\": \"2017-07-24T22:00:00.000Z\", \n" +
            "          \"start\": \"2017-07-24T21:00:00.000Z\"\n" +
            "        }, \n" +
            "        {\n" +
            "          \"end\": \"2017-07-24T23:00:00.000Z\", \n" +
            "          \"start\": \"2017-07-24T22:00:00.000Z\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ], \n" +
            "  \"post_arguments\": {}, \n" +
            "  \"response_time\": \"2017-09-18T10:10:27.952Z\", \n" +
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
        Map<String, List<TimeRange>> dagExecutionDatesByState = airflowApiClient.getDagExecutionDatesByState(DagState.RUNNING);
        Map<String, List<TimeRange>> expectedMap = new HashMap<>();
        expectedMap.put("DAG1",
                Lists.newArrayList(
                        new TimeRange(Instant.parse("2017-07-24T19:00:00.000Z"), Instant.parse("2017-07-24T20:00:00.000Z")),
                        new TimeRange(Instant.parse("2017-07-24T20:00:00.000Z"), Instant.parse("2017-07-24T21:00:00.000Z")),
                        new TimeRange(Instant.parse("2017-07-24T21:00:00.000Z"), Instant.parse("2017-07-24T22:00:00.000Z"))));
        expectedMap.put("DAG2",
                Lists.newArrayList(
                        new TimeRange(Instant.parse("2017-07-24T19:00:00.000Z"),Instant.parse("2017-07-24T20:00:00.000Z"))));
        Assert.assertTrue(expectedMap.equals(dagExecutionDatesByState));
    }

    @Test
    public void shouldReturnDagsByStateAndDagId() {
        mockServer.expect(requestTo(DAG_EXECUTION_DATES_BY_STATE_DAG_ID1_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(DAG_EXECUTION_DATES_BY_STATE_DAG_ID1_RESPONSE, MediaType.APPLICATION_JSON_UTF8));
        Map<String, List<TimeRange>> dagExecutionDatesByState = airflowApiClient.getDagExecutionDatesByState("DAG1",DagState.RUNNING);
        Map<String, List<TimeRange>> expectedMap = new HashMap<>();
        expectedMap.put("DAG1",
                Lists.newArrayList(
                        new TimeRange(Instant.parse("2017-07-24T19:00:00.000Z"), Instant.parse("2017-07-24T20:00:00.000Z")),
                        new TimeRange(Instant.parse("2017-07-24T20:00:00.000Z"), Instant.parse("2017-07-24T21:00:00.000Z")),
                        new TimeRange(Instant.parse("2017-07-24T21:00:00.000Z"), Instant.parse("2017-07-24T22:00:00.000Z")),
                        new TimeRange(Instant.parse("2017-07-24T22:00:00.000Z"), Instant.parse("2017-07-24T23:00:00.000Z"))
                ));
        Assert.assertTrue(expectedMap.equals(dagExecutionDatesByState));
    }




}