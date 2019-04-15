package presidio.webapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.logging.Logger;
import fortscale.utils.rest.jsonpatch.JsonPatch;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.elasticsearch.search.aggregations.Aggregation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.util.ObjectUtils;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.webapp.model.*;

import java.util.*;

public class RestEntityServiceImpl implements RestEntityService {

    private static final Logger logger = Logger.getLogger(RestEntityService.class);


    private final RestAlertService restAlertService;
    private final EntityPersistencyService entityPersistencyService;
    private final int pageNumber;
    private final int pageSize;
    private ObjectMapper objectMapper;

    public RestEntityServiceImpl(RestAlertService restAlertService, EntityPersistencyService entityPersistencyService, int pageSize, int pageNumber) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.restAlertService = restAlertService;
        this.entityPersistencyService = entityPersistencyService;
        objectMapper = ObjectMapperProvider.defaultJsonObjectMapper();
    }

    @Override
    public Entity getEntityById(String entityId, boolean expand) {
        List<Alert> alerts = null;
        presidio.output.domain.records.entity.Entity entity = entityPersistencyService.findEntityById(entityId);
        if (expand)
            alerts = restAlertService.getAlertsByEntityId(entityId, false).getAlerts();
        return createCompatibleResult(entity, alerts);
    }

    @Override
    public EntitiesWrapper getEntities(EntityQuery entityQuery) {
        Page<presidio.output.domain.records.entity.Entity> entities =
                entityPersistencyService.find(convertEntityQuery(entityQuery));
        List<Entity> restEntities = new ArrayList<>();

        if (entities != null && entities.getNumberOfElements() > 0) {
            Map<String, List<Alert>> entityIdsToAlertsMap = new HashMap<>();

            // Get the alert data for the received entities
            if (entityQuery.getExpand()) {
                List<String> entityIds = new ArrayList<>();
                for (presidio.output.domain.records.entity.Entity entity : entities) {
                    entityIds.add(entity.getId());
                }

                entityIdsToAlertsMap = restAlertService.getAlertsByEntityIds(entityIds);
            }

            // Create the rest response
            for (presidio.output.domain.records.entity.Entity entity : entities) {
                List<Alert> alertList = null;
                if (MapUtils.isNotEmpty(entityIdsToAlertsMap)) {
                    alertList = entityIdsToAlertsMap.get(entity.getId());
                }
                restEntities.add(createCompatibleResult(entity, alertList));
            }

            Map<String, Aggregation> entityAggregationsMap = null;
            if (CollectionUtils.isNotEmpty(entityQuery.getAggregateBy())) {
                entityAggregationsMap = ((AggregatedPageImpl<presidio.output.domain.records.entity.Entity>) entities).getAggregations().asMap();
            }
            return createEntitiesWrapper(restEntities, ((Long) entities.getTotalElements()).intValue(), entityQuery.getPageNumber(), entityAggregationsMap);
        } else {
            return createEntitiesWrapper(null, 0, 0, null);
        }
    }

    @Override
    public AlertsWrapper getAlertsByEntityId(String entityId) {
        return restAlertService.getAlertsByEntityId(entityId, false);
    }

    @Override
    public Entity updateEntity(String entityId, JsonPatch updateRequest)
    {
        presidio.output.domain.records.entity.Entity entityById = entityPersistencyService.findEntityById(entityId);
        entityById = patchEntity(updateRequest, entityById);
        entityPersistencyService.save(entityById);
        return createCompatibleResult(entityById, null);
    }

    @Override
    public EntitiesWrapper updateEntities(EntityQuery entityQuery, JsonPatch jsonPatch)
    {
        // Getting the relevant entities
        Page<presidio.output.domain.records.entity.Entity> entities = entityPersistencyService.find(
                convertEntityQuery(entityQuery));
        List<presidio.output.domain.records.entity.Entity> updatedEntities = new ArrayList<>();

        // apply the update
        updateEntities(jsonPatch, entities, updatedEntities);

        // Get the remaining entities
        while (entities.hasNext()) {
            entityQuery.setPageSize(entities.nextPageable().getPageSize());
            entityQuery.setPageNumber(entities.nextPageable().getPageNumber());
            entities = entityPersistencyService.find(convertEntityQuery(entityQuery));
            updateEntities(jsonPatch, entities, updatedEntities);
        }

        entityPersistencyService.save(updatedEntities);
        return createEntitiesWrapper(updatedEntities, Long.valueOf(entities.getTotalElements()).intValue(), null, null);
    }

    private void updateEntities(JsonPatch jsonPatch, Page<presidio.output.domain.records.entity.Entity> entities, List<presidio.output.domain.records.entity.Entity> updatedEntities) {
        entities.getContent().forEach(entity -> updatedEntities.add(patchEntity(jsonPatch, entity)));
    }

    private Entity createCompatibleResult(presidio.output.domain.records.entity.Entity entity, List<Alert> alerts) {
        Entity convertedEntity = new Entity();
        if (ObjectUtils.isEmpty(entity))
            return null;
        convertedEntity.setId(entity.getId());
        convertedEntity.setEntityId(entity.getEntityId());
        if (CollectionUtils.isNotEmpty(alerts))
            convertedEntity.setAlerts(alerts);
        if (entity.getSeverity() != null) {
            convertedEntity.setSeverity(
                    presidio.webapp.model.EntityQueryEnums.EntitySeverity.valueOf(entity.getSeverity().name()));
        }
        if (entity.getEntityType() != null) {
            convertedEntity.setEntityType(entity.getEntityType());
        }
        convertedEntity.setScore((int) entity.getScore());
        convertedEntity.setTags(entity.getTags());
        convertedEntity.setEntityName(entity.getEntityName());
        convertedEntity.setAlertClassifications(entity.getAlertClassifications());
        convertedEntity.setAlertsCount(entity.getAlertsCount());
        return convertedEntity;
    }

    private presidio.output.domain.records.entity.EntityQuery convertEntityQuery(EntityQuery entityQuery) {
        presidio.output.domain.records.entity.EntityQuery.EntityQueryBuilder builder = new presidio.output.domain.records.entity.EntityQuery.EntityQueryBuilder();
        if (CollectionUtils.isNotEmpty(entityQuery.getAlertClassifications())) {
            builder.filterByAlertClassifications(entityQuery.getAlertClassifications());
        }
        if (entityQuery.getEntityName() != null) {
            builder.filterByEntityName(entityQuery.getEntityName());
        }
        if (entityQuery.getEntityType() != null) {
            builder.filterByEntitiesTypes(Collections.singletonList(entityQuery.getEntityType()));
        }
        if (entityQuery.getIndicatorsName() != null) {
            builder.filterByIndicators(entityQuery.getIndicatorsName());
        }
        if (entityQuery.getFreeText() != null) {
            builder.filterByFreeText(entityQuery.getFreeText());
        }
        if (entityQuery.getMaxScore() != null) {
            builder.maxScore(entityQuery.getMaxScore());
        }
        if (entityQuery.getMinScore() != null) {
            builder.minScore(entityQuery.getMinScore());
        }
        if (CollectionUtils.isNotEmpty(entityQuery.getSeverity())) {
            builder.filterBySeverities(convertSeverities(entityQuery.getSeverity()));
        }
        if (entityQuery.getPageSize() != null) {
            builder.pageSize(entityQuery.getPageSize());
        }
        if (entityQuery.getPageNumber() != null) {
            builder.pageNumber(entityQuery.getPageNumber());
        }
        if (entityQuery.getIsPrefix() != null) {
            builder.filterByEntityNameWithPrefix(entityQuery.getIsPrefix());
        }
        if (CollectionUtils.isNotEmpty(entityQuery.getTags())) {
            builder.filterByEntityTags(entityQuery.getTags());
        }
        if (CollectionUtils.isNotEmpty(entityQuery.getSortFieldNames()) && entityQuery.getSortDirection() != null) {
            List<Sort.Order> orders = new ArrayList<>();
            entityQuery.getSortFieldNames().forEach(s -> orders.add(new Sort.Order(entityQuery.getSortDirection(), s.toString())));
            builder.sort(new Sort(orders));
        }
        if (CollectionUtils.isNotEmpty(entityQuery.getAggregateBy())) {
            List<String> aggregateByFields = new ArrayList<>();
            entityQuery.getAggregateBy().forEach(alertQueryAggregationFieldName -> aggregateByFields.add(alertQueryAggregationFieldName.toString()));
            builder.aggregateByFields(aggregateByFields);
        }
        return builder.build();
    }

    private List<EntitySeverity> convertSeverities(List<presidio.webapp.model.EntityQueryEnums.EntitySeverity> severityEnumList) {
        List<EntitySeverity> entitySeverities = new ArrayList<>();
        severityEnumList.forEach(severity -> entitySeverities.add(EntitySeverity.valueOf(severity.toString())));
        return entitySeverities;
    }

    private EntitiesWrapper createEntitiesWrapper(List entities, int totalNumberOfElements, Integer pageNumber, Map<String, Aggregation> entityAggregationsMap) {
        EntitiesWrapper entitiesWrapper = new EntitiesWrapper();
        if (CollectionUtils.isNotEmpty(entities)) {
            entitiesWrapper.setEntities(entities);
            entitiesWrapper.setTotal(totalNumberOfElements);
            if (pageNumber != null) {
                entitiesWrapper.setPage(pageNumber);
            }
            if (MapUtils.isNotEmpty(entityAggregationsMap)) {
                Map<String, String> aggregationNamesEnumMapping = new HashMap<>();
                entityAggregationsMap.keySet().forEach(aggregationName -> {
                    aggregationNamesEnumMapping.put(aggregationName, EntityQueryEnums.EntityQueryAggregationFieldName.fromValue(aggregationName).name());
                });
                Map<String, Map<String, Long>> aggregations = RestUtils.convertAggregationsToMap(entityAggregationsMap, aggregationNamesEnumMapping);
                entitiesWrapper.setAggregationData(aggregations);
            }
        } else {
            entitiesWrapper.setEntities(new ArrayList());
            entitiesWrapper.setTotal(0);
            entitiesWrapper.setPage(0);
        }
        return entitiesWrapper;
    }

    private presidio.output.domain.records.entity.Entity patchEntity(JsonPatch updateRequest, presidio.output.domain.records.entity.Entity entityById) {
        JsonNode patchedJson;
        try {
            JsonNode entityJsonNode = objectMapper.valueToTree(entityById);
            patchedJson = updateRequest.apply(entityJsonNode);
            entityById = objectMapper.treeToValue(patchedJson, presidio.output.domain.records.entity.Entity.class);
        } catch (Exception e) {
            logger.error("Error parsing or processing  the entity object to or from json", e);
        }
        return entityById;
    }
}
