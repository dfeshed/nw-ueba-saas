package presidio.integration.performance.test;

import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import com.rsa.netwitness.presidio.automation.test_managers.AdapterTestManager;
import com.rsa.netwitness.presidio.automation.utils.adapter.ReferenceIdGeneratorFactory;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import fortscale.common.general.Schema;
import org.apache.commons.lang.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;
import presidio.integration.performance.generators.converter.EventToMetadataConverterFactory;
import presidio.integration.performance.scenario.ProcessPerformanceStabilityScenario;
import presidio.integration.performance.scenario.RegistryPerformanceStabilityScenario;
import presidio.nw.flume.domain.test.NetwitnessStoredData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest(classes = {AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class PerformanceStabilityMongoGenTest extends AbstractTestNGSpringContextTests {
    private static final int EVENTS_GENERATION_CHUNK = 10000;
    private static final int NUM_OF_NORMAL_USERS = 94500;
    private static final int NUM_OF_ADMIN_USERS = 5000;
    private static final int NUM_OF_SERVICE_ACCOUNT_USERS = 500;

    private StopWatch stopWatch = new StopWatch();

    Instant startInstant;
    Instant endInstant;

    @Autowired
    private AdapterTestManager adapterTestManager;
    @Autowired
    private NetwitnessEventStore netwitnessEventStore;

    @Parameters({"start_time", "end_time", "probability_multiplier", "users_multiplier"})
    @Test
    //public void performance(@Optional("2018-04-03T00:00:00.00Z") String startTimeStr, @Optional("2018-04-10T00:00:00.00Z") String endTimeStr) throws GeneratorException {
    public void performance(@Optional("2018-04-03T00:00:00.00Z") String startTimeStr, @Optional("2018-04-03T01:00:00.00Z") String endTimeStr, @Optional("0.005") double probabilityMultiplier, @Optional("0.005") double usersMultiplier) throws GeneratorException {
        System.out.println("=================== TEST PARAMETERS =============== ");
        System.out.println("start_time: " + startTimeStr);
        System.out.println("end_time: " + endTimeStr);
        System.out.println("probability_multiplier: " + probabilityMultiplier);
        System.out.println("users_multiplier: " + usersMultiplier);
        System.out.println("=================================================== ");

        startInstant = Instant.parse(startTimeStr);
        endInstant = Instant.parse(endTimeStr);

        // generating test data into mongo DB, set test mode and start to process the data
        adapterTestManager.setTestMode4EndPointOnly();
        adapterTestManager.runUebaServerConfigScript(startInstant);

        stopWatch.start();

        generateDaysOfEvents(probabilityMultiplier, usersMultiplier);

        stopWatch.split();
        System.out.println(stopWatch.toSplitString());

        adapterTestManager.setProdMode4EndPointOnly();
    }

    private void generateDaysOfEvents(double probabilityMultiplier, double usersMultiplier) throws GeneratorException {
        /** Generate and send events **/
        int iterations = 0;

        Map<String, String> config = new HashMap<>();
        config.put(ReferenceIdGeneratorFactory.REFERENCE_ID_GENERATOR_TYPE_CONFIG_KEY, "cyclic");

        int numOfNormalUsers = (int) (NUM_OF_NORMAL_USERS * usersMultiplier);
        int numOfAdminUsers = (int) (NUM_OF_ADMIN_USERS * usersMultiplier);
        int numOfserviceAccountUsers = (int) (NUM_OF_SERVICE_ACCOUNT_USERS * usersMultiplier);

        RegistryPerformanceStabilityScenario registryScenario =
                new RegistryPerformanceStabilityScenario(
                        startInstant, endInstant,
                        numOfNormalUsers, numOfAdminUsers, numOfserviceAccountUsers,
                        probabilityMultiplier);
        ProcessPerformanceStabilityScenario processScenario =
                new ProcessPerformanceStabilityScenario(
                        startInstant, endInstant,
                        numOfNormalUsers, numOfAdminUsers, numOfserviceAccountUsers,
                        probabilityMultiplier);



        List<Event> registryEvents=null;
        List<Event> processEvents=null;
        long totalEventsInserted = 0;
        boolean registryHasNext = true;
        boolean processHasNext = true;
        do {

            //insert events
            if (registryHasNext) {
                registryEvents = registryScenario.generateEvents(EVENTS_GENERATION_CHUNK);
                List<Map<String, Object>> netwitnessRegEvents = new EventToMetadataConverterFactory().getConverter(Schema.REGISTRY).convert(config, registryEvents);
                netwitnessEventStore.store(createNetwitnessStoreDataList(netwitnessRegEvents), Schema.REGISTRY);
                totalEventsInserted += netwitnessRegEvents.size();
                registryHasNext = (registryEvents.size() == EVENTS_GENERATION_CHUNK);
            }

            if (processHasNext) {
                processEvents = processScenario.generateEvents(EVENTS_GENERATION_CHUNK);
                List<Map<String, Object>> netwitnessEvents = new EventToMetadataConverterFactory().getConverter(Schema.PROCESS).convert(config, processEvents);
                netwitnessEventStore.store(createNetwitnessStoreDataList(netwitnessEvents), Schema.PROCESS);
                totalEventsInserted += processEvents.size();
                processHasNext = (processEvents.size() == EVENTS_GENERATION_CHUNK);
            }

            System.out.println(String.format (" %d ", totalEventsInserted));
            System.out.flush();
        } while (registryHasNext || processHasNext);
    }

    private List<NetwitnessStoredData> createNetwitnessStoreDataList(List<Map<String, Object>> netwitnessEvents) {
        List<NetwitnessStoredData> convertedNetwitnessEvents = new ArrayList<>();
        netwitnessEvents.forEach(netwitnessEvent -> {
            convertedNetwitnessEvents.add(new NetwitnessStoredData(netwitnessEvent));
        });

        return convertedNetwitnessEvents;
    }
}
