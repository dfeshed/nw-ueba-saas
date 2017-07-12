package fortscale.common.elastic.services;

import fortscale.common.elastic.repository.Repository;
import fortscale.utils.logging.Logger;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

public class ElasticExportService implements ExportService {

    private final Logger logger = Logger.getLogger(ElasticExportService.class);


    private Repository repository;

    public ElasticExportService(Repository repository) {
        this.repository = repository;
    }

    public String export(IndexQuery query) {
        return repository.export(query);
    }

}
