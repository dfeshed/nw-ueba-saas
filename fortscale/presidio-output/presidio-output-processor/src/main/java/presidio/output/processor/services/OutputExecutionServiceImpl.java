package presidio.output.processor.services;

import com.google.common.collect.Iterators;
import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.monitoring.aspect.annotations.RunTime;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntityEnums;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.entity.EntityService;
import presidio.output.processor.services.entity.EntitiesAlertData;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by shays on 17/05/2017.
 * Main output functionality is implemented here
 */
public class OutputExecutionServiceImpl implements OutputExecutionService {
    private static final Logger logger = Logger.getLogger(OutputExecutionServiceImpl.class);

    private final AdeManagerSdk adeManagerSdk;
    private final AlertService alertService;
    private final EntityService entityService;
    private final EventPersistencyService eventPersistencyService;
    private final OutputMonitoringService outputMonitoringService;
    private final int smartThresholdScoreForCreatingAlert;
    private final int smartPageSize;
    private final int alertPageSize;
    private final long retentionOutputDataDays;


    private final int SMART_THRESHOLD_FOR_GETTING_SMART_ENTITIES = 0;

    public OutputExecutionServiceImpl(AdeManagerSdk adeManagerSdk,
                                      AlertService alertService,
                                      EntityService entityService,
                                      EventPersistencyService eventPersistencyService,
                                      OutputMonitoringService outputMonitoringService,
                                      int smartThresholdScoreForCreatingAlert, int smartPageSize, int alertPageSize, long retentionOutputDataDays) {
        this.adeManagerSdk = adeManagerSdk;
        this.alertService = alertService;
        this.entityService = entityService;
        this.eventPersistencyService = eventPersistencyService;
        this.smartPageSize = smartPageSize;
        this.alertPageSize = alertPageSize;
        this.smartThresholdScoreForCreatingAlert = smartThresholdScoreForCreatingAlert;
        this.retentionOutputDataDays = retentionOutputDataDays;
        this.outputMonitoringService = outputMonitoringService;
    }

