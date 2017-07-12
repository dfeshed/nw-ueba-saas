package fortscale.common.elastic.services;


import org.springframework.data.elasticsearch.core.query.IndexQuery;

public interface ExportService {
    String export(IndexQuery query);
}
