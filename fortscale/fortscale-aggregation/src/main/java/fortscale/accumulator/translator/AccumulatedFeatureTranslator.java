package fortscale.accumulator.translator;

import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationService;
import fortscale.entity.event.translator.EntityEventTranslationService;
import org.apache.commons.lang3.StringUtils;

/**
 * translates feature name to accumulated collection name
 * Created by barak_schuster on 10/6/16.
 */
public class AccumulatedFeatureTranslator {

    private static final String DAILY_COLLECTION_SUFFIX = "daily";
    private static final String DAILY_COLLECTION_REGEX = String.format("%s$", DAILY_COLLECTION_SUFFIX);
    private static final String HOURLY_COLLECTION_SUFFIX = "hourly";
    private static final String HOURLY_COLLECTION_REGEX = String.format("%s$", HOURLY_COLLECTION_SUFFIX);
    private static final String NAME_DELIMITER = "_";
    public static final String ACCUMULATED_COLLECTION_SUFFIX = String.format("%sacm", NAME_DELIMITER);
    private static final String ACCUMULATED_DAILY_COLLECTION_SUFFIX = String.format("d%s", ACCUMULATED_COLLECTION_SUFFIX);
    private static final String ACCUMULATED_HOURLY_COLLECTION_SUFFIX = String.format("h%s", ACCUMULATED_COLLECTION_SUFFIX);

    private final AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService;
    private final EntityEventTranslationService entityEventTranslationService;

    /**
     * C'tor
     * @param aggregatedFeatureNameTranslationService
     * @param entityEventTranslationService
     */
    public AccumulatedFeatureTranslator(AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService,
                                        EntityEventTranslationService entityEventTranslationService) {
        this.aggregatedFeatureNameTranslationService = aggregatedFeatureNameTranslationService;
        this.entityEventTranslationService = entityEventTranslationService;
    }

    /**
     * translates feature name to accumulated feature name
     *
     * @param featureName i.e. scored___entity_event__data_exfiltration_normalized_username_daily
     * @return i.e accumulated daily collection name: scored___entity_event__data_exfiltration_normalized_username_d_acm
     */
    public String toAcmEntityEventCollection(String featureName)
    {
        String originalCollectionName = entityEventTranslationService.toCollectionName(featureName);
        return getAcmCollectionName(originalCollectionName);
    }

    /**
     * translates feature name to accumulated feature name
     *
     * @param featureName i.e. distinct_number_of_normalized_src_machine_kerberos_logins
     * @return i.e accumulated daily collection name: scored___aggr_event__distinct_number_of_normalized_src_machine_kerberos_logins_d_acm
     */
    public String toAcmAggrCollection(String featureName) {
        String originalCollectionName = aggregatedFeatureNameTranslationService.toCollectionName(featureName);
        return getAcmCollectionName(originalCollectionName);
    }

    private String getAcmCollectionName(String originalCollectionName) {
        String collectionName;
        if (originalCollectionName.endsWith(DAILY_COLLECTION_SUFFIX)) {
            collectionName =
                    originalCollectionName.replaceAll(DAILY_COLLECTION_REGEX, ACCUMULATED_DAILY_COLLECTION_SUFFIX);
        } else if (originalCollectionName.endsWith(HOURLY_COLLECTION_SUFFIX)) {
            collectionName =
                    originalCollectionName.replaceAll(HOURLY_COLLECTION_REGEX, ACCUMULATED_HOURLY_COLLECTION_SUFFIX);
        } else {
            collectionName =
                    StringUtils.join(originalCollectionName, NAME_DELIMITER, ACCUMULATED_COLLECTION_SUFFIX);
        }
        return collectionName;
    }

    public EntityEventTranslationService getEntityEventTranslationService() {
        return entityEventTranslationService;
    }

    public String aggrgatedCollectionNameRegex()
    {
        String eventType = aggregatedFeatureNameTranslationService.getEventType();
        return String.format(".*%s.*%s$", eventType, ACCUMULATED_COLLECTION_SUFFIX);
    }

    public String entityEventCollectioNameRegex()
    {
        String eventType = entityEventTranslationService.getEventTypeFieldValue();
        return String.format(".*%s.*%s$", eventType, ACCUMULATED_COLLECTION_SUFFIX);
    }

    public AggregatedFeatureNameTranslationService getAggregatedFeatureNameTranslationService() {
        return aggregatedFeatureNameTranslationService;
    }
}
