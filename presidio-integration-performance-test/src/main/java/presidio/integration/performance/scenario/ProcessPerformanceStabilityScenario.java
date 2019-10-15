package presidio.integration.performance.scenario;

import presidio.integration.performance.generators.process.*;

import java.time.Instant;

public class ProcessPerformanceStabilityScenario extends EndPointPerformanceStabilityScenario {



    public ProcessPerformanceStabilityScenario(Instant startInstant, Instant endInstant,
                                               int numOfNormalUsers, int numOfAdminUsers, int numOfserviceAccountUsers,
                                               double probabilityMultiplier) {
        super(startInstant, endInstant, numOfNormalUsers, numOfAdminUsers, numOfserviceAccountUsers,probabilityMultiplier);
    }

    protected void initBuilders(){
        NonImportantProcessEventGeneratorsBuilder nonImportantProcessEventGeneratorsBuilder =
                new NonImportantProcessEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        machineGenerator,
                        nonImportantProcesses
                );
        eventGeneratorsBuilders.add(nonImportantProcessEventGeneratorsBuilder);
        ReconToolGroupAEventGeneratorsBuilder reconToolGroupAEventGeneratorsBuilder =
                new ReconToolGroupAEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        machineGenerator,
                        nonImportantProcesses
                );
        eventGeneratorsBuilders.add(reconToolGroupAEventGeneratorsBuilder);

        ReconToolGroupBEventGeneratorsBuilder reconToolGroupBEventGeneratorsBuilder =
                new ReconToolGroupBEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        machineGenerator,
                        nonImportantProcesses
                );
        eventGeneratorsBuilders.add(reconToolGroupBEventGeneratorsBuilder);

        ReconToolGroupCEventGeneratorsBuilder reconToolGroupCEventGeneratorsBuilder =
                new ReconToolGroupCEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        machineGenerator,
                        nonImportantProcesses
                );
        eventGeneratorsBuilders.add(reconToolGroupCEventGeneratorsBuilder);

        ReconToolGroupDEventGeneratorsBuilder reconToolGroupDEventGeneratorsBuilder =
                new ReconToolGroupDEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        machineGenerator,
                        nonImportantProcesses
                );
        eventGeneratorsBuilders.add(reconToolGroupDEventGeneratorsBuilder);

        ScriptingEngineExecutedEventGeneratorsBuilder scriptingEngineExecutedEventGeneratorsBuilder =
                new ScriptingEngineExecutedEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        machineGenerator,
                        nonImportantProcesses
                );
        eventGeneratorsBuilders.add(scriptingEngineExecutedEventGeneratorsBuilder);

        ScriptingEngineExecuteAndOpenEventGeneratorsBuilder scriptingEngineExecuteAndOpenEventGeneratorsBuilder =
                new ScriptingEngineExecuteAndOpenEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        machineGenerator,
                        nonImportantProcesses
                );
        eventGeneratorsBuilders.add(scriptingEngineExecuteAndOpenEventGeneratorsBuilder);

        LsassEventGeneratorsBuilder lsassEventGeneratorsBuilder =
                new LsassEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        machineGenerator,
                        nonImportantProcesses
                );
        eventGeneratorsBuilders.add(lsassEventGeneratorsBuilder);

        WindowsProcessesEventGeneratorsBuilder windowsProcessesEventGeneratorsBuilder =
                new WindowsProcessesEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        machineGenerator,
                        nonImportantProcesses
                );
        eventGeneratorsBuilders.add(windowsProcessesEventGeneratorsBuilder);
        eventGeneratorsBuilders.forEach(processEventGeneratorsBuilder -> processEventGeneratorsBuilder.setProbabilityMultiplier(probabilityMultiplier));
    }

}
