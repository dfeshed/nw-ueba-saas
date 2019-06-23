package presidio.data.generators.event.process;

import presidio.data.generators.event.EndPointPerformanceStabilityScenario;
import presidio.data.generators.event.EndpointEventGeneratorsBuilder;

import java.time.Instant;

public class ProcessPerformanceStabilityScenario extends EndPointPerformanceStabilityScenario {



    public ProcessPerformanceStabilityScenario(Instant startInstant, Instant endInstant, double probabilityMultiplier, double usersMultiplier) {
        super(startInstant, endInstant, probabilityMultiplier,usersMultiplier);
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
