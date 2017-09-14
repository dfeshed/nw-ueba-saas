package fortscale.utils.airflow.service;

import fortscale.utils.airflow.message.AirflowDagExecutionDatesApiResponse;
import fortscale.utils.airflow.message.DagState;
import fortscale.utils.airflow.message.DagToExecutionDates;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 9/13/17.
 */
public class AirflowApiClientImpl implements AirflowApiClient {

    private static final String API_URL_VARIABLE = "api";
    private static final String STATE_URL_VARIABLE = "state";
    private static final String DAG_ID_URL_VARIABLE = "dag_id";
    private static final String DAG_EXECUTION_DATES_FOR_STATE_API_NAME = "dag_execution_dates_for_state";
    private static final String PAUSE_API_NAME = "pause";
    private static final String UNPAUSE_API_NAME = "unpause";
    private static final String TRIGGER_DAG_API_NAME = "trigger_dag";
    private static final String RUN_ID_VARIABLE = "run_id";
    private static final String CONF_VARIABLE = "conf";
    private RestTemplate restTemplate;
    private String airflowRestApiBaseUrl;

    /**
     *
     * @param restTemplate some spring magic for rest calls, should contain converters to handle standard http/json communication
     * @param airflowRestApiBaseUrl i.e. "http://HOST:PORT/admin/rest_api/api";
     */
    public AirflowApiClientImpl(RestTemplate restTemplate, String airflowRestApiBaseUrl) {
        this.airflowRestApiBaseUrl = airflowRestApiBaseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public void pauseDAG(String dagId) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put(API_URL_VARIABLE, PAUSE_API_NAME);
        urlVariables.put(DAG_ID_URL_VARIABLE, dagId);

        StringBuilder urlBuilder = new StringBuilder().append(airflowRestApiBaseUrl).append("?")
                .append(API_URL_VARIABLE).append("={api}&")
                .append(DAG_ID_URL_VARIABLE).append("={dag_id}");
        restTemplate.getForObject(urlBuilder.toString(), AirflowDagExecutionDatesApiResponse.class, urlVariables);
    }

    @Override
    public void unpauseDAG(String dagId) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put(API_URL_VARIABLE, UNPAUSE_API_NAME);
        urlVariables.put(DAG_ID_URL_VARIABLE, dagId);

        StringBuilder urlBuilder = new StringBuilder().append(airflowRestApiBaseUrl).append("?")
                .append(API_URL_VARIABLE).append("={api}&")
                .append(DAG_ID_URL_VARIABLE).append("={dag_id}");
        restTemplate.getForObject(urlBuilder.toString(), AirflowDagExecutionDatesApiResponse.class, urlVariables);
    }

    @Override
    public void triggerDag(String dagId, String runId, JSONObject conf) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put(API_URL_VARIABLE, TRIGGER_DAG_API_NAME);
        urlVariables.put(DAG_ID_URL_VARIABLE, dagId);
        StringBuilder urlBuilder = new StringBuilder().append(airflowRestApiBaseUrl).append("?")
                .append(API_URL_VARIABLE).append("={api}&")
                .append(DAG_ID_URL_VARIABLE).append("={dag_id}");
        if(runId!=null)
        {
            urlBuilder.append("&").append(RUN_ID_VARIABLE).append("={run_id}");
            urlVariables.put(RUN_ID_VARIABLE,runId);
        }
        if(conf!=null)
        {
            urlBuilder.append("&").append(CONF_VARIABLE).append("={conf}");
            urlVariables.put(CONF_VARIABLE,conf.toString());
        }
        restTemplate.getForObject(urlBuilder.toString(), AirflowDagExecutionDatesApiResponse.class, urlVariables);
    }

    @Override
    public Map<String, List<Instant>> getDagExecutionDatesByState(String dagId, DagState state) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put(API_URL_VARIABLE, DAG_EXECUTION_DATES_FOR_STATE_API_NAME);
        urlVariables.put(STATE_URL_VARIABLE, state.toString());

        StringBuilder urlBuilder = new StringBuilder().append(airflowRestApiBaseUrl).append("?")
                .append(API_URL_VARIABLE).append("={api}&")
                .append(STATE_URL_VARIABLE).append("={state}");
        if (dagId!=null) {
            urlBuilder.append("&").append(DAG_ID_URL_VARIABLE).append("={dag_id}");
            urlVariables.put(DAG_ID_URL_VARIABLE, dagId);
        }
        AirflowDagExecutionDatesApiResponse response = restTemplate.getForObject(urlBuilder.toString(), AirflowDagExecutionDatesApiResponse.class, urlVariables);

        return response.getOutput().stream().collect(Collectors.toMap(DagToExecutionDates::getDagId, DagToExecutionDates::getExecutionDates));
    }
}
