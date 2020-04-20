package presidio.ade.domain.store;

import fortscale.utils.ConversionUtils;
import fortscale.utils.time.TimeRange;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.Map;

public class ScoredDataReaderMongoUtils {
    public static Query buildScoredRecordsQuery(
            String startInstantFieldName, TimeRange timeRange,
            String contextFieldNamePrefix, Map<String, String> contextFields,
            String scoreFieldName, int scoreThreshold) {

        Query query = new Query();
        addTimeRangeCriterion(query, startInstantFieldName, timeRange);
        addContextFieldCriteria(query, contextFieldNamePrefix, contextFields);
        addScoreThresholdCriterion(query, scoreFieldName, scoreThreshold);
        return query;
    }

    public static Query buildScoredRecordQuery(
            Map<String, String> fields, String startInstantFieldName, TimeRange timeRange,
            String contextFieldNamePrefix, Map<String, String>  contextFields,
            String scoreFieldName, int scoreThreshold,
            Direction direction) {

        Query query = new Query();

        if (!fields.containsKey(startInstantFieldName)) {
            addTimeRangeCriterion(query, startInstantFieldName, timeRange);
        }

        addContextFieldCriteria(query, contextFieldNamePrefix, contextFields);
        addFieldCriteria(query, startInstantFieldName, fields);
        addScoreThresholdCriterion(query, scoreFieldName, scoreThreshold);
        query = query.with(new Sort(direction, startInstantFieldName));
        return query;
    }

    /*******************
     * Private methods *
     *******************/

    private static void addContextFieldCriteria(Query query, String contextFieldNamePrefix, Map<String, String> contextFields) {
        contextFields.forEach((contextFieldName, contextFieldValue) ->
                query.addCriteria(Criteria.where(contextFieldNamePrefix + contextFieldName).is(contextFieldValue))
        );
    }

    private static void addFieldCriteria(Query query, String startInstantFieldName, Map<String, String> fields) {
        fields.forEach((fieldName, fieldValue) ->
                query.addCriteria(Criteria.where(fieldName).is(
                        fieldName.equals(startInstantFieldName) ?
                        ConversionUtils.convertToObject(fieldValue, Instant.class) :
                        fieldValue
                ))
        );
    }

    private static void addScoreThresholdCriterion(Query query, String scoreFieldName, int scoreThreshold) {
        query.addCriteria(Criteria.where(scoreFieldName).gt(scoreThreshold));
    }

    private static void addTimeRangeCriterion(Query query, String startInstantFieldName, TimeRange timeRange) {
        query.addCriteria(Criteria.where(startInstantFieldName).gte(timeRange.getStart()).lt(timeRange.getEnd()));
    }
}
