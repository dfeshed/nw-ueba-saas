package presidio.output.processor.services.entity;

import fortscale.utils.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntityQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.entities.EntityPersistencyService;

import java.time.*;
import java.util.*;

/**
 * Created by shays on 27/08/2017.
 */
public class EntityScoreServiceImpl implements EntityScoreService {
    private static final Logger log = Logger.getLogger(EntityScoreServiceImpl.class);

    private EntityPersistencyService entityPersistencyService;

    private AlertPersistencyService alertPersistencyService;

    private AlertSeverityService alertSeverityService;

    private int defaultAlertsBatchSize;

    public int defaultEntitiesBatchSize;

    public EntityScoreServiceImpl(EntityPersistencyService entityPersistencyService,
                                  AlertPersistencyService alertPersistencyService,
                                  AlertSeverityService alertSeverityService,
                                  int defaultAlertsBatchSize,
                                  int defaultEntitiesBatchSize) {
        this.entityPersistencyService = entityPersistencyService;
        this.alertPersistencyService = alertPersistencyService;
        this.alertSeverityService = alertSeverityService;
        this.defaultAlertsBatchSize = defaultAlertsBatchSize;
        this.defaultEntitiesBatchSize = defaultEntitiesBatchSize;
    }


    /**
     * Iterate all entities which have score more then 0, and reset the score to 0.
     * Excluded entity ids are entities which should not be reset.
     *
     * @param excludedEntitiesIds is the list of entities which should
     */
    @Override
    public void clearEntityScoreForEntitiesThatShouldNotHaveScore(Set<String> excludedEntitiesIds, String entityType) {
        log.debug("Check if there are entities without relevant alert and score higher then 0");

        EntityQuery.EntityQueryBuilder entityQueryBuilder = new EntityQuery.EntityQueryBuilder().minScore(1)
                .filterByEntitiesTypes(Collections.singletonList(entityType))
                .pageSize(defaultEntitiesBatchSize)
                .pageNumber(0);
        Page<Entity> entitiesPage = entityPersistencyService.find(entityQueryBuilder.build());

        log.debug("found " + entitiesPage.getTotalElements() + " entities which score that should be reset");
        List<Entity> clearedEntitiesList = new ArrayList<>();
        while (entitiesPage != null && entitiesPage.hasContent()) {
            entitiesPage.getContent().forEach(entity -> {
                if (!excludedEntitiesIds.contains(entity.getId())) {
                    entity.setScore(0D);
                    entity.setSeverity(null);
                    clearedEntitiesList.add(entity);
                }
            });

            entitiesPage = getNextEntityPage(entityQueryBuilder, entitiesPage);
        }

        log.info("Resetting " + clearedEntitiesList.size() + " entities scores and severity");
        entityPersistencyService.save(clearedEntitiesList);
    }

    /**
     * Iterate on all alerts for entities in the last alertEffectiveDurationInDays days,
     * and calculate the entity score based on the effective alerts.
     *
     * @return map of each entityId to an object that contains the new score and number of alerts
     */
    @Override
    public Map<String, EntitiesAlertData> calculateEntityScores(int alertEffectiveDurationInDays, Instant endDate, String entityType) {

        List<LocalDateTime> days = getListOfLastXdays(alertEffectiveDurationInDays, endDate);

        Map<String, EntitiesAlertData> aggregatedEntityScore = new HashMap<>();
        //TODO: also filter by status >

        if (days != null && days.size() > 0) {
            for (LocalDateTime startOfDay : days) {

                log.info("Start Calculate entity score for day " + startOfDay + " (Calculation, without persistency");
                long startTime = Date.from(startOfDay.atZone(ZoneOffset.UTC).toInstant()).getTime();
                LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
                long endTime = Date.from(endOfDay.atZone(ZoneOffset.UTC).toInstant()).getTime();


                AlertQuery.AlertQueryBuilder alertQueryBuilder = new AlertQuery.AlertQueryBuilder()
                        .filterByStartDate(startTime)
                        .filterByEndDate(endTime)
                        .filterByEntityType(entityType)
                        .sortField(Alert.START_DATE, true)
                        .setPageSize(this.defaultAlertsBatchSize)
                        .setPageNumber(0);

                AlertQuery alertQuery = alertQueryBuilder.build();

                Page<Alert> alertsPage = alertPersistencyService.find(alertQuery);
                while (alertsPage != null && alertsPage.hasContent()) {
                    alertsPage.getContent().forEach(alert -> {
                        String entityDocumentId = alert.getEntityDocumentId();
                        if (aggregatedEntityScore.containsKey(entityDocumentId)) {
                            EntitiesAlertData entitiesAlertData = aggregatedEntityScore.get(entityDocumentId);
                            entitiesAlertData.incrementEntityScore(alert.getContributionToEntityScore());
                            entitiesAlertData.incrementAlertsCount();
                            entitiesAlertData.addClassification(alert.alertPrimaryClassification());
                            entitiesAlertData.addIndicators(alert.getIndicatorsNames());
                        } else {
                            aggregatedEntityScore.put(entityDocumentId, new EntitiesAlertData(alert.getContributionToEntityScore(), 1, alert.alertPrimaryClassification(), alert.getIndicatorsNames()));
                        }

                    });
                    alertsPage = getNextAlertPage(alertQueryBuilder, alertsPage);
                }
            }
        }
        return aggregatedEntityScore;
    }

    private List<LocalDateTime> getListOfLastXdays(int days, Instant endTime) {

        LocalDate endDate = endTime.atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate startTime = endDate.minusDays(days);
        List<LocalDateTime> dates = new ArrayList<>();
        for (LocalDate d = startTime; !d.isAfter(endDate); d = d.plusDays(1)) {
            LocalDateTime time = d.atStartOfDay();
            dates.add(time);
        }
        return dates;
    }


    /**
     * Return the next entity page or null if no next
     *
     * @param page
     * @return
     */

    private Page<Entity> getNextEntityPage(EntityQuery.EntityQueryBuilder entityQueryBuilder, Page<Entity> page) {
        if (page.hasNext()) {
            Pageable pageable = page.nextPageable();
            entityQueryBuilder.pageNumber(pageable.getPageNumber());
            page = entityPersistencyService.find(entityQueryBuilder.build());

        } else {
            page = null;
        }
        return page;
    }

    /**
     * Return the next entity page or null if no next
     *
     * @param page
     * @return
     */

    private Page<Alert> getNextAlertPage(AlertQuery.AlertQueryBuilder alertQueryBuilder, Page<Alert> page) {
        if (page.hasNext()) {
            Pageable pageable = page.nextPageable();
            alertQueryBuilder.setPageNumber(pageable.getPageNumber());
            page = alertPersistencyService.find(alertQueryBuilder.build());

        } else {
            page = null;
        }
        return page;
    }


}
