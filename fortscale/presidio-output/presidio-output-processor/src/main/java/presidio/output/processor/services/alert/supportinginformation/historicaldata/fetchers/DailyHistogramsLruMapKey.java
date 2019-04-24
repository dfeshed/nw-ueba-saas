package presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;

public class DailyHistogramsLruMapKey {
    private final TimeRange timeRange;
    private final Map<String, String> contexts;
    private final Schema schema;
    private final String featureName;
    private final String featureBucketConfName;
    private final boolean includeOnlyBaseline;

    public DailyHistogramsLruMapKey(
            TimeRange timeRange,
            Map<String, String> contexts,
            Schema schema,
            String featureName,
            String featureBucketConfName,
            boolean includeOnlyBaseline) {

        this.timeRange = timeRange;
        this.contexts = contexts;
        this.schema = schema;
        this.featureName = featureName;
        this.featureBucketConfName = featureBucketConfName;
        this.includeOnlyBaseline = includeOnlyBaseline;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof DailyHistogramsLruMapKey)) return false;
        DailyHistogramsLruMapKey that = (DailyHistogramsLruMapKey)object;
        return new EqualsBuilder()
                .append(timeRange, that.timeRange)
                .append(contexts, that.contexts)
                .append(schema, that.schema)
                .append(featureName, that.featureName)
                .append(featureBucketConfName, that.featureBucketConfName)
                .append(includeOnlyBaseline, that.includeOnlyBaseline)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(timeRange)
                .append(contexts)
                .append(schema)
                .append(featureName)
                .append(featureBucketConfName)
                .append(includeOnlyBaseline)
                .toHashCode();
    }
}
