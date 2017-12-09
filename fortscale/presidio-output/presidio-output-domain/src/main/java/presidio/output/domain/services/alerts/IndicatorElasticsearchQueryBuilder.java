package presidio.output.domain.services.alerts;

import presidio.output.domain.records.alerts.IndicatorQuery;
import presidio.output.domain.services.ElasticsearchQueryBuilder;


public class IndicatorElasticsearchQueryBuilder extends ElasticsearchQueryBuilder<IndicatorQuery> {

    public IndicatorElasticsearchQueryBuilder(IndicatorQuery query) {
        super(query);
    }


    @Override
    public void withSort(IndicatorQuery query) {

    }

    @Override
    public void withPageable(IndicatorQuery query) {

    }

    @Override
    public void withFilter(IndicatorQuery query) {

    }

    @Override
    public void addAggregation(IndicatorQuery query) {

    }
}
