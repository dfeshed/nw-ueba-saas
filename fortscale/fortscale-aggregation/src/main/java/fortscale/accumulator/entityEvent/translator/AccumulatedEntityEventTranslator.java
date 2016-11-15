package fortscale.accumulator.entityEvent.translator;

import fortscale.accumulator.translator.BaseAccumulatedFeatureTranslator;
import fortscale.entity.event.translator.EntityEventTranslationService;

/**
 * Created by barak_schuster on 10/16/16.
 */
public class AccumulatedEntityEventTranslator extends BaseAccumulatedFeatureTranslator {

    private final EntityEventTranslationService entityEventTranslationService;

    /**
     * C'tor
     * @param entityEventTranslationService
     *
     */
    public AccumulatedEntityEventTranslator(EntityEventTranslationService entityEventTranslationService) {
        this.entityEventTranslationService = entityEventTranslationService;
    }

    /**
     * translates feature name to accumulated feature name
     *
     * @param featureName i.e. scored___entity_event__data_exfiltration_normalized_username_daily
     * @return i.e accumulated daily collection name: scored___entity_event__data_exfiltration_normalized_username_d_acm
     */
    @Override
    public String toAcmCollectionName(String featureName) {
        String originalCollectionName = entityEventTranslationService.toCollectionName(featureName);
        return getAcmCollectionName(originalCollectionName);
    }

    @Override
    public String getAcmCollectionNameRegex() {
        String eventType = entityEventTranslationService.getEventTypeFieldValue();
        return String.format(".*%s.*%s$", eventType, ACCUMULATED_COLLECTION_SUFFIX);
    }
}
