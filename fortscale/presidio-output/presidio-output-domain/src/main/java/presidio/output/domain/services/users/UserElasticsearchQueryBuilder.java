package presidio.output.domain.services.users;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

public class UserElasticsearchQueryBuilder extends NativeSearchQueryBuilder {

    public UserElasticsearchQueryBuilder(UserQuery userQuery) {

        // filters
        withFilter(userQuery);

        // sort
        withSort(userQuery);

        // paging
        withPageable(userQuery);
    }

    private void withFilter(UserQuery userQuery) {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // filter by alert classifications
        if (! userQuery.getFilterByAlertClassifications().isEmpty()) {
            boolQueryBuilder.must(matchQuery(User.ALERT_CLASSIFICATOINS_FIELD_NAME, userQuery.getFilterByAlertClassifications()));
        }

        if (boolQueryBuilder.hasClauses()) {
            super.withFilter(boolQueryBuilder);
        }
    }

    private void withSort(UserQuery userQuery) {
        if (StringUtils.isNotEmpty(userQuery.getSortField())) {
            FieldSortBuilder sortBuilder = new FieldSortBuilder(userQuery.getSortField());
            SortOrder order = userQuery.isAscendingOrder()? SortOrder.ASC: SortOrder.DESC;
            sortBuilder.order(order);
            super.withSort(sortBuilder);
        }
    }

    private void withPageable(UserQuery userQuery) {
        if (userQuery.getPageNumber() > -1 || userQuery.getPageSize() > -1) {
            PageRequest pageRequest = new PageRequest(userQuery.getPageNumber(), userQuery.getPageSize());
            super.withPageable(pageRequest);
        }
    }

}
