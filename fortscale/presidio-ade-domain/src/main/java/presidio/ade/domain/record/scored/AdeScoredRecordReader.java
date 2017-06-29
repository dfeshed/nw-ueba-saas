package presidio.ade.domain.record.scored;

import fortscale.common.feature.Feature;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeRecordReader;

import javax.validation.constraints.NotNull;
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

        String featureName = adeScoredRecord.getFeatureName();
        if (featureNames.contains(featureName)) {
            Double score = adeScoredRecord.getScore();
            Feature feature = Feature.toFeature(featureName, score);
            featureMap.put(featureName, feature);
        }

        return featureMap;
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
