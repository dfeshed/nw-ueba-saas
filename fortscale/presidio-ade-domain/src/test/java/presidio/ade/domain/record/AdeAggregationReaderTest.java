package presidio.ade.domain.record;

import org.junit.Test;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class AdeAggregationReaderTest {

    @Test
    public void getFeature() {
        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("sslSubject.name", "bla");
        contextMap.put("score", "0");
        contextMap.put("context.sslSubject.name", "asfsdg");
        AdeAggregationReader adeAggregationReader = new AdeAggregationReader(new AdeAggregationRecord(
                Instant.now(), Instant.now(), "featureName", 0.0, "bucketConf",
                contextMap, AggregatedFeatureType.FEATURE_AGGREGATION), null);
        adeAggregationReader.getFeature("sslSubject.name", Object.class);
        adeAggregationReader.getFeature("score", Object.class);
        adeAggregationReader.getFeature("context.sslSubject.name", Object.class);
    }
}