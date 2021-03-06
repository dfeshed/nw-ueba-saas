package presidio.output.processor.services.entity;

import com.google.common.collect.Iterators;
import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import fortscale.utils.recordreader.RecordReader;
import fortscale.utils.recordreader.ReflectionRecordReader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import presidio.output.commons.services.entity.EntityMappingServiceImpl;
import presidio.output.commons.services.entity.EntitySeverityService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntityEnums;
import presidio.output.domain.records.entity.EntityQuery;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.domain.services.event.EventPersistencyService;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by efratn on 22/08/2017.
 */
public class EntityServiceImpl implements EntityService {

    private static Logger log = Logger.getLogger(EntityServiceImpl.class);

    private static final int ENTITIES_SAVE_PAGE_SIZE = 1000;

    private final AlertPersistencyService alertPersistencyService;
    private final EventPersistencyService eventPersistencyService;
    private final EntityPersistencyService entityPersistencyService;
    private final EntityScoreService entityScoreService;
    private final EntitySeverityService entitySeverityService;
    private final EntityMappingServiceImpl entityMappingServiceImpl;
    private final String TAG_ADMIN = "admin";

    private final int alertEffectiveDurationInDays;//How much days an alert can affect on the entity score
    private final int defaultEntitiesBatchSize;

    public EntityServiceImpl(EventPersistencyService eventPersistencyService,
                             EntityPersistencyService entityPersistencyService,
                             AlertPersistencyService alertPersistencyService,
                             EntityScoreService entityScoreService,
                             EntitySeverityService entitySeverityService,
                             int alertEffectiveDurationInDays,
                             int defaultEntitiesBatchSize,
                             EntityMappingServiceImpl entityMappingServiceImpl) {
        this.eventPersistencyService = eventPersistencyService;
        this.entityPersistencyService = entityPersistencyService;
        this.alertPersistencyService = alertPersistencyService;
        this.entityScoreService = entityScoreService;
        this.entitySeverityService = entitySeverityService;
        this.alertEffectiveDurationInDays = alertEffectiveDurationInDays;
        this.defaultEntitiesBatchSize = defaultEntitiesBatchSize;
        this.entityMappingServiceImpl = entityMappingServiceImpl;
    }

    public int getDefaultEntitiesBatchSize() {
        return defaultEntitiesBatchSize;
    }

    @Override
    public Entity createEntity(String entityId, String entityType) {
        EntityDetails entityDetails = getEntityDetails(entityId, entityType);
        if (entityDetails == null) {
            return null;
        }
        return new Entity(entityDetails.getEntityId(), entityDetails.getEntityName(), entityDetails.getTags(), entityType);
    }

    @Override
    public Entity findEntityById(String entityDocumentId) {
        return entityPersistencyService.findEntityByDocumentId(entityDocumentId);
    }

    @Override
    public List<Entity> save(List<Entity> entities) {
        Iterable<Entity> savedEntities = entityPersistencyService.save(entities);
        return IteratorUtils.toList(savedEntities.iterator());
    }

    private EntityDetails getEntityDetails(String entityId, String entityType) {
        String entityNameField = entityMappingServiceImpl.getEntityNameField(entityType);
        String entityName;
        if(entityNameField.equals(entityType)){
            entityName = entityId;
        }
        else {
            List <Schema> schemas = entityMappingServiceImpl.getSchemas(entityType);
            List<String> collectionNames = entitySeverityService.collectionNamesForSchemas(schemas);
            EnrichedEvent event = eventPersistencyService.findLatestEventForEntity(entityId, collectionNames, entityType);
            if (event == null) {
                log.error("no events were found for entity {}", entityId);
                return null;
            }

            RecordReader recordReader = new ReflectionRecordReader(event);
            entityName = recordReader.get(entityNameField, String.class);
        }

        List<String> tags = new ArrayList<>();
        return new EntityDetails(entityName, entityId, tags, entityType);
    }


