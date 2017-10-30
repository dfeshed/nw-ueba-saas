package presidio.manager.api.service;

import com.google.common.collect.Lists;
import fortscale.common.SDK.PipelineState;
import fortscale.common.SDK.PipelineStateDataProcessingCursor;
import fortscale.utils.airflow.message.DagState;
import fortscale.utils.airflow.service.AirflowApiClient;
import fortscale.utils.airflow.service.DagExecutionStatus;
import fortscale.utils.time.TimeRange;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ManagerServiceImplTest {
    @Test
    public void shouldReturnStoppedStatus() {
        Duration buildingBaselineDuration = Duration.ofDays(30);
        String managerDagIdPrefix = "full_flow";
        AirflowApiClient airflowApiClient = Mockito.mock(AirflowApiClient.class);
        DagState dagState = DagState.RUNNING;
        Mockito.when(airflowApiClient.getDagExecutionDatesByStateAndDagIdPrefix(dagState, managerDagIdPrefix)).thenReturn(new HashMap<>());
        ManagerServiceImpl managerService = new ManagerServiceImpl(managerDagIdPrefix, airflowApiClient, buildingBaselineDuration);
        PipelineState.StatusEnum actualStatus = managerService.getStatus();
        PipelineState.StatusEnum expectedStatus = PipelineState.StatusEnum.STOPPED;
        Assert.assertEquals(expectedStatus, actualStatus);
        PipelineState expectedPipelineState = new PipelineState();
        expectedPipelineState.setStatus(expectedStatus);
        PipelineState actualPipelineState = managerService.getPipelineState();
        Assert.assertEquals(expectedPipelineState, actualPipelineState);
    }

    @Test
    public void shouldReturnBuildingBaselineStatus() {
        Duration buildingBaselineDuration = Duration.ofDays(30);
        String managerDagIdPrefix = "full_flow";
        AirflowApiClient airflowApiClient = Mockito.mock(AirflowApiClient.class);
        DagState dagState = DagState.RUNNING;
        DagExecutionStatus dag1Status = new DagExecutionStatus("full_flow1", Instant.parse("2017-07-20T07:00:00.000Z"),
                Lists.newArrayList(
                        new TimeRange(Instant.parse("2017-07-24T19:00:00.000Z"), Instant.parse("2017-07-24T20:00:00.000Z")),
                        new TimeRange(Instant.parse("2017-07-24T20:00:00.000Z"), Instant.parse("2017-07-24T21:00:00.000Z")),
                        new TimeRange(Instant.parse("2017-07-24T21:00:00.000Z"), Instant.parse("2017-07-24T22:00:00.000Z")))
                , dagState);
        DagExecutionStatus dag2Status = new DagExecutionStatus("full_flow2", Instant.parse("2017-07-24T07:00:00.000Z"),
                Lists.newArrayList(
                        new TimeRange(Instant.parse("2017-07-24T19:00:00.000Z"), Instant.parse("2017-07-24T20:00:00.000Z")))
                , dagState);
        Map<String, DagExecutionStatus> mockedResult = new HashMap<>();
        mockedResult.put("full_flow1", dag1Status);
        mockedResult.put("full_flow2", dag2Status);
        Mockito.when(airflowApiClient.getDagExecutionDatesByStateAndDagIdPrefix(dagState, managerDagIdPrefix)).thenReturn(mockedResult);
        ManagerServiceImpl managerService = new ManagerServiceImpl(managerDagIdPrefix, airflowApiClient, buildingBaselineDuration);
        PipelineState.StatusEnum actualStatus = managerService.getStatus();
        PipelineState.StatusEnum expectedStatus = PipelineState.StatusEnum.BUILDING_BASELINE;
        Assert.assertEquals(expectedStatus, actualStatus);
        PipelineState expectedPipelineState = new PipelineState();
        expectedPipelineState.setStatus(expectedStatus);
        PipelineStateDataProcessingCursor expectedCursor1 =
                new PipelineStateDataProcessingCursor(new TimeRange(Instant.parse("2017-07-24T19:00:00.000Z"), Instant.parse("2017-07-24T20:00:00.000Z")));
        PipelineStateDataProcessingCursor expectedCursor2 =
                new PipelineStateDataProcessingCursor(new TimeRange(Instant.parse("2017-07-24T20:00:00.000Z"), Instant.parse("2017-07-24T21:00:00.000Z")));
        PipelineStateDataProcessingCursor expectedCursor3 =
                new PipelineStateDataProcessingCursor(new TimeRange(Instant.parse("2017-07-24T21:00:00.000Z"), Instant.parse("2017-07-24T22:00:00.000Z")));
        expectedPipelineState.setDataProcessingCursor(Lists.newArrayList(expectedCursor1, expectedCursor2, expectedCursor3));
        PipelineState actualPipelineState = managerService.getPipelineState();
        Assert.assertEquals(expectedPipelineState, actualPipelineState);
    }

    @Test
    public void shouldReturnRunningStatus() {
        Duration buildingBaselineDuration = Duration.ofDays(1);
        String managerDagIdPrefix = "full_flow";
        AirflowApiClient airflowApiClient = Mockito.mock(AirflowApiClient.class);
        DagState dagState = DagState.RUNNING;
        DagExecutionStatus dag1Status = new DagExecutionStatus("full_flow1", Instant.parse("2017-07-20T07:00:00.000Z"),
                Lists.newArrayList(
                        new TimeRange(Instant.parse("2017-07-24T19:00:00.000Z"), Instant.parse("2017-07-24T20:00:00.000Z")),
                        new TimeRange(Instant.parse("2017-07-24T20:00:00.000Z"), Instant.parse("2017-07-24T21:00:00.000Z")),
                        new TimeRange(Instant.parse("2017-07-24T21:00:00.000Z"), Instant.parse("2017-07-24T22:00:00.000Z")))
                , dagState);
        DagExecutionStatus dag2Status = new DagExecutionStatus("full_flow2", Instant.parse("2017-07-24T07:00:00.000Z"),
                Lists.newArrayList(
                        new TimeRange(Instant.parse("2017-07-24T19:00:00.000Z"), Instant.parse("2017-07-24T20:00:00.000Z")))
                , dagState);
        Map<String, DagExecutionStatus> mockedResult = new HashMap<>();
        mockedResult.put("full_flow1", dag1Status);
        mockedResult.put("full_flow2", dag2Status);
        Mockito.when(airflowApiClient.getDagExecutionDatesByStateAndDagIdPrefix(dagState, managerDagIdPrefix)).thenReturn(mockedResult);
        ManagerServiceImpl managerService = new ManagerServiceImpl(managerDagIdPrefix, airflowApiClient, buildingBaselineDuration);
        PipelineState.StatusEnum actualStatus = managerService.getStatus();
        PipelineState.StatusEnum expectedStatus = PipelineState.StatusEnum.RUNNING;
        Assert.assertEquals(expectedStatus, actualStatus);
        PipelineState expectedPipelineState = new PipelineState();
        expectedPipelineState.setStatus(expectedStatus);
        PipelineStateDataProcessingCursor expectedCursor1 =
                new PipelineStateDataProcessingCursor(new TimeRange(Instant.parse("2017-07-24T19:00:00.000Z"), Instant.parse("2017-07-24T20:00:00.000Z")));
        PipelineStateDataProcessingCursor expectedCursor2 =
                new PipelineStateDataProcessingCursor(new TimeRange(Instant.parse("2017-07-24T20:00:00.000Z"), Instant.parse("2017-07-24T21:00:00.000Z")));
        PipelineStateDataProcessingCursor expectedCursor3 =
                new PipelineStateDataProcessingCursor(new TimeRange(Instant.parse("2017-07-24T21:00:00.000Z"), Instant.parse("2017-07-24T22:00:00.000Z")));
        expectedPipelineState.setDataProcessingCursor(Lists.newArrayList(expectedCursor1, expectedCursor2, expectedCursor3));
        PipelineState actualPipelineState = managerService.getPipelineState();
        Assert.assertEquals(expectedPipelineState, actualPipelineState);
    }

    @Test
    public void shouldHandleEmptyAirflowStatus() {
        Duration buildingBaselineDuration = Duration.ofDays(1);
        String managerDagIdPrefix = "full_flow";
        AirflowApiClient airflowApiClient = Mockito.mock(AirflowApiClient.class);
        DagState dagState = DagState.RUNNING;
        Map<String, DagExecutionStatus> mockedResult = new HashMap<>();
        Mockito.when(airflowApiClient.getDagExecutionDatesByStateAndDagIdPrefix(dagState, managerDagIdPrefix)).thenReturn(mockedResult);
        ManagerServiceImpl managerService = new ManagerServiceImpl(managerDagIdPrefix, airflowApiClient, buildingBaselineDuration);
        PipelineState actualPipelineState = managerService.getPipelineState();
        Assert.assertEquals(PipelineState.StatusEnum.STOPPED, actualPipelineState.getStatus());
        Assert.assertTrue(actualPipelineState.getDataProcessingCursor().isEmpty());
    }
}
