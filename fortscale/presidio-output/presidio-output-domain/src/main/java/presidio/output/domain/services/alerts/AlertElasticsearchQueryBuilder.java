package presidio.output.domain.services.alerts;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.records.alerts.Alert;

import static org.elasticsearch.index.query.QueryBuilders.*;

public class AlertElasticsearchQueryBuilder extends NativeSearchQueryBuilder {

    public AlertElasticsearchQueryBuilder(AlertQuery alertQuery) {

        // filters
        withFilter(alertQuery);

        // sort
        withSort(alertQuery);

        // paging
        withPageable(alertQuery);

        // aggregations
        addAggregation(alertQuery);
    }

    private void withFilter(AlertQuery alertQuery) {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // filter by username
        if (StringUtils.isNotEmpty(alertQuery.getFilterBuUserName())) {
            boolQueryBuilder.must(matchQuery(Alert.USER_NAME, alertQuery.getFilterBuUserName()));
        }

        // filter by severity
        if (StringUtils.isNotEmpty(alertQuery.getFilterBySeverity())) {
            boolQueryBuilder.must(matchQuery(Alert.SEVERITY, alertQuery.getFilterBySeverity()));
        }

        // filter by date range
        if (alertQuery.getFilterByStartDate() > 0 || alertQuery.getFilterByEndDate() > 0) {
            RangeQueryBuilder rangeQuery = rangeQuery(Alert.START_DATE);
            if (alertQuery.getFilterByStartDate() > 0 ) {
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

    private void withSort(AlertQuery alertQuery) {
        if (StringUtils.isNotEmpty(alertQuery.getSortField())) {
            FieldSortBuilder sortBuilder = new FieldSortBuilder(alertQuery.getSortField());
            SortOrder order = alertQuery.isAscendingOrder()? SortOrder.ASC: SortOrder.DESC;
            sortBuilder.order(order);
            super.withSort(sortBuilder);
        }
    }

    private void withPageable(AlertQuery alertQuery) {
        if (alertQuery.getPageNumber() > -1 || alertQuery.getPageSize() > -1) {
            PageRequest pageRequest = new PageRequest(alertQuery.getPageNumber(), alertQuery.getPageSize());
            super.withPageable(pageRequest);
        }
    }

    private void addAggregation(AlertQuery alertQuery) {
        if (alertQuery.isAggregateBySeverity()) {
            TermsAggregationBuilder termsAggregationBuilder = new TermsAggregationBuilder(Alert.SEVERITY, ValueType.STRING);
            super.addAggregation(termsAggregationBuilder);
        }
    }
}
