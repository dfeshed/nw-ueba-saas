package presidio.output.domain.repositories;

import fortscale.utils.elasticsearch.PartialUpdateRequest;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.List;

public interface EntityRepositoryCustom {

    /**
     * Update specific entity fields which will be merged into the existing entity (AKA partial update)
     *
     * @param updateRequest request to update fields for a single entity
     * @return true if the entity updated successfully
     */
    boolean updateEntity (PartialUpdateRequest updateRequest);


    /**
     * Bulk update partial fields of entities.
     *
     * @param updateRequests list of partial update requests
     * @return true if all the entities updated successfully
     */
    boolean updateEntities (List<PartialUpdateRequest> updateRequests);


    /**
     * Update entities by query
     * @param searchQuery query to update only entities that match the query
     * @param field field name to update
     * @param value field value to update
     * @return
     */
    boolean updateEntitiesByQuery(SearchQuery searchQuery, String field, Object value);

}
