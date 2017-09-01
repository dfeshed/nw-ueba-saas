package presidio.output.domain.services.users;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.services.ElasticsearchQueryBuilder;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;

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

        // filter by userId
        if (StringUtils.isNotEmpty(userQuery.getFilterByUserId())) {
            boolQueryBuilder.must(matchQuery(User.USER_ID_FIELD_NAME, userQuery.getFilterByUserId()).operator(Operator.AND));
        }

        // filter by alert classifications
        if (userQuery.getFilterByAlertClassifications() != null && !(userQuery.getFilterByAlertClassifications()).isEmpty()) {
            for (String classification : userQuery.getFilterByAlertClassifications()) {
                boolQueryBuilder.should(matchQuery(User.ALERT_CLASSIFICATOINS_FIELD_NAME, classification).operator(Operator.OR));
            }
        }

        if (boolQueryBuilder.hasClauses()) {
            super.withFilter(boolQueryBuilder);
        }

        if (userQuery.getMinScore() != null || userQuery.getMaxScore() != null){
            RangeQueryBuilder rangeQuery = rangeQuery(User.SCORE_FIELD_NAME);
            if (userQuery.getMinScore()>0) {
                rangeQuery.gte(userQuery.getMinScore());
            }
            if (userQuery.getMaxScore()>0) {
                rangeQuery.lte(userQuery.getMaxScore());
            }

            boolQueryBuilder.must(rangeQuery);
        }
    }

    /**
     * Add all sort fields
     * @param userQuery
     */
    public void withSort(UserQuery userQuery) {
        if (userQuery.getSort()!=null) {

            userQuery.getSort().forEach(order->{
                FieldSortBuilder sortBuilder = new FieldSortBuilder(order.getProperty());
                SortOrder direction = order.getDirection().name().equals(SortOrder.ASC.name())? SortOrder.ASC: SortOrder.DESC;
                sortBuilder.order(direction);
                super.withSort(sortBuilder);
            });

        }
    }

    public void withPageable(UserQuery userQuery) {
        if (userQuery.getPageNumber() > -1 || userQuery.getPageSize() > -1) {
            PageRequest pageRequest = new PageRequest(userQuery.getPageNumber(), userQuery.getPageSize());
            super.withPageable(pageRequest);
        }
    }

    @Override
    public void addAggregation(UserQuery query) {
        //TODO
    }

}
