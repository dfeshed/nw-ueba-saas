package presidio.ade.domain.store;

import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.ConversionUtils;
import fortscale.utils.time.TimeRange;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;

public class ScoredDataReaderMongoUtils {
    public static void addContextFieldCriteria(Query query, String contextFieldNamePrefix, MultiKeyFeature contextFields) {
        contextFields.getFeatureNameToValue().forEach((contextFieldName, contextFieldValue) ->
                query.addCriteria(Criteria.where(contextFieldNamePrefix + contextFieldName).is(contextFieldValue))
        );
    }

    public static void addFieldCriteria(Query query, String instantFieldName, MultiKeyFeature fields) {
        fields.getFeatureNameToValue().forEach((fieldName, fieldValue) ->
                query.addCriteria(Criteria.where(fieldName).is(
                        fieldName.equals(instantFieldName) ?
                        ConversionUtils.convertToObject(fieldValue, Instant.class) :
                        fieldValue
                ))
        );
    }

    public static void addScoreThresholdCriterion(Query query, String scoreFieldName, int scoreThreshold) {
        query.addCriteria(Criteria.where(scoreFieldName).gt(scoreThreshold));
    }

    public static void addTimeRangeCriterion(Query query, String instantFieldName, TimeRange timeRange) {
        query.addCriteria(Criteria.where(instantFieldName).gte(timeRange.getStart()).lt(timeRange.getEnd()));
    }

    public static void addTimeRangeCriterion(Query query, String instantFieldName, TimeRange timeRange, MultiKeyFeature fields) {
        if (!fields.getFeatureNameToValue().containsKey(instantFieldName)) {
            addTimeRangeCriterion(query, instantFieldName, timeRange);
        }
    }
}
