package presidio.monitoring.elastic.allindexrepo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.MapUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.util.CloseableIterator;
import presidio.monitoring.records.MetricDocument;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

/**
 * This repository manages queries over ALL monitoring indexes (regardless the date index suffix)
 */
public class MetricsAllIndexesRepositoryImpl implements MetricsAllIndexesRepository {

    private final String MONITORING_ALIAS = "presidio-monitoring-*";

    private ElasticsearchOperations elasticsearchTemplate;

    public MetricsAllIndexesRepositoryImpl(ElasticsearchOperations elasticsearchTemplate) {
        this.elasticsearchTemplate=elasticsearchTemplate;
    }

    @Override
    public List<MetricDocument> findByNameAndTime(Collection<String> names, long fromTime, long toTime, Map<String, String> tags) {
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
                        .lt(toTime));

        if (MapUtils.isNotEmpty(tags)) {
            BoolQueryBuilder tagsQuery = new BoolQueryBuilder();
            for (Map.Entry<String, String> tag : tags.entrySet()) {
                tagsQuery.should(matchQuery("tags." + tag.getKey(), tag.getValue()).operator(Operator.OR));
            }
            queryBuilder.must(tagsQuery);
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withIndices(MONITORING_ALIAS)
                .build();

        List<MetricDocument> metricDocuments;
        try (CloseableIterator<MetricDocument> iterator = elasticsearchTemplate.stream(searchQuery, MetricDocument.class)) {
            metricDocuments = IteratorUtils.toList(iterator);
        }

        return metricDocuments;
    }
}
