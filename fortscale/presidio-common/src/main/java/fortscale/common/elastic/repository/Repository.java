package fortscale.common.elastic.repository;


import org.springframework.data.elasticsearch.core.query.IndexQuery;

public interface Repository {
    String export(IndexQuery query);
}
