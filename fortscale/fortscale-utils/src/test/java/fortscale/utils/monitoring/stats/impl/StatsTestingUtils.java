package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsDoubleFlexMetric;
import fortscale.utils.monitoring.stats.StatsLongFlexMetric;
import fortscale.utils.monitoring.stats.StatsMetricsTag;
import fortscale.utils.monitoring.stats.StatsStringFlexMetric;
import fortscale.utils.monitoring.stats.engine.*;
import fortscale.utils.monitoring.stats.impl.engine.testing.StatsTestingEngine;
import fortscale.utils.process.hostnameService.HostnameService;

import java.util.List;
import java.util.Optional;

/**
 *
 * A class that hosts various testing utils.
 *
 * It is abstract class to make sure all its functions are static.
 *
 * Created by gaashh on 4/27/16.
 */
abstract public class StatsTestingUtils {

    final static String TEST_PROCESS_NAME       = "test-process";
    final static String TEST_PROCESS_GROUP_NAME = "test-process-group";
    final static long   TEST_PROCESS_PID        = 6789;
    final static String TEST_HOST_NAME          = "test-host";

    // An helper function that creates a stats service with testing engine
    static public StatsServiceImpl createStatsServiceImplWithTestingEngine(){

        // Create a mock hostname service
        HostnameService hostnameService = new MockHostnameService(TEST_HOST_NAME);

        // Create the stats service
        StatsServiceImpl statsService = createStatsServiceImplWithTestingEngineExtended(
                TEST_PROCESS_NAME,TEST_PROCESS_GROUP_NAME,TEST_PROCESS_PID, hostnameService);

        return statsService;
    }

    // An helper function that creates a stats service with testing engine with extended parameters
    static public StatsServiceImpl createStatsServiceImplWithTestingEngineExtended(
            String processName, String processGroupName, long  processPID, HostnameService hostnameService){

        // Create the testing engine
        StatsEngine statsEngine  = new StatsTestingEngine();

        // Create the stats service and hook the engine to it
        StatsServiceImpl statsService = new StatsServiceImpl(statsEngine,
                                                             processName, processGroupName, processPID, hostnameService,
                                                             0, 0, 0, 0, 0, true, true);


        return statsService;
    }


    // An helper function to get tag attribute from engine group Data.  Null if not found
    static public String engineGroupDataGetTagByName(StatsEngineMetricsGroupData groupData, String tagName) {

        List<StatsMetricsTag> tagsList = groupData.getMetricsTags();

        Optional<StatsMetricsTag> result = tagsList.stream().filter(tag -> tag.getName().equals(tagName)).findFirst();

        if (!result.isPresent()) {
            return null;
        }

        return result.get().getValue();

    }

    // An helper function to get long field value from engine group Data.  Null if not found
    static public  Long engineGroupDataGetLongValueByName(StatsEngineMetricsGroupData groupData, String valueName) {

        List<StatsEngineLongMetricData> valuesList = groupData.getLongMetricsDataList();

        Optional<StatsEngineLongMetricData> result =
                valuesList.stream().filter(tag -> tag.getName().equals(valueName)).findFirst();

        if (!result.isPresent()) {
            return null;
        }

        return result.get().getValue();

    }

    // An helper function to get double field value from engine group Data.  Null if not found
    static public  Double engineGroupDataGetDoubleValueByName(StatsEngineMetricsGroupData groupData, String valueName) {

        List<StatsEngineDoubleMetricData> valuesList = groupData.getDoubleMetricsDataList();

        Optional<StatsEngineDoubleMetricData> result =
                valuesList.stream().filter(tag -> tag.getName().equals(valueName)).findFirst();

        if (!result.isPresent()) {
            return null;
        }

        return result.get().getValue();

    }

    // An helper function to get string field value from engine group Data.  Null if not found
    static public String engineGroupDataGetStringValueByName(StatsEngineMetricsGroupData groupData, String valueName) {

        List<StatsEngineStringMetricData> valuesList = groupData.getStringMetricsDataList();

        Optional<StatsEngineStringMetricData> result =
                valuesList.stream().filter(tag -> tag.getName().equals(valueName)).findFirst();

        if (!result.isPresent()) {
            return null;
        }

        return result.get().getValue();

    }

    // A mock host name service that just returns the host name it was set to
    static class MockHostnameService implements HostnameService {

        protected String hostname;

        public MockHostnameService(String hostname) {
            this.hostname = hostname;
        }

        @Override
        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

    }

}


// Long flex metric helper class
class FlexLong implements StatsLongFlexMetric {
    Long value;

    FlexLong(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }
}

// Double flex metric helper class
class FlexDouble implements StatsDoubleFlexMetric {
    Double value;

    FlexDouble(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }
}

// String flex metric helper class
class FlexString implements StatsStringFlexMetric {
    String value;

    FlexString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
