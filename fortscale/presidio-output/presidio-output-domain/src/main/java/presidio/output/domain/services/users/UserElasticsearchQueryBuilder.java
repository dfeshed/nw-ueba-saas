package presidio.output.domain.services.users;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.services.ElasticsearchQueryBuilder;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

public class UserElasticsearchQueryBuilder extends ElasticsearchQueryBuilder<UserQuery> {

    public UserElasticsearchQueryBuilder(UserQuery userQuery) {
        super(userQuery);
    }

    public void withFilter(UserQuery userQuery) {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // filter by alert classifications
        if (! userQuery.getFilterByAlertClassifications().isEmpty()) {
            boolQueryBuilder.must(matchQuery(User.ALERT_CLASSIFICATOINS_FIELD_NAME, userQuery.getFilterByAlertClassifications()));
        }

        if (boolQueryBuilder.hasClauses()) {
            super.withFilter(boolQueryBuilder);
        }
    }

    public void withSort(UserQuery userQuery) {
        if (StringUtils.isNotEmpty(userQuery.getSortField())) {
            FieldSortBuilder sortBuilder = new FieldSortBuilder(userQuery.getSortField());
            SortOrder order = userQuery.isAscendingOrder()? SortOrder.ASC: SortOrder.DESC;
            sortBuilder.order(order);
            super.withSort(sortBuilder);
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
