package fortscale.common.elastic.repository;


import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

public class ElasticRepository implements Repository {

    private final ElasticsearchTemplate elasticsearchTemplate;

    public ElasticRepository(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public String export(IndexQuery query) {
        return elasticsearchTemplate.index(query);
    }

}
