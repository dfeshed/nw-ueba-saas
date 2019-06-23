package presidio.output.commons.services.entity;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import presidio.output.domain.records.PresidioRange;
import presidio.output.domain.records.EntitySeveritiesRangeDocument;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntityQuery;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.repositories.EntitySeveritiesRangeRepository;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by efrat Noam on 12/4/17.
 */
public class EntitySeverityServiceImpl implements EntitySeverityService {

    private static final Logger logger = Logger.getLogger(EntitySeverityServiceImpl.class);

    private Map<EntitySeverity, EntitySeverityComputeData> severityToComputeDataMap;

    @Autowired
    private EntitySeveritiesRangeRepository entitySeveritiesRangeRepository;

    @Autowired
    private EntityPersistencyService entityPersistencyService;

    private final OutputToCollectionNameTranslator outputToCollectionNameTranslator;

    @Value("${entity.batch.size:2000}")
    private int defaultEntitiesBatchSize;

    public EntitySeverityServiceImpl(Map<EntitySeverity, EntitySeverityComputeData> severityToComputeDataMap,
                                     OutputToCollectionNameTranslator outputToCollectionNameTranslator) {
        this.severityToComputeDataMap = severityToComputeDataMap;
        this.outputToCollectionNameTranslator = outputToCollectionNameTranslator;
    }

    /**
     * Calculate severities map which defines the right entity severity per entity score calculated according to percentiles
     *
     * @return map from score to getSeverity
     */
    @Override
    public EntityScoreToSeverity getSeveritiesMap(boolean recalcEntityScorePercentiles, String entityType) {
        if (!recalcEntityScorePercentiles) {
            return getExistingEntityScoreToSeverity(entityType);
        }

        //calculating percentiles according all entity scores
        double[] entityScores = getScoresArray(entityType);
        EntitySeveritiesRangeDocument entitySeveritiesRangeDocument = createEntitySeveritiesRangeDocument(entityScores, entityType);
        entitySeveritiesRangeRepository.save(entitySeveritiesRangeDocument);

        return new EntityScoreToSeverity(entitySeveritiesRangeDocument.getSeverityToScoreRangeMap());
    }

    /**
     * Create the entity severities range document from the received scores
     *
     * @param entityScores
     * @return
     */
    protected EntitySeveritiesRangeDocument createEntitySeveritiesRangeDocument(double[] entityScores, String entityType) {

        Map<EntitySeverity, PresidioRange<Double>> severityToScoreRangeMap = calculateEntitySeverityRangeMap(entityScores);

        return new EntitySeveritiesRangeDocument(severityToScoreRangeMap, entityType);
    }

