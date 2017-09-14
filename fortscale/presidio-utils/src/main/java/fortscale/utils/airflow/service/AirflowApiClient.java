package fortscale.utils.airflow.service;

import fortscale.utils.airflow.message.DagState;
import org.json.JSONObject;

import java.time.Instant;
import java.util.List;
import java.util.Map;


/**
 * api client for presidio.workflows.plugins.rest_api_plugin
 * inspired by {@link <a href ="https://github.com/teamclairvoyant/airflow-rest-api-plugin"/>}
 * Created by barak_schuster on 9/13/17.
 */
public interface AirflowApiClient {

    /**
     * Pauses a DAG
     *
     * @param dagId
     */
    void pauseDAG(String dagId);

    /**
     * Resume a paused DAG
     *
     * @param dagId
     */
    void unpauseDAG(String dagId);

    /**
     * Triggers a Dag to Run
     *
     * @param dagId - The DAG ID of the DAG you want to trigger
     * @param runId - The RUN ID to use for the DAG run
     * @param conf  - Some configuration to pass to the DAG you trigger - (URL Encoded JSON)
     */
    void triggerDag(String dagId, String runId, JSONObject conf);

    /**
     * syntactic sugar
     *
     * @see this#triggerDag(String, String, JSONObject)
     */
    default void triggerDag(String dagId, JSONObject conf) {
        triggerDag(dagId, null, conf);
    }

    /**
     * syntactic sugar
     *
     * @see this#triggerDag(String, String, JSONObject)
     */
    default void triggerDag(String dagId, String runId) {
        triggerDag(dagId, runId, null);
    }


    /**
     * @param dagId - The DAG ID of the DAG you want to trigger
     * @return map key:dag list: all execution dates in given {@param state}
     */
    Map<String, List<Instant>> getDagExecutionDatesByState(String dagId, DagState state);

    /**
     * syntactic sugar
     *
     * @see this#getDagExecutionDatesByState(String, DagState)
     */
    default Map<String, List<Instant>> getDagExecutionDatesByState(DagState state) {
        return getDagExecutionDatesByState(null, state);
    }

}
