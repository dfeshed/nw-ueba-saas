package presidio.output.domain.repositories;

import org.springframework.data.elasticsearch.core.query.SearchQuery;

public interface AlertRepositoryCustom {

    /**
     * Update alerts by query
     * @param searchQuery query to update only alerts that match the query
     * @param field field name to update
     * @param value field value to update
     * @return
     */
    boolean updateAlertsByQuery(SearchQuery searchQuery, String field, Object value);
}