    protected Map<EntitySeverity, PresidioRange<Double>> calculateEntitySeverityRangeMap(double[] entityScores) {

        if (ArrayUtils.isEmpty(entityScores)) {
            return createEmptyMap();
        }

        Map<EntitySeverity, PresidioRange<Double>> severityToScoreRangeMap = new LinkedHashMap<>();

        // sorting the scores
        Arrays.sort(entityScores);
        ArrayUtils.reverse(entityScores);

        // Get the severities in desc order
        List<EntitySeverity> severitiesOrderedAsc = EntitySeverity.getSeveritiesOrderedAsc();
        List<EntitySeverity> severitiesOrderedDesc = new LinkedList<>(severitiesOrderedAsc);
        Collections.reverse(severitiesOrderedDesc);

        Integer rangeStartIndex = 0;

        // Go over all the severities from Critical to Low
        for (EntitySeverity entitySeverity : severitiesOrderedDesc) {

            if (entitySeverity.equals(EntitySeverity.LOW)) {
                severityToScoreRangeMap.put(EntitySeverity.LOW, new PresidioRange<>(0d, entityScores[rangeStartIndex]));
            } else {

                // Get the details
                EntitySeverityComputeData entitySeverityComputeData = severityToComputeDataMap.get(entitySeverity);

                // Calculate the max entities that can get the severity as percentage from the entities
                int numberOfCalculatedFromPercent = (int) Math.floor(entitySeverityComputeData.getPercentageOfEntities() * ((double) entityScores.length) / 100);
                int rangeEndIndex = rangeStartIndex + numberOfCalculatedFromPercent;

                // If maxEntities set check that the amount calculated from percentage is not bigger that the entities allowed
                if (entitySeverityComputeData.getMaximumEntities() != null && numberOfCalculatedFromPercent > entitySeverityComputeData.getMaximumEntities()) {
                    rangeEndIndex = (int) (rangeStartIndex + entitySeverityComputeData.getMaximumEntities());
                }

                // Looking for the separation point between the severities
                for (int i = rangeEndIndex; i > rangeStartIndex; i--) {

                    // The delta between the scores is big enough to separate the severities
                    if (entityScores[i] * entitySeverityComputeData.getMinimumDeltaFactor() <= entityScores[i - 1]) {
                        double minSeverityScore = entityScores[i - 1];

                        // Set the severity boundaries
                        severityToScoreRangeMap.put(entitySeverity, new PresidioRange<>(minSeverityScore, entityScores[rangeStartIndex]));
                        rangeStartIndex = i;

                        break;
                    }
                }
            }
        }

        // Fix the mapping by going from low to critical and calculating the lower bound of each severity according to the
        // upper bound of the lower severity
        for (int i = 0; i < severitiesOrderedAsc.size() - 1; i++) {
            EntitySeverity severity = severitiesOrderedAsc.get(i);
            EntitySeverity higherSeverity = severitiesOrderedAsc.get(i + 1);

            Double upperBound = severityToScoreRangeMap.get(severity).getUpperBound();
            double minimumDelta = severityToComputeDataMap.get(higherSeverity).getMinimumDeltaFactor();

            PresidioRange<Double> higherSeverityRange = severityToScoreRangeMap.get(higherSeverity);

            // If no range set it
            if (higherSeverityRange == null) {
                severityToScoreRangeMap.put(higherSeverity, new PresidioRange<>(upperBound * minimumDelta, upperBound * minimumDelta));
            } else if (upperBound * minimumDelta < severityToScoreRangeMap.get(higherSeverity).getLowerBound()) {
                // Set the new lower and upper bound
                severityToScoreRangeMap.replace(higherSeverity, new PresidioRange<>(upperBound * minimumDelta, Math.max(higherSeverityRange.getUpperBound(), upperBound * minimumDelta)));
            }
        }

        return severityToScoreRangeMap;
    }

    private EntityScoreToSeverity getExistingEntityScoreToSeverity(String entityType) {
        EntitySeveritiesRangeDocument entitySeveritiesRangeDocument = entitySeveritiesRangeRepository.findOne(EntitySeveritiesRangeDocument.getEntitySeveritiesDocIdName(entityType));

        if (entitySeveritiesRangeDocument == null) { //no existing percentiles were found
            logger.debug("No entity score percentile calculation results were found, setting scores thresholds to zero (all entities will get LOW severity (till next daily calculation)");

            Map<EntitySeverity, PresidioRange<Double>> severityToScoreRangeMap = createEmptyMap();
            return new EntityScoreToSeverity(severityToScoreRangeMap);
        }

        return new EntityScoreToSeverity(entitySeveritiesRangeDocument.getSeverityToScoreRangeMap());
    }

    private Map<EntitySeverity, PresidioRange<Double>> createEmptyMap() {
        Map<EntitySeverity, PresidioRange<Double>> severityToScoreRangeMap = new LinkedHashMap<>();
        severityToScoreRangeMap.put(EntitySeverity.LOW, new PresidioRange<>(-1d, -1d));
        severityToScoreRangeMap.put(EntitySeverity.MEDIUM, new PresidioRange<>(-1d, -1d));
        severityToScoreRangeMap.put(EntitySeverity.HIGH, new PresidioRange<>(-1d, -1d));
        severityToScoreRangeMap.put(EntitySeverity.CRITICAL, new PresidioRange<>(-1d, -1d));
        return severityToScoreRangeMap;
    }

