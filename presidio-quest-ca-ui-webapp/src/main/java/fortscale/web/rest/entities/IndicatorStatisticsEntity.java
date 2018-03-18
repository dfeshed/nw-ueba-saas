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

    private long count;

    @JsonProperty("count")
    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}



