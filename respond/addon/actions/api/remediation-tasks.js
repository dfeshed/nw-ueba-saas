import { promiseRequest } from 'streaming-data/services/data-access/requests';
import FilterQuery from 'respond/utils/filter-query';
import buildExplorerQuery from './util/explorer-build-query';
import chunk from 'respond/utils/array/chunk';
import RSVP from 'rsvp';

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
    const query = buildExplorerQuery(filters, sort, 'created');
    return promiseRequest({
      method: 'query',
      modelName: 'remediation-tasks',
      query: query.toJSON()
    });
  },

  getRemediationTasksForIncident(incidentId) {
    const query = buildExplorerQuery({ incidentId }, { sortField: 'created', isSortDescending: true });
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
    const query = buildExplorerQuery(filters, sort, 'created');

    return promiseRequest({
      method: 'queryRecord',
      modelName: 'remediation-tasks-count',
      query: query.toJSON()
    });
  },

  /**
   * Updates one property of the Remediation Task record on one or more remediation tasks
   * @method updateRemediationTask
   * @public
   * @param entityId {string} - The ID of the remediation task to update
   * @param field {string} - The name of the field on the record (e.g., 'priority' or 'status') to update
   * @param updatedValue {*} - The value to be set/updated on the record's field
   */
  updateRemediationTask(entityId, field, updatedValue) {
    const entityIdChunks = chunk(entityId, 500);

    const requests = entityIdChunks.map((chunk) => {
      return promiseRequest({
        method: 'updateRecord',
        modelName: 'remediation-tasks',
        query: {
          entityIds: chunk,
          updates: {
            [field]: updatedValue
          }
        }
      });
    });

    return RSVP.allSettled(requests);
  },

  /**
   * Creates a remediation task
   * @method createRemediationTask
   * @public
   * @param task {Object} - The task object
   * @param task.incidentId {string} - The ID of the incident to which this remediation task should be added
   * @param task.name {string} - The name of the remediation task
   * @param task.priority {string} - The priority for the remediation task (LOW, MEDIUM, HIGH, CRITICAL)
   * @param task.status {string} - The status for the remediation task (NEW, ASSIGNED, IN_PROGRESS, REMEDIATED, RISK_ACCEPTED, NOT_APPLICABLE)
   * @param task.targetQueue {string} - The target queue for the task (OPERATIONS, GRC, CONTENT_IMPROVEMENT)
   * @param task.remediationType {string} - The remediationType options depend on the target queue selected
   * @param task.assignee {string} - Individual assigned to work on the remediation
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
  },

  /**
   * Deletes a remediation task
   * @method deleteRemediationTask
   * @param taskId {string} - The ID of the remediation task to delete
   * @returns {*}
   * @public
   */
  deleteRemediationTask(taskId) {
    const taskIdChunks = chunk(taskId, 500);
    const requests = taskIdChunks.map((chunk) => {
      const query = FilterQuery.create().addFilter('_id', chunk);
      return promiseRequest({
        method: 'deleteRecord',
        modelName: 'remediation-tasks',
        query: query.toJSON()
      });
    });

    return RSVP.allSettled(requests);
  }
};


export default RemediationTasksAPI;