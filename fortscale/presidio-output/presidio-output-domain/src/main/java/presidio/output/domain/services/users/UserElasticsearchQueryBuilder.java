package presidio.output.domain.services.users;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.ObjectUtils;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.ElasticsearchQueryBuilder;

import static org.elasticsearch.index.query.QueryBuilders.*;

public class UserElasticsearchQueryBuilder extends ElasticsearchQueryBuilder<UserQuery> {

    public UserElasticsearchQueryBuilder(UserQuery userQuery) {
        super(userQuery);
    }

    public void withFilter(UserQuery userQuery) {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // filter by username
        if (StringUtils.isNotEmpty(userQuery.getFilterByUserName())) {
            if (userQuery.isPrefix()) {
                boolQueryBuilder.must(prefixQuery(User.USER_NAME_FIELD_NAME, userQuery.getFilterByUserName()));
            } else {
                boolQueryBuilder.must(matchQuery(User.USER_NAME_FIELD_NAME, userQuery.getFilterByUserName()).operator(Operator.AND));
            }
        }

        // filter by alert classifications
        if (CollectionUtils.isNotEmpty(userQuery.getFilterByAlertClassifications())) {
            BoolQueryBuilder classificationQuery = new BoolQueryBuilder();
            for (String classification : userQuery.getFilterByAlertClassifications()) {
                classificationQuery.should(matchQuery(User.ALERT_CLASSIFICATIONS_FIELD_NAME, classification).operator(Operator.OR));
            }
            boolQueryBuilder.must(classificationQuery);
        }

        // filter by alert indicators
        if (CollectionUtils.isNotEmpty(userQuery.getFilterByIndicators())) {
            BoolQueryBuilder indicatorsQuery = new BoolQueryBuilder();
            for (String indicator : userQuery.getFilterByIndicators()) {
                indicatorsQuery.should(matchQuery(User.INDICATORS_FIELD_NAME, indicator).operator(Operator.OR));
            }
            boolQueryBuilder.must(indicatorsQuery);
        }

        // filter by userIds
        if (CollectionUtils.isNotEmpty(userQuery.getFilterByUsersIds())) {
            BoolQueryBuilder userIdQuery = new BoolQueryBuilder();
            for (String id : userQuery.getFilterByUsersIds()) {
                userIdQuery.should(matchQuery(User.USER_ID_FIELD_NAME, id).operator(Operator.OR));
            }
            boolQueryBuilder.must(userIdQuery);
        }

        // filter by user severity
        if (CollectionUtils.isNotEmpty(userQuery.getFilterBySeverities())) {
            BoolQueryBuilder severityQuery = new BoolQueryBuilder();
            for (UserSeverity severity : userQuery.getFilterBySeverities()) {
                severityQuery.should(matchQuery(User.SEVERITY_FIELD_NAME, severity.name()).operator(Operator.OR));
            }
            boolQueryBuilder.must(severityQuery);
        }

        // filter by tags
        if (CollectionUtils.isNotEmpty(userQuery.getFilterByUserTags())) {
            BoolQueryBuilder tagsQuery = new BoolQueryBuilder();
            for (String tag : userQuery.getFilterByUserTags()) {
                tagsQuery.should(matchQuery(User.TAGS_FIELD_NAME, tag).operator(Operator.OR));
            }
            boolQueryBuilder.must(tagsQuery);
        }

        // filter by min or max score
        if (userQuery.getMinScore() > 0 || userQuery.getMaxScore() > 0) {
            RangeQueryBuilder rangeQuery = rangeQuery(User.SCORE_FIELD_NAME);
            if (userQuery.getMinScore() > 0) {
                rangeQuery.gte(userQuery.getMinScore());
            }
            if (userQuery.getMaxScore() > 0) {
                rangeQuery.lte(userQuery.getMaxScore());
            }

            boolQueryBuilder.must(rangeQuery);
        }

        if (boolQueryBuilder.hasClauses()) {
            super.withFilter(boolQueryBuilder);
        }
    }

    /**
     * Add all sort fields
     *
     * @param userQuery
     */
    public void withSort(UserQuery userQuery) {
        if (userQuery.getSort() != null) {
            userQuery.getSort().forEach(order -> {
                FieldSortBuilder sortBuilder = new FieldSortBuilder(order.getProperty());
                SortOrder direction = order.getDirection().name().equals(SortOrder.ASC.name()) ? SortOrder.ASC : SortOrder.DESC;
                sortBuilder.order(direction);
                super.withSort(sortBuilder);
            });
        }
    }

    public void withPageable(UserQuery userQuery) {
        if (userQuery.getPageNumber() >= 0 && userQuery.getPageSize() > 0) {
            PageRequest pageRequest = new PageRequest(userQuery.getPageNumber(), userQuery.getPageSize());
            super.withPageable(pageRequest);
        }
    }

    @Override
    public void addAggregation(UserQuery userQuery) {
        if (CollectionUtils.isNotEmpty(userQuery.getAggregateByFields())) {
            if (userQuery.getAggregateByFields().contains(User.SEVERITY_FIELD_NAME)) {
                super.addAggregation(AggregationBuilders.terms(User.SEVERITY_FIELD_NAME).field(User.SEVERITY_FIELD_NAME));
            }
            if (userQuery.getAggregateByFields().contains(User.TAGS_FIELD_NAME)) {
                super.addAggregation(AggregationBuilders.terms(User.TAGS_FIELD_NAME).field(User.TAGS_FIELD_NAME));
            }
            if (userQuery.getAggregateByFields().contains(User.ALERT_CLASSIFICATIONS_FIELD_NAME)) {
                super.addAggregation(AggregationBuilders.terms(User.ALERT_CLASSIFICATIONS_FIELD_NAME).field(User.ALERT_CLASSIFICATIONS_FIELD_NAME));
            }
        }
    }
}
