package fortscale.acumulator.translator;

import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationService;
import org.apache.commons.lang3.StringUtils;

/**
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

    public AccumulatedFeatureTranslator(AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService) {
        this.aggregatedFeatureNameTranslationService = aggregatedFeatureNameTranslationService;
    }

//    todo: entityCollectionTranslation
    /**
     * translates feature name to accumulated feature name
     *
     * @param featureName i.e. distinct_number_of_normalized_src_machine_kerberos_logins
     * @return i.e accumulated daily collection name: scored___aggr_event__distinct_number_of_normalized_src_machine_kerberos_logins_d_acm
     */
    public String toAcmAggrCollection(String featureName) {
        String aggregatedFeatureCollectionName = aggregatedFeatureNameTranslationService.toCollectionName(featureName);
        String collectionName;
        if (aggregatedFeatureCollectionName.endsWith(DAILY_COLLECTION_SUFFIX)) {
            collectionName =
                    aggregatedFeatureCollectionName.replaceAll(DAILY_COLLECTION_REGEX, ACCUMULATED_DAILY_COLLECTION_SUFFIX);
        } else if (aggregatedFeatureCollectionName.endsWith(HOURLY_COLLECTION_SUFFIX)) {
            collectionName =
                    aggregatedFeatureCollectionName.replaceAll(HOURLY_COLLECTION_REGEX, ACCUMULATED_HOURLY_COLLECTION_SUFFIX);
        } else {
            collectionName =
                    StringUtils.join(aggregatedFeatureCollectionName, NAME_DELIMITER, ACCUMULATED_COLLECTION_SUFFIX);
        }
        return collectionName;
    }
}
