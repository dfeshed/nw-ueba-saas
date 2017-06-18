package fortscale.accumulator.aggregation.translator;

import fortscale.accumulator.translator.BaseAccumulatedFeatureTranslator;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationService;

/**
 * Created by barak_schuster on 10/16/16.
 */
public class AccumulatedAggregatedFeatureEventTranslator extends BaseAccumulatedFeatureTranslator {

    private final AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService;

    /**
     * C'tor
     * @param aggregatedFeatureNameTranslationService
     *
     */
    public AccumulatedAggregatedFeatureEventTranslator(AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService) {
        this.aggregatedFeatureNameTranslationService = aggregatedFeatureNameTranslationService;
    }

    /**
     * translates feature name to accumulated feature name
     *
     * @param featureName i.e. distinct_number_of_normalized_src_machine_kerberos_logins
     * @return i.e accumulated daily collection name: scored___aggr_event__distinct_number_of_normalized_src_machine_kerberos_logins_d_acm
     */
    @Override
    public String toAcmCollectionName(String featureName) {
        String originalCollectionName = aggregatedFeatureNameTranslationService.toCollectionName(featureName);
        return getAcmCollectionName(originalCollectionName);
    }

    @Override
    public String getAcmCollectionNameRegex() {
        String eventType = aggregatedFeatureNameTranslationService.getEventType();
        return String.format(".*%s.*%s$", eventType, ACCUMULATED_COLLECTION_SUFFIX);
    }
}