    @Override
    public void setEntityAlertData(Entity entity, EntitiesAlertData entitiesAlertData) {
        if (CollectionUtils.isNotEmpty(entitiesAlertData.getClassifications())) {
            entity.setAlertClassifications(new ArrayList<String>(entitiesAlertData.getClassifications()));
        }
        if (CollectionUtils.isNotEmpty(entitiesAlertData.getIndicators())) {
            entity.setIndicators(new ArrayList<String>(entitiesAlertData.getIndicators()));
        }
        entity.setAlertsCount(entitiesAlertData.getAlertsCount());
        entity.setScore(entitiesAlertData.getEntityScore());
        entity.setLastAlertDate(entitiesAlertData.getLastAlertDate());
        EntitySeverity newSeverity = entitySeverityService.getSeveritiesMap(false, entity.getEntityType()).getEntitySeverity(entity.getScore());
        entity.setSeverity(newSeverity);
    }

    @Override
    public void setEntityAlertDataToDefault(Entity entity) {
        entity.setAlertClassifications(null);
        entity.setIndicators(null);
        entity.setAlertsCount(0);
        entity.setScore(0);
        entity.setLastAlertDate(null);
        EntitySeverity newSeverity = EntitySeverity.LOW;
        entity.setSeverity(newSeverity);
    }

    @Override
    public void addEntityAlertData(Entity entity, EntitiesAlertData entitiesAlertData) {
        List<String> classificationUnion = unionOfCollectionsToList(entity.getAlertClassifications(), entitiesAlertData.getClassifications());
        entity.setAlertClassifications(classificationUnion);
        List<String> indicatorsUnion = unionOfCollectionsToList(entity.getIndicators(), entitiesAlertData.getIndicators());
        entity.setIndicators(indicatorsUnion);
        entity.incrementAlertsCountByNumber(entitiesAlertData.getAlertsCount());
        entity.incrementEntityScoreByNumber(entitiesAlertData.getEntityScore());
        if (entity.getLastAlertDate() == null || entity.getLastAlertDate().before(entitiesAlertData.getLastAlertDate())) {
            entity.setLastAlertDate(entitiesAlertData.getLastAlertDate());
        }
        EntitySeverity newSeverity = entitySeverityService.getSeveritiesMap(false, entity.getEntityType()).getEntitySeverity(entity.getScore());
        entity.setSeverity(newSeverity);
    }

    @Override
    public void updateEntityData(Instant endDate, String entityType) {
        log.debug("Starting Updating all entities alert data.");
        updateAllEntitiesAlertData(endDate, entityType);
        log.debug("finished updating all entities alert data.");
        entitySeverityService.updateSeverities(entityType);
    }

    @Override
    public boolean updateAllEntitiesAlertData(Instant endDate, String entityType) {

        //Get map of entities ids to new score and alerts count
        Map<String, EntitiesAlertData> aggregatedEntityScore = entityScoreService.calculateEntityAlertsData(alertEffectiveDurationInDays, endDate, entityType);

        //Get entities in batches and update the score only if it changed, and add to changesEntities
        Set<String> entitiesIDForBatch = new HashSet<>();
        List<Entity> changedEntities = new ArrayList<>();
        for (Map.Entry<String, EntitiesAlertData> entry : aggregatedEntityScore.entrySet()) {

            entitiesIDForBatch.add(entry.getKey());
            if (entitiesIDForBatch.size() < defaultEntitiesBatchSize) {
                continue;
            }
            //Update entity score batch
            changedEntities.addAll(updateEntityAlertDataForBatch(aggregatedEntityScore, entitiesIDForBatch));


            //After batch calculation, reset the set
            entitiesIDForBatch.clear();

        }

        if (!entitiesIDForBatch.isEmpty()) {
            //there is leftover smaller then batch size
            changedEntities.addAll(updateEntityAlertDataForBatch(aggregatedEntityScore, entitiesIDForBatch));
        }

        //Persist entities that the score changed
        log.info(changedEntities.size() + " entities changed. Saving to database");

        Double pages = Math.ceil(changedEntities.size() / (ENTITIES_SAVE_PAGE_SIZE * 1D));
        for (int i = 0; i < pages.intValue(); i++) {
            List<Entity> page = changedEntities.subList(i * ENTITIES_SAVE_PAGE_SIZE, Math.min((i + 1) * ENTITIES_SAVE_PAGE_SIZE, changedEntities.size()));
            entityPersistencyService.save(page);
        }
        log.info(changedEntities.size() + " entities saved to database");

        //Clean entities which not have alert in the last 90 days, but still have score
        entityScoreService.clearEntityScoreForEntitiesThatShouldNotHaveScore(aggregatedEntityScore.keySet(), entityType);
        //Reset alerts contribution if alert startDate is before 90 days
        entityScoreService.clearAlertsContributionThatShouldNotHaveContributionToEntityScore(alertEffectiveDurationInDays, endDate, entityType);

        return true;
    }

