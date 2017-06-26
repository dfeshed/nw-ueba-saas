package presidio.ade.domain.record.scored;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.feature.FeatureStringValue;
import fortscale.common.feature.FeatureValue;
import fortscale.utils.recordreader.ReflectionRecordReader;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.scored.enriched_scored.AdeScoredEnrichedRecord;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A record reader for {@link AdeRecord}s.
 */
public class AdeScoredRecordReader extends AdeRecordReader {
    private AdeScoredRecord adeScoredRecord;

    /**
     * C'tor.
     *
     * @param adeScoredRecord    the ADE Scored record from which values are extracted
     * @param fieldPathDelimiter this ADE record reader's field path delimiter (evaluated as a regular expression)
     */
    public AdeScoredRecordReader(@NotNull AdeScoredRecord adeScoredRecord, @NotNull String fieldPathDelimiter) {
        super(adeScoredRecord, fieldPathDelimiter);
        this.adeScoredRecord = adeScoredRecord;
    }

    /**
     * Get map of feature name to feature.
     *
     * @return map
     */
    @Override
    public Map<String, Feature> getAllFeatures(Set<String> featureNames) {
        Map<String, Feature> featureMap = new HashMap<>();

        for (String featureName : featureNames) {
            if (featureName.equals(adeScoredRecord.getFeatureName())) {
                Double score = adeScoredRecord.getScore();
                Feature feature = toFeature(featureName, score);
                featureMap.put(featureName, feature);
            }
        }

        return featureMap;
    }

    /**
     * Create feature of feature name and score.
     *
     * @param name  feature name
     * @param score score
     * @return Feature
     */
    private static Feature toFeature(String name, Double score) {
        FeatureValue featureValue = new FeatureNumericValue((score));
        return new Feature(name, featureValue);
    }

    /**
     * Default c'tor (default field path delimiter is used).
     *
     * @param adeScoredRecord the ADE scored record from which values are extracted
     */
    public AdeScoredRecordReader(@NotNull AdeScoredRecord adeScoredRecord) {
        super(adeScoredRecord);
        this.adeScoredRecord = adeScoredRecord;
    }

}
