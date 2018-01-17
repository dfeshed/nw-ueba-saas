package presidio.manager.api.service;

import fortscale.common.SDK.PipelineState;
import fortscale.common.SDK.PipelineStateDataProcessingCursor;
import fortscale.utils.airflow.message.DagState;
import fortscale.utils.airflow.service.AirflowApiClient;
import fortscale.utils.airflow.service.DagExecutionStatus;
import fortscale.utils.time.TimeRange;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 9/18/17.
 */
public class ManagerServiceImpl implements ManagerService {
    public static final java.lang.String RESET_PRESIDIO_DAG_ID = "reset_presidio";
    private final String managerDagIdPrefix;
    private final AirflowApiClient airflowApiClient;
    private final Duration buildingBaselineDuration;
    private volatile PipelineState.StatusEnum statusEnum;

    /**
     * C'tor
     *
     * @param managerDagIdPrefix       dagId is construct from prefix and a hash of it's configuration. this is the prefix
     * @param airflowApiClient         client to preform queries on DAGs
     * @param buildingBaselineDuration Duration used to determine weather we are still building baseline or not
     */
    public ManagerServiceImpl(String managerDagIdPrefix, AirflowApiClient airflowApiClient, Duration buildingBaselineDuration) {
        this.managerDagIdPrefix = managerDagIdPrefix;
        this.airflowApiClient = airflowApiClient;
        this.buildingBaselineDuration = buildingBaselineDuration;
        // stopped is a viable response only on system startup - before system is configured
        this.statusEnum = PipelineState.StatusEnum.STOPPED;
    }

    /**
     * ideally the manager should ask each component of it's cursor and give an accumulated response.
     * for simplicity reasons we currently ask only the full flow dag.
     */
    @Override
    public List<PipelineStateDataProcessingCursor> getCurrentlyRunningCursor() {
        Map<String, DagExecutionStatus> dagStatusByState = airflowApiClient.getDagExecutionDatesByStateAndDagIdPrefix(DagState.RUNNING, managerDagIdPrefix);
        Set<PipelineStateDataProcessingCursor> resultSet = new HashSet<>();
        for (String dagId : dagStatusByState.keySet()) {
            // todo: instead of asking only the airflow client we should ask all the components
            List<TimeRange> timeRanges = dagStatusByState.get(dagId).getExecutionDates();

            if (timeRanges != null && !timeRanges.isEmpty()) {
                List<PipelineStateDataProcessingCursor> timeCursors =
                        timeRanges.stream().map(PipelineStateDataProcessingCursor::new).collect(Collectors.toList());

                resultSet.addAll(timeCursors);
            }
        }
        return new ArrayList<>(resultSet);
    }

    /**
     * ideally the manager should ask each component of it's cursor and give an accumulated response.
     * for simplicity reasons we currently ask only the full flow dag.
     * since control api is not implemented yet, we only return status of stopping&cleaning and running without asking the components, relying on the main dag.
     *
     * @return if dagId does not exist, it will appear as stopped
     */
    @Override
    public PipelineState.StatusEnum getStatus() {
        Map<String, DagExecutionStatus> resetDagResponse = airflowApiClient.getDagExecutionDatesByStateAndDagIdPrefix(DagState.RUNNING, RESET_PRESIDIO_DAG_ID);

        // if cleanup dag is running - the state of the system is cleaning
        if(!resetDagResponse.isEmpty())
        {
            statusEnum = PipelineState.StatusEnum.CLEANING;
        }

        else {
            Map<String, DagExecutionStatus> fullFlowStatusResponse = airflowApiClient.getDagExecutionDatesByStateAndDagIdPrefix(DagState.RUNNING, managerDagIdPrefix);

            // stopped is a viable response only on system startup
            if (fullFlowStatusResponse != null && !fullFlowStatusResponse.isEmpty()) {
                TimeRange firstTimeRange = fullFlowStatusResponse.values().stream().map(DagExecutionStatus::getExecutionDates).flatMap(Collection::stream).min(TimeRange::compareTimeRange).get();

                // it's almost like assuming that there is only one dag. not error prune, but simple enough for the moment
                // update: today i regret the previous written comment.
                DagExecutionStatus firstRunningDag = fullFlowStatusResponse.values().stream().min(Comparator.comparing(DagExecutionStatus::getStartInstant)).get();

                // todo: this logic should be given by ade manager sdk
                if (Duration.between(firstRunningDag.getStartInstant(), firstTimeRange.getStart()).compareTo(buildingBaselineDuration) < 0) {
                    statusEnum = PipelineState.StatusEnum.BUILDING_BASELINE;
                } else {
                    statusEnum = PipelineState.StatusEnum.RUNNING;
                }
            }
        }

        return statusEnum;
    }

    @Override
    public void cleanAndRun() {
        airflowApiClient.triggerDag(RESET_PRESIDIO_DAG_ID);
    }
}
