package presidio.output.domain.repositories;

import fortscale.utils.elasticsearch.PartialUpdateRequest;
import fortscale.utils.logging.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQueryBuilder;
import presidio.output.domain.records.entity.Entity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityRepositoryImpl implements EntityRepositoryCustom {

    private static final Logger logger = Logger.getLogger(EntityRepositoryImpl.class);

    private final ElasticsearchOperations elasticsearchOperations;

    public EntityRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public boolean updateEntity(PartialUpdateRequest updateRequest) {
        UpdateQuery updateQuery = buildQuery (updateRequest.getDocumentId(), updateRequest.getFields());
        UpdateResponse response = elasticsearchOperations.update(updateQuery);
        return DocWriteResponse.Result.UPDATED.equals(response.getResult());
    }


    @Override
    public boolean updateEntities(List<PartialUpdateRequest> updateRequests) {
        List<UpdateQuery> queries =  updateRequests.stream()
                                    .map(request -> buildQuery(request.getDocumentId(), request.getFields()))
                                    .collect(Collectors.toList());
        try {
            elasticsearchOperations.bulkUpdate(queries);
        } catch (ElasticsearchException ex) {
            logger.error("partial update was failed", ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean updateEntitiesByQuery(SearchQuery searchQuery, String field, Object value) {
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

    private UpdateQuery buildQuery(String entityId, Map fields) {
        UpdateRequest updateRequest = new UpdateRequest().doc(fields);
        return new UpdateQueryBuilder()
                .withId(entityId)
                .withClass(Entity.class)
                .withUpdateRequest(updateRequest).build();
    }

}
