package presidio.monitoring.endPoint;

import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.time.Instant;
import java.util.Map;


public class MetricUniqueKey {

    private final String name;
    private final Instant logicTime;
    private final Map<MetricEnums.MetricTagKeysEnum, String> tags;

    public MetricUniqueKey(String name, Instant logicTime, Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        this.name = name;
        this.logicTime = logicTime;
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetricUniqueKey)) return false;

        MetricUniqueKey that = (MetricUniqueKey) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (logicTime != null ? !logicTime.equals(that.logicTime) : that.logicTime != null) return false;
        return tags != null ? tags.equals(that.tags) : that.tags == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (logicTime != null ? logicTime.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }


}
