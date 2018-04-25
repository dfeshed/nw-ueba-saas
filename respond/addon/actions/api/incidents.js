import { resolveSinceWhenStartTime } from 'respond/utils/since-when-types';
import FilterQuery from 'respond/utils/filter-query';
import chunk from 'respond/utils/array/chunk';
import RSVP from 'rsvp';
import buildExplorerQuery from './util/explorer-build-query';
import { lookup } from 'ember-dependency-lookup';

// NOOP function to replace Ember.K
const NOOP = () => {};

export default {
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
    const request = lookup('service:request');
    const query = buildExplorerQuery(filters, sort, 'created');
    const streamOptions = { cancelPreviouslyExecuting: true };

    return request.streamRequest({
      method: 'stream',
      modelName: 'incidents',
      query: query.toJSON(),
      streamOptions,
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
    const request = lookup('service:request');
    const query = buildExplorerQuery(filters, sort, 'created');

    return request.promiseRequest({
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
    const request = lookup('service:request');
    return request.promiseRequest({
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
    const request = lookup('service:request');
    const entityIdChunks = chunk(entityId, 500);

    const requests = entityIdChunks.map((chunk) => {
      return request.promiseRequest({
        method: 'updateRecord',
        modelName: 'incidents',
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
   * Executes a websocket alerts fetch call and returns a Promise. Promise should resolve to the alerts
   * for the incident ID supplied to the method
   *
   * @method getAlertsForIncident
   * @public
   * @param incidentId The ID of the incident
   * @param {{ onResponse: Function, onError: Function, onInit: Function, onCompleted: Function }} callbacks
   * @returns {Promise}
   */
  getAlertsForIncident(incidentId, { onResponse = NOOP, onError = NOOP, onInit = NOOP, onCompleted = NOOP }) {
    const request = lookup('service:request');
    const streamOptions = { cancelPreviouslyExecuting: true };
    const query = FilterQuery.create()
      .addSortBy('receivedTime', false)
      .addFilter('incidentId', incidentId);

    return request.streamRequest({
      method: 'stream',
      modelName: 'alerts',
      query: query.toJSON(),
      streamOptions,
      onInit,
      onResponse,
      onError,
      onCompleted
    });
  },

  /**
   * Executes a websocket alerts stream call to find alerts that mention a given entity in a given time frame.
   * @param {String} entityType Either 'IP', 'MAC_ADDRESS', 'HOST', 'DOMAIN', 'FILE_NAME' or 'FILE_HASH'
   * @param {String} entityId ID of an entity; e.g. '10.20.30.40', 'HOST1', 'g00gle.com', 'john_smith', 'setup.exe'
   * @param {String} sinceWhen Name of a canned time range object.
   * @param {{ onResponse: Function, onError: Function, onInit: Function, onCompleted: Function }} callbacks
   * @public
   */
  getRelatedAlerts(entityType, entityId, sinceWhen, { onResponse = NOOP, onError = NOOP, onInit = NOOP, onCompleted = NOOP }) {
    const request = lookup('service:request');
    const streamOptions = { cancelPreviouslyExecuting: true };
    const lower = resolveSinceWhenStartTime(sinceWhen);
    const query = {
      criteria: {
        entityType,
        data: entityId,
        timeRange: {
          lower
        }
      },
      limit: 1000,
      chunkSize: 100
    };

    return request.streamRequest({
      method: 'stream',
      modelName: 'related-alerts-search',
      query,
      streamOptions,
      onInit,
      onResponse,
      onError,
      onCompleted
    });
  },

  /**
   * Executes a websocket promise request to add a given list of alerts to a given incident ID.
   * @param {[]} alertIds The alertIds for the alerts to be added to the incident.
   * @param {string} incidentId The ID of the incident to be added to.
   * @returns {Promise}
   * @public
   */
  addAlertsToIncident(alertIds, incidentId) {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'updateRecord',
      modelName: 'alerts-associated',
      query: {
        data: {
          // entity = POJO with incident ID
          entity: {
            id: incidentId
          },
          associated: alertIds
        }
      }
    });
  },

  /**
   * Executes a websocket delete incident call and returns a Promise
   * @method delete
   * @public
   * @param incidentId The id of the incident to delete (or an array of ids)
   * @returns {Promise}
   */
  delete(incidentId) {
    const request = lookup('service:request');
    const incidentIdChunks = chunk(incidentId, 500);
    const requests = incidentIdChunks.map((chunk) => {
      const query = FilterQuery.create().addFilter('_id', chunk);
      return request.promiseRequest({
        method: 'deleteRecord',
        modelName: 'incidents',
        query: query.toJSON()
      });
    });

    return RSVP.allSettled(requests);
  },

  /**
   * Escalates an incident
   * @method escalate
   * @public
   * @param incidentId
   */
  escalate(incidentId) {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'escalate',
      modelName: 'incidents',
      query: {
        incidentId
      }
    });
  },

  /**
   * Returns incidents related system settings
   * @method getIncidentsSettings
   * @public
   * @returns {*}
   */
  getIncidentsSettings() {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'findAll',
      modelName: 'incidents-settings',
      query: {}
    });
  },

  createIncidentFromAlerts(incidentDetails, alertIds) {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'createRecord',
      modelName: 'incidents',
      query: {
        data: {
          entity: incidentDetails,
          associated: alertIds
        }
      }
    });
  },

  /**
   * Searches incident name and incident ID fields
   * @param searchText
   * @param sortField
   * @param sortDescending
   * @param {{ onResponse: Function, onError: Function, onInit: Function, onCompleted: Function }} callbacks
   * @public
   * @returns {*}
   */
  search(searchText, sortField = 'created', sortDescending = true, { onResponse = NOOP, onError = NOOP, onInit = NOOP, onCompleted = NOOP }) {
    const request = lookup('service:request');
    const streamOptions = { cancelPreviouslyExecuting: true };

    return request.streamRequest({
      method: 'stream',
      modelName: 'incidents',
      query: {
        filter: [{
          field: '_id',
          regexp: `(?i)${searchText}.*`
        }, {
          field: 'name',
          regexp: `(?i)${searchText}.*`
        }],
        sort: [{
          descending: sortDescending,
          field: sortField
        }],
        stream: {
          limit: 1000,
          batch: 100
        },
        filterOperator: 'or'
      },
      streamOptions,
      onInit,
      onResponse,
      onError,
      onCompleted
    });
  }
};
