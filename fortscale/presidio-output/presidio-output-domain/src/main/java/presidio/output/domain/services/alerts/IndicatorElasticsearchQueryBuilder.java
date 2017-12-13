package presidio.output.domain.services.alerts;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorQuery;
import presidio.output.domain.services.ElasticsearchQueryBuilder;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;


public class IndicatorElasticsearchQueryBuilder extends ElasticsearchQueryBuilder<IndicatorQuery> {

    public IndicatorElasticsearchQueryBuilder(IndicatorQuery indicatorQuery) {
        super(indicatorQuery);
    }


    @Override
    public void withSort(IndicatorQuery indicatorQuery) {
        if (indicatorQuery.getSort() != null) {
            indicatorQuery.getSort().forEach(order -> {
                FieldSortBuilder sortBuilder = new FieldSortBuilder(order.getProperty());
                SortOrder direction = order.getDirection().name().equals(SortOrder.ASC.name()) ? SortOrder.ASC : SortOrder.DESC;
                sortBuilder.order(direction);
                super.withSort(sortBuilder);
            });
        }
    }

    @Override
    public void withPageable(IndicatorQuery indicatorQuery) {
        if (indicatorQuery.getPageNumber() > -1 || indicatorQuery.getPageSize() > -1) {
            PageRequest pageRequest = new PageRequest(indicatorQuery.getPageNumber(), indicatorQuery.getPageSize());
            super.withPageable(pageRequest);
        }

    }

    @Override
    public void withFilter(IndicatorQuery indicatorQuery) {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // filter by alert id
        if (StringUtils.isNotEmpty(indicatorQuery.getFilterByAlertsId())) {
            BoolQueryBuilder userNameQuery = new BoolQueryBuilder();
            userNameQuery.should(matchQuery(Indicator.ALERT_ID, indicatorQuery.getFilterByAlertsId()));
            boolQueryBuilder.must(userNameQuery);
        }
        super.withQuery(boolQueryBuilder);
    }

    @Override
    public void addAggregation(IndicatorQuery query) {

    }
}
