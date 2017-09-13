package fortscale.utils.airflow;

import org.json.JSONObject;
import org.springframework.data.util.Pair;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 9/13/17.
 */
public class AirflowApiClientImpl implements AirflowApiClient{

    public static final String API_URL_VARIABLE = "api";
    public static final String STATE_URL_VARIABLE = "state";
    public static final String DAG_ID_URL_VARIABLE = "dag_id";
    private RestTemplate restTemplate;
    private String airflowRestApiBaseUrl;

    public AirflowApiClientImpl() {
        this.restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
    }

    @Override
    public void pauseDAG(String dagId) {

    }

    @Override
    public void unpauseDAG(String dagId) {

    }

    @Override
    public void triggerDag(String dagId, Optional<String> runId, Optional<JSONObject> conf) {

    }

    @Override
    public Map<String, List<Instant>> getDagExecutionDatesByState(Optional<String> dagId, DagState state) {
        airflowRestApiBaseUrl = "http://dev-brks2:8000/admin/rest_api/api";

        Map<String,String> urlvariables = new HashMap<>();
        urlvariables.put(API_URL_VARIABLE, "dag_execution_dates_for_state" );
        urlvariables.put(STATE_URL_VARIABLE,state.toString());

        StringBuilder urlBuilder = new StringBuilder().append(airflowRestApiBaseUrl).append("?")
                .append(API_URL_VARIABLE).append("={api}&")
                .append(STATE_URL_VARIABLE).append("={state}&");
        if(dagId.isPresent())
        {
            urlBuilder.append(DAG_ID_URL_VARIABLE).append("={dag_id}");
            urlvariables.put(DAG_ID_URL_VARIABLE,"anomaly_detection_engine_dag_example");
        }
        AirflowDagExecutionDatesApiResponse reponse = restTemplate.getForObject(urlBuilder.toString(), AirflowDagExecutionDatesApiResponse.class, urlvariables);

        return reponse.getOutput().stream().collect(Collectors.toMap(DagToExecutionDates::getDagId, DagToExecutionDates::getExecutionDates));
    }
}
