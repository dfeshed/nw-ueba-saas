package presidio.output.domain.repositories;

import fortscale.utils.logging.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

public class AlertRepositoryImpl implements AlertRepositoryCustom {

    private static final Logger logger = Logger.getLogger(AlertRepositoryImpl.class);

    private final ElasticsearchOperations elasticsearchOperations;

    public AlertRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public boolean updateAlertsByQuery(SearchQuery searchQuery, String field, Object value) {
        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(elasticsearchOperations.getClient());
        updateByQuery.filter(searchQuery.getQuery());
        updateByQuery.script(new Script(String.format("ctx._source.%s=%s",field, value)));
        try {
            BulkByScrollResponse updateResponse = updateByQuery.get(TimeValue.timeValueHours(1));
            logger.debug("updateResponse = {}", updateResponse);
            return true;
        } catch (ElasticsearchException ex) {
            logger.error("update by query failed", ex);
            return false;
        }
    }
}
