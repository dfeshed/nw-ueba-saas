package presidio.output.domain.services.users;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.ObjectUtils;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.ElasticsearchQueryBuilder;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

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
        if (!CollectionUtils.isEmpty(userQuery.getFilterByAlertClassifications())) {
            for (String classification : userQuery.getFilterByAlertClassifications()) {
                boolQueryBuilder.should(matchQuery(User.ALERT_CLASSIFICATOINS_FIELD_NAME, classification).operator(Operator.OR));
            }
        }

        // filter by userIds
        if (!CollectionUtils.isEmpty(userQuery.getFilterByUsersIds())) {
            for (String id : userQuery.getFilterByUsersIds()) {
                boolQueryBuilder.should(matchQuery(User.USER_ID_FIELD_NAME, id).operator(Operator.OR));
            }
        }

        // filter by user severitie
        if (!CollectionUtils.isEmpty(userQuery.getFilterBySeverities())) {
            for (UserSeverity severity : userQuery.getFilterBySeverities()) {
                boolQueryBuilder.should(matchQuery(User.USER_SEVERITY_FIELD_NAME, severity.name()).operator(Operator.OR));
            }
        }

        // filter by isAdmin
        if (userQuery.getFilterByIsAdmin() != null && userQuery.getFilterByIsAdmin()) {
            boolQueryBuilder.must(matchQuery(User.IS_ADMIN_FIELD_NAME, userQuery.getFilterByIsAdmin()).operator(Operator.AND));
        }


        if (userQuery.getMinScore() != null || userQuery.getMaxScore() != null) {
            RangeQueryBuilder rangeQuery = rangeQuery(User.SCORE_FIELD_NAME);
            if (userQuery.getMinScore() != null) {
                rangeQuery.gte(userQuery.getMinScore());
            }
            if (userQuery.getMaxScore() != null) {
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
        if (!ObjectUtils.isEmpty(userQuery.getSort())) {

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
    public void addAggregation(UserQuery query) {
        //TODO
    }

}
