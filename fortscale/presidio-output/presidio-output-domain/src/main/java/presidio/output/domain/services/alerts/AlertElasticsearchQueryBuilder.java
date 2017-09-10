package presidio.output.domain.services.alerts;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
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
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // filter by username
        if (CollectionUtils.isNotEmpty(alertQuery.getFilterByUserName())) {
            boolQueryBuilder.should(matchQuery(Alert.USER_NAME, alertQuery.getFilterByUserName()).operator(Operator.OR));
        }

        // filter by severity
        if (CollectionUtils.isNotEmpty(alertQuery.getFilterBySeverity())) {
            boolQueryBuilder.should(matchQuery(Alert.SEVERITY, alertQuery.getFilterBySeverity()).operator(Operator.OR));
        }

        // filter by classification
        if (alertQuery.getFilterByClassification() != null && !(alertQuery.getFilterByClassification()).isEmpty()) {
            boolQueryBuilder.should(matchQuery(Alert.CLASSIFICATIONS, alertQuery.getFilterByClassification()).operator(Operator.OR));
        }

        // filter by date range
        if (alertQuery.getFilterByStartDate() > 0 && alertQuery.getFilterByEndDate() > 0 && alertQuery.getFilterByStartDate() < alertQuery.getFilterByEndDate()) {
            RangeQueryBuilder rangeQuery = rangeQuery(Alert.START_DATE);
            if (alertQuery.getFilterByStartDate() > 0) {
                rangeQuery.from(alertQuery.getFilterByStartDate());
            }
            if (alertQuery.getFilterByEndDate() > 0) {
                rangeQuery.to(alertQuery.getFilterByEndDate());
            }
            boolQueryBuilder.must(rangeQuery);
        }

        // filter by is user admin
        if (alertQuery.getFilterByIsUserAdmin() != null) {
            boolQueryBuilder.must(matchQuery(Alert.IS_USER_ADMIN_FIELD_NAME, alertQuery.getFilterByIsUserAdmin()).operator(Operator.AND));
        }

        if (boolQueryBuilder.hasClauses()) {
            super.withFilter(boolQueryBuilder);
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
        if (alertQuery.isAggregateBySeverity()) {
            super.addAggregation(AggregationBuilders.terms(Alert.SEVERITY).field(Alert.SEVERITY));
        }
    }
}
