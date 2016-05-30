package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsDoubleFlexMetric;
import fortscale.utils.monitoring.stats.StatsLongFlexMetric;
import fortscale.utils.monitoring.stats.StatsMetricsTag;
import fortscale.utils.monitoring.stats.StatsStringFlexMetric;
import fortscale.utils.monitoring.stats.engine.*;
import fortscale.utils.monitoring.stats.impl.engine.testing.StatsTestingEngine;

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

    // An helper function that creates a stats service with testing engine
    static public StatsServiceImpl createStatsServiceImplWithTestingEngine(){

        // Create the testing engine
        StatsEngine statsEngine  = new StatsTestingEngine();

        // Create the stats service and hook the engine to it
        StatsServiceImpl statsService = new StatsServiceImpl(statsEngine);

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
