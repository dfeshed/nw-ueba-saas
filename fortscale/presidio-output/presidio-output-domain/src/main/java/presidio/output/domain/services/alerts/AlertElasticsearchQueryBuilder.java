package presidio.output.domain.services.alerts;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.services.ElasticsearchQueryBuilder;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

public class AlertElasticsearchQueryBuilder extends ElasticsearchQueryBuilder<AlertQuery> {

    public AlertElasticsearchQueryBuilder(AlertQuery alertQuery) {
        super(alertQuery);
    }

    public void withFilter(AlertQuery alertQuery) {
        final BoolQueryBuilder boolQueryBuilder2 = new BoolQueryBuilder();
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // filter by username
        if (CollectionUtils.isNotEmpty(alertQuery.getFilterByUserName())) {
            BoolQueryBuilder userNameQuery = new BoolQueryBuilder();
            for (String userName : alertQuery.getFilterByUserName()) {
                userNameQuery.should(matchQuery(Alert.USER_NAME, userName));
            }
            boolQueryBuilder.must(userNameQuery);
        }

        // filter by user id
        if (CollectionUtils.isNotEmpty(alertQuery.getFilterByUserId())) {
            BoolQueryBuilder userIdQuery = new BoolQueryBuilder();
            for (String userId : alertQuery.getFilterByUserId()) {
                userIdQuery.should(matchQuery(Alert.USER_ID, userId));
            }
            boolQueryBuilder.must(userIdQuery);
        }

        // filter by severity
        if (CollectionUtils.isNotEmpty(alertQuery.getFilterBySeverity())) {
            BoolQueryBuilder severityQuery = new BoolQueryBuilder();
            for (String severity : alertQuery.getFilterBySeverity()) {
                severityQuery.should(matchQuery(Alert.SEVERITY, severity));
            }

            boolQueryBuilder.must(severityQuery);
        }

        // filter by classification
        if (CollectionUtils.isNotEmpty(alertQuery.getFilterByClassification())) {
            BoolQueryBuilder classificationQuery = new BoolQueryBuilder();
            for (String classification : alertQuery.getFilterByClassification()) {
                classificationQuery.should(matchQuery(Alert.CLASSIFICATIONS, classification));
            }
            boolQueryBuilder.must(classificationQuery);
        }

        // filter by start date
        if (alertQuery.getFilterByStartDate() > 0) {
            boolQueryBuilder.must(rangeQuery(Alert.START_DATE).gte(alertQuery.getFilterByStartDate()));
        }

        // filter by end date (meaning that the alert start date is less than the specified query filter
        if (alertQuery.getFilterByEndDate() > 0) {
            boolQueryBuilder.must(rangeQuery(Alert.START_DATE).to(alertQuery.getFilterByEndDate()).includeUpper(true));
        }

        // filter by tags
        if (CollectionUtils.isNotEmpty(alertQuery.getFilterByTags())) {
            BoolQueryBuilder tagsQuery = new BoolQueryBuilder();
            for (String tag : alertQuery.getFilterByTags()) {
                tagsQuery.should(matchQuery(Alert.USER_TAGS_FIELD_NAME, tag).operator(Operator.OR));
            }
            boolQueryBuilder.must(tagsQuery);
        }

        // filter by indicator names
        if (CollectionUtils.isNotEmpty(alertQuery.getFilterByIndicatorNames())) {
            BoolQueryBuilder indicatorNameQuery = new BoolQueryBuilder();
            for (String indicatorName : alertQuery.getFilterByIndicatorNames()) {
                indicatorNameQuery.should(matchQuery(Alert.INDICATOR_NAMES, indicatorName).operator(Operator.OR));
            }
            boolQueryBuilder.must(indicatorNameQuery);
        }

        // filter by min or max score
        if (alertQuery.getFilterByMinScore() > 0 || alertQuery.getFilterByMaxScore() > 0) {
            RangeQueryBuilder rangeQuery = rangeQuery(Alert.SCORE);
            if (alertQuery.getFilterByMinScore() > -1)
                rangeQuery.gte(alertQuery.getFilterByMinScore());
            if (alertQuery.getFilterByMaxScore() > -1)
                rangeQuery.lte(alertQuery.getFilterByMaxScore());
            boolQueryBuilder.must(rangeQuery);

            boolQueryBuilder.must(rangeQuery);
        }


        boolQueryBuilder2.filter(boolQueryBuilder);
        if (boolQueryBuilder.hasClauses()) {
            super.withQuery(boolQueryBuilder2);
        }
    }

    public void withSort(AlertQuery alertQuery) {
        if (alertQuery.getSort() != null) {

            alertQuery.getSort().forEach(order -> {
                FieldSortBuilder sortBuilder = new FieldSortBuilder(order.getProperty());
                SortOrder direction = order.getDirection().name().equals(SortOrder.ASC.name()) ? SortOrder.ASC : SortOrder.DESC;
                sortBuilder.order(direction);
                super.withSort(sortBuilder);
            });

        }
    }

    public void withPageable(AlertQuery alertQuery) {
        if (alertQuery.getPageNumber() > -1 || alertQuery.getPageSize() > -1) {
            PageRequest pageRequest = new PageRequest(alertQuery.getPageNumber(), alertQuery.getPageSize());
            super.withPageable(pageRequest);
        }
    }

    public void addAggregation(AlertQuery alertQuery) {
        if (CollectionUtils.isNotEmpty(alertQuery.getAggregateByFields())) {
            if (alertQuery.getAggregateByFields().contains(Alert.SEVERITY)) {
                super.addAggregation(AggregationBuilders.terms(Alert.SEVERITY).field(Alert.SEVERITY));
            }
            if (alertQuery.getAggregateByFields().contains(Alert.CLASSIFICATIONS)) {
                super.addAggregation(AggregationBuilders.terms(Alert.CLASSIFICATIONS).field(Alert.CLASSIFICATIONS));
            }
            if (alertQuery.getAggregateByFields().contains(Alert.INDICATOR_NAMES)) {
                super.addAggregation(AggregationBuilders.terms(Alert.INDICATOR_NAMES).field(Alert.INDICATOR_NAMES));
            }
            if (alertQuery.getAggregateByFields().contains(Alert.AGGR_SEVERITY_PER_DAY)) {
                super.addAggregation(AggregationBuilders.dateHistogram(Alert.AGGR_SEVERITY_PER_DAY).field(Alert.START_DATE)
                        .dateHistogramInterval(DateHistogramInterval.DAY)
                        .subAggregation(AggregationBuilders.terms(Alert.SEVERITY).field(Alert.SEVERITY)));
            }
        }
    }
}
