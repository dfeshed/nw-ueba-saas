package presidio.monitoring.elastic.allindexrepo;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import presidio.monitoring.records.MetricDocument;

import java.util.Collection;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

/**
 * This repository manages queries over ALL monitoring indexes (regardless the date index suffix)
 */
public class MetricsAllIndexesRepositoryImpl implements MetricsAllIndexesRepository {

    private final String MONITORING_ALIAS = "presidio-monitoring-*";

    private PresidioElasticsearchTemplate elasticsearchTemplate;

    public MetricsAllIndexesRepositoryImpl(PresidioElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate=elasticsearchTemplate;
    }

    @Override
    public List<MetricDocument> findByNameInAndLogicTimeGreaterThanEqualAndLogicTimeLessThan(Collection<String> names, long fromTime, long toTime) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();

        //filter by metric name:
        if (CollectionUtils.isNotEmpty(names)) {
            BoolQueryBuilder nameQuery = new BoolQueryBuilder();
            for (String name : names) {
                nameQuery.should(matchQuery("name", name).operator(Operator.OR));
            }
            queryBuilder.must(nameQuery);
        }

        //filter by specified logical time:
        queryBuilder.must(
                rangeQuery("logicTime")
                        .gte(fromTime)
                        .to(toTime)
                        .includeUpper(true)
                        .includeLower(true));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withIndices(MONITORING_ALIAS)
                .build();

        List<MetricDocument> metricDocuments = elasticsearchTemplate.queryForList(searchQuery, MetricDocument.class);
        return metricDocuments;
    }
}
