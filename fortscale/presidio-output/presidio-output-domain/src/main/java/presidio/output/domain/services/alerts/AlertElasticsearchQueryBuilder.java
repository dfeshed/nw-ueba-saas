package presidio.output.domain.services.alerts;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;
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
        if (StringUtils.isNotEmpty(alertQuery.getFilterByUserName())) {
            boolQueryBuilder.must(matchQuery(Alert.USER_NAME, alertQuery.getFilterByUserName()).operator(Operator.AND));
        }

        // filter by classification
        if (alertQuery.getFilterByClassification() != null && !(alertQuery.getFilterByClassification()).isEmpty()) {
            for (String classification : alertQuery.getFilterByClassification()) {
                boolQueryBuilder.must(matchQuery(Alert.CLASSIFICATION, classification).operator(Operator.OR));
            }
        }

        // filter by severity
        if (StringUtils.isNotEmpty(alertQuery.getFilterBySeverity())) {
            boolQueryBuilder.must(matchQuery(Alert.SEVERITY, alertQuery.getFilterBySeverity()));
        }

        // filter by date range
        if (alertQuery.getFilterByStartDate() > 0 || alertQuery.getFilterByEndDate() > 0) {
            RangeQueryBuilder rangeQuery = rangeQuery(Alert.START_DATE);
            if (alertQuery.getFilterByStartDate() > 0) {
                rangeQuery.from(alertQuery.getFilterByStartDate());
            }
            if (alertQuery.getFilterByEndDate() > 0) {
                rangeQuery.to(alertQuery.getFilterByEndDate());
            }
            boolQueryBuilder.must(rangeQuery);
        }

        if (boolQueryBuilder.hasClauses()) {
            super.withFilter(boolQueryBuilder);
        }
    }

    public void withSort(AlertQuery alertQuery) {
        if (StringUtils.isNotEmpty(alertQuery.getSortField())) {
            FieldSortBuilder sortBuilder = new FieldSortBuilder(alertQuery.getSortField());
            SortOrder order = alertQuery.isAscendingOrder() ? SortOrder.ASC : SortOrder.DESC;
            sortBuilder.order(order);
            super.withSort(sortBuilder);
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
            TermsAggregationBuilder termsAggregationBuilder = new TermsAggregationBuilder(Alert.SEVERITY, ValueType.STRING);
            super.addAggregation(termsAggregationBuilder);
        }
    }
}
