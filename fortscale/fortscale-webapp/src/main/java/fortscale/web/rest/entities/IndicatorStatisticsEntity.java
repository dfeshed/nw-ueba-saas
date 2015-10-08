package fortscale.web.rest.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shays on 08/10/2015.
 * This class return list count-timeRange pairs
 */
public class IndicatorStatisticsEntity {

    private String TIME_RANGE = "time_range";
    private String COUNT = "count";
    private List<Map<String,Long>> indicatorCount = new ArrayList<>();


    public void addIndicatorCount(long count, int timeRange){

        Map<String, Long> map = new HashMap<>();
        map.put(COUNT,count);
        map.put(TIME_RANGE,(long)timeRange);
        indicatorCount.add(map);

    }

    @JsonProperty("indicator_count")
    public List<Map<String, Long>> getIndicatorCount() {
        return indicatorCount;
    }

    public void setIndicatorCount(List<Map<String, Long>> indicatorCount) {
        this.indicatorCount = indicatorCount;
    }
}
