package presidio.output.domain.services.entities;

import fortscale.utils.elasticsearch.PartialUpdateRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntityEnums;
import presidio.output.domain.records.entity.EntityQuery;
import presidio.output.domain.repositories.EntityRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityPersistencyServiceImpl implements EntityPersistencyService {
    @Autowired
    private EntityRepository entityRepository;

    @Override
    public Entity save(Entity entity) {
        entity.updateFieldsBeforeSave();
        return entityRepository.save(entity);
    }

    @Override
    public Iterable<Entity> save(List<Entity> entities) {
        if (entities != null && entities.size() > 0) {
            entities.forEach(Entity::updateFieldsBeforeSave);
            return entityRepository.saveAll(entities);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Entity findEntityByDocumentId(String documentId) {
        return entityRepository.findById(documentId).get();
    }

    @Override
    public Iterable<Entity> findAll() {
        return entityRepository.findAll();
    }

    /**
     * Finds by a collection of repository generated ids(elastic)
     */
    public Page<Entity> findByIds(Collection<String> ids, PageRequest pageRequest) {
        return entityRepository.findByIdIn(ids, pageRequest);
    }

    public Page<Entity> findByEntityId(String entityId, PageRequest pageRequest) {
        return entityRepository.findByEntityId(entityId, pageRequest);
    }

    @Override
    public Page<Entity> find(EntityQuery entityQuery) {
        return entityRepository.search(new EntityElasticsearchQueryBuilder(entityQuery).build());
    }

    @Override
    public Stream<Entity> findEntitiesByLastUpdateLogicalDateAndEntityType(Instant startDate, Instant endDate, String entityType) {
        return entityRepository.findByLastUpdateLogicalStartDateGreaterThanEqualAndLastUpdateLogicalEndDateLessThanEqualAndEntityType(startDate.toEpochMilli(), endDate.toEpochMilli(), entityType);
    }

    @Override
    public void updateTrend(EntityEnums.Trends trend, String id, double score) {
        PartialUpdateRequest updateRequest = buildPartialUpdateRequest(trend, id, score);
        entityRepository.updateEntity(updateRequest);
    }

    @Override
    public void updateTrends(EntityEnums.Trends trend, Map<String, Double> entityScores) {
        List<PartialUpdateRequest> updateRequests = entityScores.entrySet().stream()
                                                    .map(entityScore -> buildPartialUpdateRequest(trend, entityScore.getKey(), entityScore.getValue()))
                                                    .collect(Collectors.toList());
        entityRepository.updateEntities(updateRequests);
    }

    @Override
    public void clearTrends(EntityEnums.Trends trend, Instant untilInstant) {
        RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(Entity.LAST_UPDATE_BY_LOGICAL_END_DATE_FIELD_NAME).lte(untilInstant.toEpochMilli());
        String field = Entity.TRENDING_SCORE_FIELD_NAME + "." + trend;
        entityRepository.updateEntitiesByQuery(new NativeSearchQuery(queryBuilder), field,0);
    }

    private PartialUpdateRequest buildPartialUpdateRequest(EntityEnums.Trends trend, String id, double score) {
        Map<String, Double> trendScore = Map.of(trend.name(), score);
        return new PartialUpdateRequest(id).withField(Entity.TRENDING_SCORE_FIELD_NAME, trendScore);
    }

}
