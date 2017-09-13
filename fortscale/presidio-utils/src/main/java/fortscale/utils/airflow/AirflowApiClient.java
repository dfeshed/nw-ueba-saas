package fortscale.utils.airflow;

import org.json.JSONObject;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * api client for presidio.workflows.plugins.rest_api_plugin
 * Created by barak_schuster on 9/13/17.
 */
public interface AirflowApiClient {

    /**
     * Pauses a DAG
     * @param dagId
     */
    void pauseDAG(String dagId);

    /**
     * Resume a paused DAG
     * @param dagId
     */
    void unpauseDAG(String dagId);

    /**
     * Triggers a Dag to Run
     * @param dagId - The DAG ID of the DAG you want to trigger
     * @param runId - The RUN ID to use for the DAG run
     * @param conf - Some configuration to pass to the DAG you trigger - (URL Encoded JSON)
     */
    void triggerDag(String dagId, Optional<String> runId, Optional<JSONObject> conf);


    /**
     *
     * @param dagId- The DAG ID of the DAG you want to trigger
     *
     * @return map key:dag list: all execution dates in given {@param state}
     */
    Map<String,List<Instant>> getDagExecutionDatesByState(Optional<String>  dagId, DagState state);

}