    /**
     * Run the output processor main functionality which consist of the following-
     * 1. Get SMARTs from ADE and create Alerts entities for SMARTs with score higher than the threshold
     * 2. Enrich alerts with information from Input component (fields which were not part of the ADE schema)
     * 3. Alerts classification (rule based semantics)
     * 4. Calculates supporting information
     *
     * @param startDate
     * @param endDate
     * @throws Exception
     */
    @RunTime
    @Override
    public void run(Instant startDate, Instant endDate, String configurationName, String entityType) throws Exception {
        logger.debug("Started output process with params: start date {}:{}, end date {}:{}.", CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        List<PageIterator<SmartRecord>> smartPageIterators = adeManagerSdk.getSmartRecords(smartPageSize, smartPageSize, new TimeRange(startDate, endDate), SMART_THRESHOLD_FOR_GETTING_SMART_ENTITIES, configurationName);

        Map<String, Entity> entities = new HashMap<>();
        List<SmartRecord> smarts = null;
        List<Alert> alerts = new ArrayList<>();
        int indicatorsCountHourly = 0;
        int alertsCountHourly = 0;

        for(PageIterator<SmartRecord> smartPageIterator : smartPageIterators){
            while (smartPageIterator.hasNext()) {
                smarts = smartPageIterator.next();
                for (SmartRecord smart : smarts) {
                    String unexpectedSizeOfSmartContextMessage = String.format("Unexpected smart context size for smart: %s.", smart.getId());
                    Set<Map.Entry<String, String>> smartContextEntries = smart.getContext().entrySet();
                    Assert.isTrue(smartContextEntries.size() == 1, unexpectedSizeOfSmartContextMessage);
                    Map.Entry<String, String> contextEntry = smartContextEntries.iterator().next();
                    String entityId = contextEntry.getValue();

                    if (entityId == null || entityId.isEmpty()) {
                        logger.error("Failed to get entity id from smart context, entity id is null or empty for smart {}. skipping to next smart", smart.getId());
                        continue;
                    }
                    Entity entity;
                    if ((entity = getCreatedEntity(entities, entityId, entityType)) == null && (entity = getSingleEntityByIdAndType(entityId, entityType)) == null) {
                        //Need to create entity and add it to about to be created list
                        entity = entityService.createEntity(entityId, entityType);
                        if (entity == null) {
                            logger.error("Failed to process entity details for smart {}, skipping to next smart in the batch", smart.getId());
                            continue;
                        }
                        entities.put(entity.getEntityId(), entity);
                    }

                    Alert alertEntity = alertService.generateAlert(smart, entity, smartThresholdScoreForCreatingAlert);
                    if (alertEntity != null) {
                        EntitiesAlertData entitiesAlertData = new EntitiesAlertData(alertEntity.getContributionToEntityScore(), 1, alertEntity.alertPrimaryClassification(), alertEntity.getIndicatorsNames(), alertEntity.getEndDate());
                        entityService.addEntityAlertData(entity, entitiesAlertData);
                        alerts.add(alertEntity);
                        indicatorsCountHourly += alertEntity.getIndicatorsNum();

                        String classification = alertEntity.alertPrimaryClassification();
                        outputMonitoringService.reportTotalAlertCount(1, alertEntity.getSeverity(), classification, startDate);
                        alertsCountHourly++;
                    }

                    if (getCreatedEntity(entities, entity.getEntityId(), entity.getEntityType()) == null) {
                        entities.put(entity.getEntityId(), entity);
                    }

                    if (alerts.size() >= alertPageSize) {
                        flushAlerts(startDate, alerts);
                    }

                }
                flushAlerts(startDate, alerts);
            }
        }


        storeEntities(new ArrayList<>(entities.values())); //Get the generated entities with the new elasticsearch ID

        trendsCalculation(endDate, entityType);

        outputMonitoringService.reportTotalEntitiesCount(entities.size(), startDate, entityType);
        outputMonitoringService.reportNumericMetric(outputMonitoringService.INDICATORS_COUNT_HOURLY_METRIC_NAME, indicatorsCountHourly, startDate);
        outputMonitoringService.reportNumericMetric(outputMonitoringService.ALERTS_COUNT_HOURLY_METRIC_NAME, alertsCountHourly, startDate);


        if (CollectionUtils.isNotEmpty(smarts)) {
            outputMonitoringService.reportLastSmartTimeProcessed(smarts.get(smarts.size() - 1).getStartInstant().toEpochMilli(), startDate);
        }
        logger.info("output process application completed for start date {}:{}, end date {}:{}.", CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
    }

    private void flushAlerts(Instant startDate, List<Alert> alerts) {
        storeAlerts(alerts);
        outputMonitoringService.reportTotalAnomalyEvents(alerts, startDate);
        alerts.clear();
    }

    private Entity getSingleEntityByIdAndType(String entityId, String entityType) {
        List<Entity> entities = entityService.findEntityByVendorEntityIdAndType(entityId, entityType);
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        if (entities.size() > 1) {
            logger.error("Cannot have vendor entityId more then once {}", entityId);
        }
        return entities.get(0);
    }

    private Entity getCreatedEntity(Map<String, Entity> entities, String entityId, String entityType) {
        if(entities.containsKey(entityId) ) {
            Entity entity = entities.get(entityId);
            if (entity.getEntityType().equals(entityType)) {
                return entity;
            }
        }
        return null;
    }

    public void updateAllEntitiesData(Instant startDate, Instant endDate, String configurationName, String entityType) throws Exception {
        this.entityService.updateEntityData(endDate, entityType);
        logger.info("updating entities data completed successfully");

        logger.info("Starting to report daily metrics");
        outputMonitoringService.reportDailyMetrics(startDate, endDate, configurationName, entityType);
    }



    private void storeAlerts(List<Alert> alerts) {
        if (CollectionUtils.isNotEmpty(alerts)) {
            alertService.save(alerts);
        }
        logger.info("{} output alerts were generated", alerts.size());
    }

    private List<Entity> storeEntities(List<Entity> entities) {
        List<Entity> savedEntities = Collections.EMPTY_LIST;
        if (CollectionUtils.isNotEmpty(entities)) {
            logger.info("{} output entities were generated", entities.size());
            savedEntities = entityService.save(entities);
        }
        return savedEntities;

    }

    public void clean(Instant startDate, Instant endDate, String entityType) throws Exception {
        logger.debug("Start deleting alerts and updating entities score.");
        // delete alerts
        List<Alert> cleanedAlerts = alertService.cleanAlerts(startDate, endDate, entityType);

        // update entity scores
        updateEntitiesScoreFromDeletedAlerts(cleanedAlerts);

    }

    @Override
    public void cleanAlertsByTimeRangeAndEntityType(Instant startDate, Instant endDate, String entityType) throws Exception {
        clean(startDate, endDate, entityType);
    }

    @Override
    public void cleanAlertsForRetention(Instant endDate, String entityType) throws Exception {
        clean(Instant.EPOCH, endDate.minus(retentionOutputDataDays, ChronoUnit.DAYS), entityType);
    }

    private void updateEntitiesScoreFromDeletedAlerts(List<Alert> cleanedAlerts) {
        Set<Entity> entitiesToUpdate = new HashSet<>();
        cleanedAlerts.forEach(alert -> {
            if (!entitiesToUpdate.contains(alert.getEntityDocumentId())) {
                entitiesToUpdate.add(entityService.findEntityById(alert.getEntityDocumentId()));
            }
        });
        logger.info("{} entities are going to update score", entitiesToUpdate.size());
        entitiesToUpdate.forEach(entity -> {
            entityService.recalculateEntityAlertData(entity);
        });
        entityService.save(new ArrayList<>(entitiesToUpdate));
    }

    private void trendsCalculation(Instant endDate, String entityType) {
        logger.info("starting trends calculation");
        for (EntityEnums.Trends trend: EntityEnums.Trends.values()) {
            logger.info("calculating {} trends ", trend);
            entityService.updateEntityTrends(trend, endDate, entityType);
        }
        logger.info("trends calculation is done");
    }


    @Override
    public void cleanAll() throws Exception {
        // TODO: Implement
    }
}
