package presidio.ade.test.utils.generators;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import org.apache.commons.collections.map.SingletonMap;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.data.generators.common.*;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.IEventGenerator;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * generates hourly Aggregation Records
 */
//public class AdeAggregationRecordHourlyGenerator implements IEventGenerator<AdeAggregationRecord> {
public class AdeAggregationRecordHourlyGenerator{
    private IStringListGenerator contextIdGenerator;
    private ITimeGenerator startInstantGenerator;
    private IMapGenerator valueToAggregatedFeatureGenerator;
    private final static String CONTEXT = "userId";

    public AdeAggregationRecordHourlyGenerator(IMapGenerator valueToAggregatedFeatureGenerator,
                                               ITimeGenerator startInstantGenerator,
                                               IStringListGenerator contextIdGenerator) throws GeneratorException {


        this.startInstantGenerator = startInstantGenerator;
        this.contextIdGenerator = contextIdGenerator;
        this.valueToAggregatedFeatureGenerator = valueToAggregatedFeatureGenerator;

    }


    public List<AdeAggregationRecord> generate() throws GeneratorException {
        List<AdeAggregationRecord> evList = new ArrayList<>();

        // fill list of events
        while (startInstantGenerator.hasNext()) {

            Instant startInstant = startInstantGenerator.getNext();
            Instant endInstant = startInstant.plus(FixedDurationStrategy.HOURLY.toDuration());
            Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToValueMap = valueToAggregatedFeatureGenerator.getNext();

            List<String> contextIds = contextIdGenerator.getNext();

            contextIds.forEach(context -> {
                        Map<String, String> contextId = new SingletonMap(CONTEXT, context);
                        aggregatedFeatureEventConfToValueMap.forEach((aggregatedFeatureConf, featureValue) -> {
                                    String featureName = aggregatedFeatureConf.getName();
                                    String bucketConfName = aggregatedFeatureConf.getBucketConfName();
                                    AggregatedFeatureType aggregatedFeatureType = getAggregatedFeatureType();
                                    AdeAggregationRecord record = generateAggregationRecord(startInstant, endInstant, featureName, featureValue, bucketConfName,
                                            contextId, aggregatedFeatureType);

                                    evList.add(record);
                                }
                        );
                    }
            );
        }
        return evList;
    }

    /**
     * Create AdeAggregationRecord record
     *
     * @param startInstant          start instant
     * @param endInstant            end instant
     * @param featureName           feature name
     * @param featureValue          feature value
     * @param bucketConfName        bucket config name
     * @param context               map of context
     * @param aggregatedFeatureType aggregated feature type
     * @return AdeAggregationRecord
     */
    protected AdeAggregationRecord generateAggregationRecord(Instant startInstant,
                                                             Instant endInstant, String featureName,
                                                             Double featureValue, String bucketConfName,
                                                             Map<String, String> context, AggregatedFeatureType aggregatedFeatureType) {

        return new AdeAggregationRecord(startInstant, endInstant,
                featureName, featureValue, bucketConfName, context, aggregatedFeatureType);
    }

    /**
     * @return AggregatedFeatureType (e.g: SCORE_AGGREGATION, FEATURE_AGGREGATION)
     */
    protected AggregatedFeatureType getAggregatedFeatureType() {
        return AggregatedFeatureType.SCORE_AGGREGATION;
    }

}
