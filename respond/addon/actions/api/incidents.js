import EmberObject from 'ember-object';
import { assert } from 'ember-metal/utils';
import { promiseRequest, streamRequest } from 'streaming-data/services/data-access/requests';
import FilterQuery from 'respond/utils/filter-query';
import { isEmpty, isPresent, typeOf, isNone } from 'ember-utils';
import { isEmberArray } from 'ember-array/utils';
import buildExplorerQuery from './util/explorer-build-query';

const IncidentsAPI = EmberObject.extend({});

// NOOP function to replace Ember.K
const NOOP = () => {};

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
    const query = buildExplorerQuery(filters, sort, 'created');

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
    const query = buildExplorerQuery(filters, sort, 'created');

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
   * Executes a websocket alerts fetch call and returns a Promise. Promise should resolve to the alerts
   * for the incident ID supplied to the method
   *
   * @method getAlertsForIncident
   * @public
   * @param incidentId The ID of the incident
   * @returns {Promise}
   */
  getAlertsForIncident(incidentId) {
    const query = FilterQuery.create()
      .addFilter('incidentId', incidentId);

    return promiseRequest({
      method: 'query',
      modelName: 'alerts',
      query: query.toJSON()
    });
  },

  /**
   * Composites the `getStoryline` and `getAlertsForIncident` calls together in order to support storylines
   * for incidents migrated from 10.x (which don't have storylines).
   *
   * First executes `getStoryline` and checks for empty results. If empty, then executes `getAlertsForIncident` and
   * massages that response into a structure that mimicks the `getStoryline` responses.
   *
   * @method getAlertsForIncident
   * @param incidentId The ID of the incident
   * @returns {Promise}
   * @public
   */
  getStorylineSafely(incidentId) {
    return IncidentsAPI.getStoryline(incidentId)
      .then(function(response) {

        // Did we get an empty response (no indicators)?
        const isStorylineEmpty = !response || !response.data ||
          !response.data.relatedIndicators ||
          !response.data.relatedIndicators.length;

        if (!isStorylineEmpty) {
          return response;  // Not empty, good to go!
        }

        // Response was empty. Maybe this is an incident migrated for 10.6, whose data doesn't support storyline.
        // Try fetching the alerts for the incident using the standard `alerts` socket API instead.
        return IncidentsAPI.getAlertsForIncident(incidentId)
          .then(function({ data }) {

            // We've got the alerts list. Wrap into a storyline-like JSON structure.
            const storyline = {
              relatedIndicators: []
            };
            if (isEmberArray(data)) {
              storyline.relatedIndicators = data.map((alert) => ({
                indicator: alert,
                group: '0'
              }));
            }
            return { code: 0, data: storyline };
          });
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