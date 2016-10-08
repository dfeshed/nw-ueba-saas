package fortscale.aggregation.feature.event.store.translator;

import org.apache.commons.lang3.StringUtils;

/**
 * translates aggregatedFeatureName
 * Created by barak_schuster on 10/6/16.
 */
public class AggregatedFeatureNameTranslationService {
    private static final String COLLECTION_NAME_PREFIX = "scored_";
    private static final String COLLECTION_NAME_SEPARATOR = "__";

    private String eventType;

    /**
     * event type to be concatenated in case the of {@link this#toCollectionName(String)} translation
     * @param eventType
     */
    public AggregatedFeatureNameTranslationService(String eventType)
    {
        this.eventType = eventType;
    }

    /**
     * @param aggregatedFeatureName - name to be translates
     * @return translation of aggregatedFeatureName to mongo collection name
     */
    public String toCollectionName(String aggregatedFeatureName) {
        return StringUtils.join(
                COLLECTION_NAME_PREFIX, COLLECTION_NAME_SEPARATOR,
                eventType, COLLECTION_NAME_SEPARATOR,
                aggregatedFeatureName);
    }

}
