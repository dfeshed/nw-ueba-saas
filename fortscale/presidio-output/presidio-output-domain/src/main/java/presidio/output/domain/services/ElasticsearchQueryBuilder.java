package presidio.output.domain.services;

import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import presidio.output.domain.records.alerts.AlertQuery;

/**
 * Created by efratn on 22/08/2017.
 */
public abstract class ElasticsearchQueryBuilder<T> extends NativeSearchQueryBuilder {

    public abstract void withFilter(T query);
    public abstract void withSort(T query);
    public abstract void withPageable(T query);
    public abstract void addAggregation(T query);

    public ElasticsearchQueryBuilder(T query) {
        // filters
        withFilter(query);

        // sort
        withSort(query);

        // paging
        withPageable(query);

        // aggregations
        addAggregation(query);
    }
}
