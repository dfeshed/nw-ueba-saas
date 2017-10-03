package fortscale.utils.airflow.service;

import fortscale.utils.airflow.message.AirflowDagExecutionDatesApiResponse;
import fortscale.utils.airflow.message.DagState;
import fortscale.utils.logging.Logger;
import org.json.JSONObject;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * @see AirflowApiClient
 * Created by barak_schuster on 9/13/17.
 */
public class AirflowApiClientImpl implements AirflowApiClient {
    private static final Logger logger = Logger.getLogger(AirflowApiClientImpl.class);

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
     * @param restTemplate          some spring magic for rest calls, should contain converters to handle standard http/json communication
     * @param airflowRestApiBaseUrl i.e. "http://HOST:PORT/admin/rest_api/api";
     */
    public AirflowApiClientImpl(RestTemplate restTemplate, String airflowRestApiBaseUrl) {
        this.airflowRestApiBaseUrl = airflowRestApiBaseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public void pauseDAG(String dagId) {
        Assert.hasText(dagId,"dagId must be not empty");

        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put(API_URL_VARIABLE, PAUSE_API_NAME);
        urlVariables.put(DAG_ID_URL_VARIABLE, dagId);

        String url = String.format("%s?%s={api}&%s={dag_id}", airflowRestApiBaseUrl, API_URL_VARIABLE, DAG_ID_URL_VARIABLE);
        restTemplate.getForObject(url, AirflowDagExecutionDatesApiResponse.class, urlVariables);
    }

    @Override
    public void unpauseDAG(String dagId) {
        Assert.hasText(dagId,"dagId must be not empty");

        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put(API_URL_VARIABLE, UNPAUSE_API_NAME);
        urlVariables.put(DAG_ID_URL_VARIABLE, dagId);

        String url = String.format("%s?%s={api}&%s={dag_id}", airflowRestApiBaseUrl, API_URL_VARIABLE, DAG_ID_URL_VARIABLE);
        restTemplate.getForObject(url, AirflowDagExecutionDatesApiResponse.class, urlVariables);
    }

    @Override
    public void triggerDag(String dagId, String runId, JSONObject conf) {
        Assert.hasText(dagId,"dagId must be not empty");

        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put(API_URL_VARIABLE, TRIGGER_DAG_API_NAME);
        urlVariables.put(DAG_ID_URL_VARIABLE, dagId);
        StringBuilder urlBuilder = new StringBuilder().append(airflowRestApiBaseUrl).append("?")
                .append(API_URL_VARIABLE).append("={api}&")
                .append(DAG_ID_URL_VARIABLE).append("={dag_id}");
        if (runId != null) {
            urlBuilder.append("&").append(RUN_ID_VARIABLE).append("={run_id}");
            urlVariables.put(RUN_ID_VARIABLE, runId);
        }
        if (conf != null) {
            urlBuilder.append("&").append(CONF_VARIABLE).append("={conf}");
            urlVariables.put(CONF_VARIABLE, conf.toString());
        }
        restTemplate.getForObject(urlBuilder.toString(), AirflowDagExecutionDatesApiResponse.class, urlVariables);
    }

    @Override
    public Map<String, DagExecutionStatus> getDagExecutionDatesByState(String dagId, DagState state) {
        Assert.notNull(state,"state must be not empty");
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put(API_URL_VARIABLE, DAG_EXECUTION_DATES_FOR_STATE_API_NAME);
        urlVariables.put(STATE_URL_VARIABLE, state.toString());

        StringBuilder urlBuilder = new StringBuilder().append(airflowRestApiBaseUrl).append("?")
                .append(API_URL_VARIABLE).append("={api}&")
                .append(STATE_URL_VARIABLE).append("={state}");
        if (dagId != null) {
            urlBuilder.append("&").append(DAG_ID_URL_VARIABLE).append("={dag_id}");
            urlVariables.put(DAG_ID_URL_VARIABLE, dagId);
        }
        String url = urlBuilder.toString();
        AirflowDagExecutionDatesApiResponse response = restTemplate.getForObject(url, AirflowDagExecutionDatesApiResponse.class, urlVariables);

        Map<String, DagExecutionStatus> result = new HashMap<>();
        if(response!=null && response.getOutput()!=null) {
            result =
                    response.getOutput().stream()
                            .map(x -> new DagExecutionStatus(x.getDagId(), x.getStartDate(), x.getExecutionDates(), state))
                            .collect(Collectors.toMap(DagExecutionStatus::getDagId, Function.identity()));
        }
        else
        {
            logger.info("got 0 dags for dagId={}, state={}",dagId,state);
        }
        return result;
    }
}
