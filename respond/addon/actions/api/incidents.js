import EmberObject from 'ember-object';
import { assert } from 'ember-metal/utils';
import { promiseRequest, streamRequest } from 'streaming-data/services/data-access/requests';
import FilterQuery from 'respond/utils/filter-query';
import moment from 'moment';
import { isEmpty, isPresent, typeOf, isNone } from 'ember-utils';
import { isEmberArray } from 'ember-array/utils';

const IncidentsAPI = EmberObject.extend({});

// NOOP function to replace Ember.K
const NOOP = () => {};

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

IncidentsAPI.reopenClass({
  /**
   * Executes a websocket Incidents fetch call and returns a Promise. Arguments include the filters that should be
   * applied against the incidents collection and the sort information for the returned incidents result set.
   *
   * @method getIncidents
   * @public
   * @param filters The filters to apply against the incidents collection
   * @param sort The sorting information ({ id, isDescending }) for the result set
   * @param {function} onResponse The callback for the onNext/onResponse event (when data retrieved by chunk)
   * @param {function} onError The callback for any error during streaming
   * @returns {Promise}
   */
  getIncidents(filters, sort, { onResponse = NOOP, onError = NOOP, onInit = NOOP, onCompleted = NOOP }) {
    const query = _buildQuery(filters, sort);

    return streamRequest({
      method: 'stream',
      modelName: 'incidents',
      query: query.toJSON(),
      onInit,
      onResponse,
      onError,
      onCompleted
    });
  },

  /**
   * Retrieves the total count of incidents for a query. This is separated from the getIncidents() call to improve
   * performance, allowing the first chunk of streamed results to arrive without waiting further for this call
   * @method getIncidentsCount
   * @public
   * @param filters
   * @param sort
   * @returns {Promise}
   */
  getIncidentsCount(filters, sort) {
    const query = _buildQuery(filters, sort);

    return promiseRequest({
      method: 'queryRecord',
      modelName: 'incidents-count',
      query: query.toJSON()
    });
  },

  /**
   * Executes a websocket Incident fetch call and returns a Promise. Promise should resolve to the profile details/info
   * for the incident ID supplied to the method
   *
   * @method getIncidentDetails
   * @public
   * @param incidentId The ID of the incident to fetch
   * @returns {Promise}
   */
  getIncidentDetails(incidentId) {
    return promiseRequest({
      method: 'queryRecord',
      modelName: 'incidents',
      query: {
        id: null,
        incidentId
      }
    });
  },

  /**
   * Executes websocket request to update an incident record
   * @method updateIncident
   * @public
   * @param entityId
   * @param field
   * @param updatedValue
   * @returns {*}
   */
  updateIncident(entityId, field, updatedValue) {
    const entityIds = isEmberArray(entityId) ? entityId : [entityId];

    return promiseRequest({
      method: 'updateRecord',
      modelName: 'incidents',
      query: {
        entityIds,
        updates: {
          [field]: updatedValue
        }
      }
    });
  },

  /**
   * Executes a websocket request to update an incident's priority
   * @method changeIncidentPriority
   * @public
   * @param incidentId
   * @param newPriorityValue
   * @returns {Promise}
   */
  changeIncidentPriority(incidentId, newPriorityValue) {
    assert('An incident ID is provided', isPresent(incidentId));
    assert('newPriorityValue is provided as a string', typeOf(newPriorityValue) === 'string');
    return IncidentsAPI.updateIncident(incidentId, 'priority', newPriorityValue);
  },

  /**
   * Executes a websocket request to update the priority on multiple incidents simultaneously. Delegates to
   * changeIncidentPriority() which can handle bulk updates.
   * @method bulkChangeIncidentPriority
   * @public
   * @param incidentIds An array of incident IDs
   * @param newPriorityValue
   * @returns {Promise}
   */
  bulkChangeIncidentPriority(incidentIds, newPriorityValue) {
    assert('bulkChangeIncidentPriority is provided array of incident IDs', isEmberArray(incidentIds) && !isEmpty(incidentIds));
    return IncidentsAPI.changeIncidentPriority(incidentIds, newPriorityValue);
  },

  /**
   * Executes a websocket request to update the status on a single incident
   * @method changeIncidentStatus
   * @public
   * @param incidentId
   * @param newStatusValue
   * @returns {*}
   */
  changeIncidentStatus(incidentId, newStatusValue) {
    assert('An incident ID is provided', isPresent(incidentId));
    assert('newStatusValue is provided as a string', typeOf(newStatusValue) === 'string');
    return IncidentsAPI.updateIncident(incidentId, 'status', newStatusValue);
  },

  /**
   * Executes a websocket request to update the status on multiple incidents simultaneously. Delegates to
   * changeIncidentStatus() which can handle bulk updates.
   * @method bulkChangeIncidentStatus
   * @public
   * @param incidentIds An array of incident IDs
   * @param newStatusValue
   * @returns {Promise}
   */
  bulkChangeIncidentStatus(incidentIds, newStatusValue) {
    assert('bulkChangeIncidentStatus is provided array of incident IDs', isEmberArray(incidentIds) && !isEmpty(incidentIds));
    return IncidentsAPI.changeIncidentStatus(incidentIds, newStatusValue);
  },

  /**
   * Executes a websocket request to update the assignee on a single incident
   * @method changeIncidentAssignee
   * @public
   * @param incidentId
   * @param newAssignee {Object} Object with four properties (id, firstName, lastName, email)
   * @returns {Promise}
   */
  changeIncidentAssignee(incidentId, newAssignee) {
    assert('An incident ID is provided', isPresent(incidentId));
    assert('newAssignee is provided as an object', typeOf(newAssignee) === 'object');
    assert('newAssignee object has all required properties', !isNone(newAssignee.id) &&
      !isNone(newAssignee.name));
    return IncidentsAPI.updateIncident(incidentId, 'assignee', newAssignee);
  },

  /**
   * Executes a websocket request to update the assignee on multiple incidents simultaneously. Delegates to
   * changeIncidentAssignee() which can handle bulk updates.
   * @method bulkChangeIncidentAssignee
   * @public
   * @param incidentIds An array of incident IDs
   * @param newAssignee {Object} Object with four properties (id, firstName, lastName, email)
   * @returns {Promise}
   */
  bulkChangeIncidentAssignee(incidentIds, newAssignee) {
    assert('bulkChangeIncidentAssignee is provided array of incident IDs', isEmberArray(incidentIds) && !isEmpty(incidentIds));
    return IncidentsAPI.changeIncidentAssignee(incidentIds, newAssignee);
  },

  /**
   * Executes a websocket storyline fetch call and returns a Promise. Promise should resolve to the storyline details/info
   * for the incident ID supplied to the method
   *
   * The server returns a `data` object which has only a single property `relatedIndicators[]`, which is the actual
   * storyline array we want. So we flatten/discard the top-level object here and re-assign `data` to the array directly.
   *
   * Also, each member of that `relatedIndicators[]` is an object with several properties, including `indicator`.
   * Each `indicator` property points to an ugly JSON object that we wrap in a util class here in order to give it a
   * friendly access API downstream.
   *
   * @method getStoryline
   * @public
   * @param incidentId The ID of the incident
   * @returns {Promise}
   */
  getStoryline(incidentId) {
    const query = FilterQuery.create()
      .addSortBy('event.timestamp', false)  // TODO: ask backend if 'indicator.timestamp' is supported
      .addFilter('_id', incidentId);

    return promiseRequest({
      method: 'queryRecord',
      modelName: 'storyline',
      query: query.toJSON()
    });
  },

  /**
   * Executes a websocket delete incident call and returns a Promise
   * @method deleteIncident
   * @public
   * @param incidentId The id of the incident to delete
   * @returns {Promise}
   */
  delete(incidentId) {
    const query = FilterQuery.create()
      .addFilter('_id', incidentId);

    return promiseRequest({
      method: 'deleteRecord',
      modelName: 'incidents',
      query: query.toJSON()
    });
  }
});

export default IncidentsAPI;