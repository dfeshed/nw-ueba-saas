package presidio.output.domain.services.entities;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntityQuery;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.services.ElasticsearchQueryBuilder;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;

public class EntityElasticsearchQueryBuilder extends ElasticsearchQueryBuilder<EntityQuery>  {
    public static final int DEFAULT_AGG_BULK_SIZE = 1000;

    public EntityElasticsearchQueryBuilder(EntityQuery entityQuery) {
        super(entityQuery);
    }

    public void withFilter(EntityQuery entityQuery) {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        final BoolQueryBuilder boolQueryBuilder2 = new BoolQueryBuilder();

        // filter by entityName
        if (StringUtils.isNotEmpty(entityQuery.getFilterByEntityName())) {
            if (entityQuery.isPrefix()) {
                boolQueryBuilder.must(prefixQuery(Entity.ENTITY_NAME_FIELD_NAME, entityQuery.getFilterByEntityName()));
            } else {
                boolQueryBuilder.must(matchQuery(Entity.ENTITY_NAME_FIELD_NAME, entityQuery.getFilterByEntityName()).operator(Operator.AND));
            }
        }


        // filter by freeText
        if (StringUtils.isNotEmpty(entityQuery.getFilterByFreeText())) {
            BoolQueryBuilder freeTextQuery = new BoolQueryBuilder();
            if (entityQuery.isPrefix()) {
                freeTextQuery.should(prefixQuery(Entity.ENTITY_NAME_FIELD_NAME, entityQuery.getFilterByFreeText()));
            } else {
                freeTextQuery.should(matchQuery(Entity.ENTITY_NAME_FIELD_NAME, entityQuery.getFilterByFreeText()));
            }

            boolQueryBuilder.must(freeTextQuery);
        }

        // filter by alert classifications
        if (CollectionUtils.isNotEmpty(entityQuery.getFilterByAlertClassifications())) {
            BoolQueryBuilder classificationQuery = new BoolQueryBuilder();
            for (String classification : entityQuery.getFilterByAlertClassifications()) {
                classificationQuery.should(matchQuery(Entity.ALERT_CLASSIFICATIONS_FIELD_NAME, classification).operator(Operator.OR));
            }
            boolQueryBuilder.must(classificationQuery);
        }

        // filter by alert indicators
        if (CollectionUtils.isNotEmpty(entityQuery.getFilterByIndicators())) {
            BoolQueryBuilder indicatorsQuery = new BoolQueryBuilder();
            for (String indicator : entityQuery.getFilterByIndicators()) {
                indicatorsQuery.should(matchQuery(Entity.INDICATORS_FIELD_NAME, indicator).operator(Operator.OR));
            }
            boolQueryBuilder.must(indicatorsQuery);
        }

        // filter by entitiesIds
        if (CollectionUtils.isNotEmpty(entityQuery.getFilterByEntitiesIds())) {
            BoolQueryBuilder entityIdQuery = new BoolQueryBuilder();
            for (String id : entityQuery.getFilterByEntitiesIds()) {
                entityIdQuery.should(matchQuery(Entity.ENTITY_ID_FIELD_NAME, id).operator(Operator.OR));
            }
            boolQueryBuilder.must(entityIdQuery);
        }

        // filter by entityTypes
        if (CollectionUtils.isNotEmpty(entityQuery.getFilterByEntitiesTypes())) {
            BoolQueryBuilder entityTypeQuery = new BoolQueryBuilder();
            for (String entityType : entityQuery.getFilterByEntitiesTypes()) {
                entityTypeQuery.should(matchQuery(Entity.ENTITY_TYPE_FIELD_NAME, entityType).operator(Operator.OR));
            }
            boolQueryBuilder.must(entityTypeQuery);
        }

        // filter by entity severity
        if (CollectionUtils.isNotEmpty(entityQuery.getFilterBySeverities())) {
            BoolQueryBuilder severityQuery = new BoolQueryBuilder();
            for (EntitySeverity severity : entityQuery.getFilterBySeverities()) {
                severityQuery.should(matchQuery(Entity.SEVERITY_FIELD_NAME, severity.name()).operator(Operator.OR));
            }
            boolQueryBuilder.must(severityQuery);
        }

        // filter by tags
        if (CollectionUtils.isNotEmpty(entityQuery.getFilterByEntityTags())) {
            BoolQueryBuilder tagsQuery = new BoolQueryBuilder();
            for (String tag : entityQuery.getFilterByEntityTags()) {
                tagsQuery.should(matchQuery(Entity.TAGS_FIELD_NAME, tag).operator(Operator.OR));
            }
            boolQueryBuilder.must(tagsQuery);
        }

        // filter by min or max score
        if (entityQuery.getMinScore() > 0 || entityQuery.getMaxScore() > 0) {
            RangeQueryBuilder rangeQuery = rangeQuery(Entity.SCORE_FIELD_NAME);
            if (entityQuery.getMinScore() > -1)
                rangeQuery.gte(entityQuery.getMinScore());
            if (entityQuery.getMaxScore() > -1)
                rangeQuery.lte(entityQuery.getMaxScore());
            boolQueryBuilder.must(rangeQuery);
        }

        boolQueryBuilder2.filter(boolQueryBuilder);
        if (boolQueryBuilder.hasClauses()) {
            super.withQuery(boolQueryBuilder2);
        }
    }

