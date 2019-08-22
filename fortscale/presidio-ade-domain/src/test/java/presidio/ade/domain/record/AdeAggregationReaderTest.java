package presidio.ade.domain.record;

import org.junit.Assert;
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
        String sslValue = "bla";
        String scoreValue = "0";
        contextMap.put("sslSubject.name", sslValue);
        contextMap.put("score", scoreValue);
        AdeAggregationReader adeAggregationReader = new AdeAggregationReader(new AdeAggregationRecord(
                Instant.now(), Instant.now(), "featureName", 0.0, "bucketConf",
                contextMap, AggregatedFeatureType.FEATURE_AGGREGATION), new HashMap<>());
        Assert.assertEquals(sslValue, adeAggregationReader.getFeature("context.sslSubject.name", String.class));
        Assert.assertEquals(scoreValue, adeAggregationReader.getFeature("context.score", String.class));
    }
}