    @Override
    public void updateSeverities(String entityType) {
        final EntityScoreToSeverity severitiesMap = getSeveritiesMap(true, entityType);
        EntityQuery.EntityQueryBuilder entityQueryBuilder =
                new EntityQuery.EntityQueryBuilder()
                        .filterByEntitiesTypes(Collections.singletonList(entityType))
                        .pageNumber(0)
                        .pageSize(defaultEntitiesBatchSize)
                        .sort(new Sort(new Sort.Order(Sort.Direction.ASC, Entity.SCORE_FIELD_NAME)));
        Page<Entity> page = entityPersistencyService.find(entityQueryBuilder.build());

        while (page != null && page.hasContent()) {
            logger.info("Updating severity for entity's page: " + page.toString());
            updateEntitySeverities(severitiesMap, page.getContent());
            page = getNextEntityPage(entityQueryBuilder, page);

        }
    }


    /**
     * This function load all entities score and store it in a double array
     * Only for entity scores above 0
     */
    private double[] getScoresArray(String entityType) {
        Sort sort = new Sort(Sort.Direction.ASC, Entity.SCORE_FIELD_NAME);
        EntityQuery.EntityQueryBuilder entityQueryBuilder = new EntityQuery.EntityQueryBuilder()
                .minScore(1)
                .pageNumber(0)
                .filterByEntitiesTypes(Collections.singletonList(entityType))
                .pageSize(this.defaultEntitiesBatchSize).sort(sort);
        Page<Entity> page = entityPersistencyService.find(entityQueryBuilder.build());
        int numberOfElements = new Long(page.getTotalElements()).intValue();
        double[] scores = new double[numberOfElements];
        AtomicInteger courser = new AtomicInteger(0);

        while (page != null && page.hasContent()) {
            page.getContent().forEach(entity -> scores[courser.getAndAdd(1)] = entity.getScore());
            page = getNextEntityPage(entityQueryBuilder, page);

        }
        return scores;
    }

    /**
     * Return the next entity page or null if no next
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

    private void updateEntitySeverities(EntityScoreToSeverity severitiesMap, List<Entity> entities) {
        List<Entity> updatedEntities = new ArrayList<>();
        if (entities == null) {
            return;
        }

        entities.forEach(entity -> {
            double entityScore = entity.getScore();
            EntitySeverity newEntitySeverity = severitiesMap.getEntitySeverity(entityScore);
            logger.debug("Updating entity severity for entityId: " + entity.getEntityId());
            if (!newEntitySeverity.equals(entity.getSeverity())) {
                entity.setSeverity(newEntitySeverity);
                updatedEntities.add(entity);
            }
        });
        if (updatedEntities.size() > 0) {
            entityPersistencyService.save(updatedEntities);
        }
    }

    public static class EntityScoreToSeverity {
        private Map<EntitySeverity, PresidioRange<Double>> entitySeverityRangeMap;

        public EntityScoreToSeverity(Map<EntitySeverity, PresidioRange<Double>> severityToScoreRangeMap) {
            this.entitySeverityRangeMap = severityToScoreRangeMap;
        }

        public EntitySeverity getEntitySeverity(double score) {
            if (entitySeverityRangeMap.get(EntitySeverity.LOW).getUpperBound() == -1) {
                return EntitySeverity.LOW;
            }

            if (score >= entitySeverityRangeMap.get(EntitySeverity.CRITICAL).getLowerBound()) {
                return EntitySeverity.CRITICAL;
            } else if (score >= entitySeverityRangeMap.get(EntitySeverity.HIGH).getLowerBound()) {
                return EntitySeverity.HIGH;
            } else if (score >= entitySeverityRangeMap.get(EntitySeverity.MEDIUM).getLowerBound()) {
                return EntitySeverity.MEDIUM;
            } else {
                return EntitySeverity.LOW;
            }
        }
    }

    @Override
    public List<String> collectionNamesForSchemas(List<Schema> schemas) {
        List<String> collections = new ArrayList<>();
        schemas.forEach(schema -> collections.add(outputToCollectionNameTranslator.toCollectionName(schema)));
        return collections;
    }
}