    /**
     * Add all sort fields
     *
     * @param entityQuery
     */
    public void withSort(EntityQuery entityQuery) {
        if (entityQuery.getSort() != null) {
            entityQuery.getSort().forEach(order -> {
                FieldSortBuilder sortBuilder = new FieldSortBuilder(order.getProperty());
                SortOrder direction = order.getDirection().name().equals(SortOrder.ASC.name()) ? SortOrder.ASC : SortOrder.DESC;
                sortBuilder.order(direction);
                super.withSort(sortBuilder);
            });
        }
    }

    public void withPageable(EntityQuery entityQuery) {
        if (entityQuery.getPageNumber() >= 0 && entityQuery.getPageSize() > 0) {
            PageRequest pageRequest = new PageRequest(entityQuery.getPageNumber(), entityQuery.getPageSize());
            super.withPageable(pageRequest);
        }
    }

    @Override
    public void addAggregation(EntityQuery entityQuery) {
        List<String> aggregateByFields = entityQuery.getAggregateByFields();
        if (CollectionUtils.isNotEmpty(aggregateByFields)) {
            if (aggregateByFields.contains(Entity.SEVERITY_FIELD_NAME)) {
                super.addAggregation(AggregationBuilders.terms(Entity.SEVERITY_FIELD_NAME).field(Entity.SEVERITY_FIELD_NAME));
            }
            if (aggregateByFields.contains(Entity.TAGS_FIELD_NAME)) {
                super.addAggregation(AggregationBuilders.terms(Entity.TAGS_FIELD_NAME).field(Entity.TAGS_FIELD_NAME).size(DEFAULT_AGG_BULK_SIZE));
            }
            if (aggregateByFields.contains(Entity.ALERT_CLASSIFICATIONS_FIELD_NAME)) {
                super.addAggregation(AggregationBuilders.terms(Entity.ALERT_CLASSIFICATIONS_FIELD_NAME).field(Entity.ALERT_CLASSIFICATIONS_FIELD_NAME).size(DEFAULT_AGG_BULK_SIZE));
            }
            if (aggregateByFields.contains(Entity.INDICATORS_FIELD_NAME)) {
                super.addAggregation(AggregationBuilders.terms(Entity.INDICATORS_FIELD_NAME).field(Entity.INDICATORS_FIELD_NAME).size(DEFAULT_AGG_BULK_SIZE));
            }
        }
    }
}