    /**
     * @param aggregatedEntityScore - all the entities which have at least one alert in the last 3 month with the new score
     * @param entitiesIDForBatch     - only the ids in the current handled batch
     * @return List of updated entities
     */
    public List<Entity> updateEntityAlertDataForBatch(Map<String, EntitiesAlertData> aggregatedEntityScore, Set<String> entitiesIDForBatch) {
        log.info("Updating entity batch (without persistence)- batch contain: " + entitiesIDForBatch.size() + " entities");
        List<Entity> changedEntities = new ArrayList<>();

        PageRequest pageRequest = new PageRequest(0, entitiesIDForBatch.size());
        Page<Entity> entities = entityPersistencyService.findByIds(entitiesIDForBatch, pageRequest);

        if (entities.getTotalElements() != entitiesIDForBatch.size()) {
            log.error("Need to update {} entities, but only {} entities exists on elastic search", entitiesIDForBatch.size(), entities.getTotalElements());
        }
        entities.forEach(entity -> {
            double newEntityScore = aggregatedEntityScore.get(entity.getId()).getEntityScore();
            if (entity.getScore() != newEntityScore) {
                setEntityAlertData(entity, aggregatedEntityScore.get(entity.getId()));
                changedEntities.add(entity);
            }
        });

        return changedEntities;
    }

    public List<Entity> findEntityByVendorEntityIdAndType(String vendorEntityId, String entityType) {
        EntityQuery entityQuery = new EntityQuery
                .EntityQueryBuilder()
                .filterByEntitiesIds(Collections.singletonList(vendorEntityId))
                .filterByEntitiesTypes(Collections.singletonList(entityType))
                .build();

        Page<Entity> entitiesPage = this.entityPersistencyService.find(entityQuery);
        if (!entitiesPage.hasContent() || entitiesPage.getContent().size() < 1) {
            return null;
        }
        return entitiesPage.getContent();
    }

    private List<String> unionOfCollectionsToList(Collection col1, Collection col2) {
        if (CollectionUtils.isEmpty(col1) || CollectionUtils.isEmpty(col2)) {
            if (CollectionUtils.isEmpty(col1) && CollectionUtils.isEmpty(col2)) {
                return null;
            } else {
                return CollectionUtils.isEmpty(col1) ? new ArrayList<String>(col2) : new ArrayList<String>(col1);
            }
        } else {
            return (List<String>) CollectionUtils.union(col1, col2);
        }
    }

    @Override
    public void recalculateEntityAlertData(Entity entity) {
        List<Alert> alerts = alertPersistencyService.findByEntityDocumentId(entity.getId());
        EntitiesAlertData entitiesAlertData = new EntitiesAlertData();
        if (CollectionUtils.isNotEmpty(alerts)) {
            alerts.forEach(alert -> {
                if (alert.getContributionToEntityScore() > 0) {
                    entitiesAlertData.addClassification(alert.alertPrimaryClassification());
                    entitiesAlertData.addIndicators(alert.getIndicatorsNames());
                    entitiesAlertData.incrementAlertsCount();
                    entitiesAlertData.incrementEntityScore(alert.getContributionToEntityScore());
                    entitiesAlertData.setLastAlertDate(alert.getEndDate());
                }

            });
            setEntityAlertData(entity, entitiesAlertData);
        } else {
            setEntityAlertDataToDefault(entity);
        }
    }

    @Override
    public void updateEntityTrends(EntityEnums.Trends trend, Instant time, String entityType) {
        Instant startTime = time.minus(trend.getPeriod());
        Map<String, Double> entityScores = entityScoreService.calculateEntityScores(startTime, time, entityType);
        Iterators.partition(entityScores.entrySet().iterator(), defaultEntitiesBatchSize).forEachRemaining(scores -> {
            entityPersistencyService.updateTrends(trend, scores.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        });
        entityPersistencyService.clearTrends(trend, startTime);
    }
}
