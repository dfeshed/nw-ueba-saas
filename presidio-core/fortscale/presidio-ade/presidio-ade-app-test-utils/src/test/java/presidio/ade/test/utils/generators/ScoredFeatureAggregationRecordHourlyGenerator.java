package presidio.ade.test.utils.generators;

import fortscale.domain.feature.score.FeatureScore;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IMapGenerator;
import presidio.data.generators.common.IStringListGenerator;
import presidio.data.generators.common.time.ITimeGenerator;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Generates hourly Scored Feature Aggregation events
 */
public class ScoredFeatureAggregationRecordHourlyGenerator extends AdeAggregationRecordHourlyGenerator {
    private List<FeatureScore> featureScores;
    private Double featureValue;

    public ScoredFeatureAggregationRecordHourlyGenerator(
            IMapGenerator scoreToAggregatedFeatureGenerator,
            IStringListGenerator contextIdGenerator,
            ITimeGenerator startInstantGenerator) throws GeneratorException {
        super(scoreToAggregatedFeatureGenerator, startInstantGenerator, contextIdGenerator);
        this.featureScores = Collections.emptyList();
        this.featureValue = 0.0;
    }


    /**
     *  Create ScoredFeatureAggregationRecord record
     */
    @Override
    protected AdeAggregationRecord generateAggregationRecord(Instant startInstant,
                                                             Instant endInstant, String featureName,
                                                             Double score, String bucketConfName,
                                                             Map<String, String> context, AggregatedFeatureType aggregatedFeatureType){
        return new ScoredFeatureAggregationRecord(score,featureScores, startInstant, endInstant,
                featureName, featureValue, bucketConfName, context, aggregatedFeatureType);
    }

    @Override
    protected AggregatedFeatureType getAggregatedFeatureType(){
        return AggregatedFeatureType.FEATURE_AGGREGATION;
    }

}
