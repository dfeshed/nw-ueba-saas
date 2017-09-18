package presidio.manager.api.service;

import fortscale.common.SDK.PipelineState;
import fortscale.common.SDK.PipelineStateDataProcessingCursor;
import fortscale.utils.airflow.message.DagState;
import fortscale.utils.airflow.service.AirflowApiClient;
import fortscale.utils.time.TimeRange;
import org.joda.time.DateTime;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Created by barak_schuster on 9/18/17.
 */
public class ManagerServiceImpl implements ManagerService {
    private List<String> managerDagIds;
    private AirflowApiClient airflowApiClient;
    private Instant dataPipelineStartTime;
    private Duration buildingBaselineDuration;

    public ManagerServiceImpl(List<String> managerDagIds, AirflowApiClient airflowApiClient, Instant dataPipelineStartTime, Duration buildingBaselineDuration) {
        this.managerDagIds = managerDagIds;
        this.airflowApiClient = airflowApiClient;
        this.dataPipelineStartTime = dataPipelineStartTime;
        this.buildingBaselineDuration = buildingBaselineDuration;
    }

    /**
     *
     * ideally the manager should ask each component of it's cursor and give an accumulated response.
     * for simplicity reasons we currently ask only the full flow dag.
     */
    @Override
    public List<PipelineStateDataProcessingCursor> getCurrentlyRunningCursor() {
        List<PipelineStateDataProcessingCursor> result = new ArrayList<>();
        for (String dagId: managerDagIds) {
            // todo: instead of asking only the airflow client we should ask all the components
            Map<String, List<TimeRange>> dagExecutionDatesByState = airflowApiClient.getDagExecutionDatesByState(dagId, DagState.RUNNING);
            List<TimeRange> timeRanges = dagExecutionDatesByState.get(dagId);

            if(timeRanges !=null && !timeRanges.isEmpty()) {
                Instant startDate = timeRanges.stream().min(TimeRange::compareTo).get().getStart();
                Instant endDate = timeRanges.stream().max(TimeRange::compareTo).get().getEnd();
                PipelineStateDataProcessingCursor cursor = new PipelineStateDataProcessingCursor();
                cursor.setFrom(new DateTime(startDate.toEpochMilli()));
                cursor.setFrom(new DateTime(endDate.toEpochMilli()));
                result.add(cursor);
            }
        }
        return result;
    }

    /**
     *
     * ideally the manager should ask each component of it's cursor and give an accumulated response.
     * for simplicity reasons we currently ask only the full flow dag.
     * since control api is not implemented yet, we only return status of stopping&cleaning and running without asking the components, relying on the main dag.
     * @param currentlyRunningCursor
     */
    @Override
    public PipelineState.StatusEnum getStatus(List<PipelineStateDataProcessingCursor> currentlyRunningCursor) {
        if(currentlyRunningCursor==null)
        {
            return PipelineState.StatusEnum.STOPPED;
        }
        if(currentlyRunningCursor.isEmpty())
        {
            return PipelineState.StatusEnum.STOPPED;
        }
        // todo: add another if clause in order to decide cleaning status
        Instant firstRunning =
                Instant.ofEpochMilli(
                        currentlyRunningCursor.stream().min(Comparator.comparing(PipelineStateDataProcessingCursor::getFrom))
                                .get().getFrom().getMillis());
        // todo: this logic should be given by ade manager sdk
        if(Duration.between(dataPipelineStartTime,firstRunning).compareTo(buildingBaselineDuration)<0)
        {
            return PipelineState.StatusEnum.BUILDING_BASELINE;
        }
        return PipelineState.StatusEnum.RUNNING;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
