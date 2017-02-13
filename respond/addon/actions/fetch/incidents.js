import { promiseRequest } from 'streaming-data/services/data-access/requests';
import { CANNED_FILTER_TYPES_BY_NAME } from 'respond/utils/canned-filter-types';
import { SORT_TYPES_BY_NAME } from 'respond/utils/sort-types';
import FilterQuery from 'respond/utils/filter-query';

/**
 * Executes a websocket Incidents fetch call and returns a Promise. Arguments include the filters that should be
 * applied against the incidents collection and the sort information for the returned incidents result set.
 *
 * @method fetchIncidents
 * @public
 * @param filters The filters to apply against the incidents collection
 * @param sort The sorting information ({ id, isDescending}) for the result set
 * @returns {Promise}
 */
function fetchIncidents(filters, sort) {
  const {
    field: cannedFilterField,
    value: cannedFilterValue } = CANNED_FILTER_TYPES_BY_NAME[filters.cannedFilter].filter;

  const {
    sortField,
    isDescending } = SORT_TYPES_BY_NAME[sort];

  const query = FilterQuery.create()
    .addSortBy(sortField, isDescending)
    .addFilter(cannedFilterField, cannedFilterValue)
    .addRangeFilter('created', 0, undefined);

  const request = {
    method: 'query',
    modelName: 'incidents',
    query: query.toJSON()
  };

  return promiseRequest(request);
}

export default fetchIncidents;