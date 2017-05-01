import { promiseRequest } from 'streaming-data/services/data-access/requests';
import FilterQuery from 'respond/utils/filter-query';
import moment from 'moment';

// utility function for constructing a basic/standard incidents query
const _buildQuery = (filters = { created: { name: 'ALL_TIME', unit: 'years', subtract: 50 } }, { sortField, isSortDescending = true }) => {
  const query = FilterQuery.create().addSortBy(sortField, isSortDescending);

  Object.keys(filters).forEach((filterField) => {
    const value = filters[filterField];

    if (filterField === 'created') {
      if ('start' in value) {  // Custom Range Filter
        query.addRangeFilter('created', value.start || 0, value.end || undefined);
      } else { // Common date/time range filter
        query.addRangeFilter('created', moment().subtract(value.subtract, value.unit).valueOf(), undefined);
      }
    } else {
      query.addFilter(filterField, filters[filterField]);
    }
  });

  return query;
};

const RemediationTasksAPI = {
  /**
   * Executes a websocket remediation tasks fetch call and returns a Promise. Arguments include the filters that should be
   * applied against the tasks collection and the sort information for the returned tasks result set.
   *
   * @method getRemediationTasks
   * @public
   * @param filters The filters to apply against the incidents collection
   * @param sort The sorting information ({ id, isDescending }) for the result set
   * @returns {Promise}
   */
  getRemediationTasks(filters, sort) {
    const query = _buildQuery(filters, sort);
    return promiseRequest({
      method: 'query',
      modelName: 'remediation-tasks',
      query: query.toJSON()
    });
  },


  /**
   * Retrieves the total count of remediation tasks for a query. This is separated from the getRemediationTasks() call to improve
   * performance, allowing the first chunk of streamed results to arrive without waiting further for this call
   * @method getRemediationTaskCount
   * @public
   * @param filters
   * @param sort
   * @returns {Promise}
   */
  getRemediationTaskCount(filters, sort) {
    const query = _buildQuery(filters, sort);

    return promiseRequest({
      method: 'queryRecord',
      modelName: 'remediation-tasks-count',
      query: query.toJSON()
    });
  },

  /**
   * @method createRemediationTask
   * @public
   * @param task
   * @returns {*}
   */
  createRemediationTask(task) {
    return promiseRequest({
      method: 'createRecord',
      modelName: 'remediation-tasks',
      query: {
        data: task
      }
    });
  }
};

export default RemediationTasksAPI;