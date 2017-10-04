import EmberObject from 'ember-object';
import { promiseRequest, streamRequest } from 'streaming-data/services/data-access/requests';
import FilterQuery from 'respond/utils/filter-query';
import chunk from 'respond/utils/array/chunk';
import RSVP from 'rsvp';
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
    const entityIdChunks = chunk(entityId, 500);

    const requests = entityIdChunks.map((chunk) => {
      return promiseRequest({
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
    const query = FilterQuery.create()
      .addSortBy('receivedTime', false)
      .addFilter('incidentId', incidentId);

    return streamRequest({
      method: 'stream',
      modelName: 'alerts',
      query: query.toJSON(),
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
   * @param {String[]} devices List of device fields to be included in filter.
   * Each device field is either: 'source.device', 'destination.device', 'detector' or 'domain'.
   * For now, only the first device will be used. When backend supports querying multiple device fields, all the given
   * devices will be used.
   * @param {{ onResponse: Function, onError: Function, onInit: Function, onCompleted: Function }} callbacks
   * @public
   */
  getRelatedAlerts(entityType, entityId, sinceWhen, devices, { onResponse = NOOP, onError = NOOP, onInit = NOOP, onCompleted = NOOP }) {

    // Map the given list of devices to fields in the IM Mongo database.
    // For now, only map the first one; ignore the others until backend supports querying multiple fields with an OR.
    let [ deviceField ] = devices || [];
    if (String(deviceField).match(/source|destination/)) {
      deviceField += '.device';
    }
    let eventField;
    switch (entityType) {
      case 'FILE_NAME':
        eventField = 'data.filename';
        break;
      case 'FILE_HASH':
        eventField = 'data.hash';
        break;
      case 'IP':
        eventField = `${deviceField}.ip_address`;
        break;
      case 'MAC_ADDRESS':
        eventField = `${deviceField}.mac_address`;
        break;
      case 'DOMAIN':
        eventField = `${deviceField}.dns_domain`;
        break;
      case 'HOST':
        eventField = (deviceField === 'domain') ? deviceField : `${deviceField}.dns_hostname`;
        break;
      case 'USER':
        eventField = `${deviceField}.user.username`;
        break;
    }

    const query = FilterQuery.create()
      .addSortBy('receivedTime', false)
      .addSinceWhenFilter('receivedTime', sinceWhen)
      .addFilter(`alert.events.${eventField}`, entityId);

    return streamRequest({
      method: 'stream',
      modelName: 'alerts',
      query: query.toJSON(),
      onInit,
      onResponse,
      onError,
      onCompleted
    });
  },


  /**
   * Executes a websocket promise request to add a given list of alerts to a given incident ID.
   * @param {object[]} alerts The alert POJOs to be added to the incident.
   * @param {string} incidentId The ID of the incident to be added to.
   * @param {number} incidentCreated The incident's created timestamp. Required by server API for some odd reason.
   * @returns {Promise}
   * @public
   */
  addAlertsToIncident(alerts, incidentId) {
    return promiseRequest({
      method: 'updateRecord',
      modelName: 'alerts-associated',
      query: {
        data: {
          // entity = POJO with incident ID
          entity: {
            id: incidentId
          },
          // associated = array of POJOs with alert IDs
          associated: alerts.map((alert) => ({
            // if the alerts array is an array of string, use the string as ID, otherwise expect each alert to be
            // an object and pull from there. Note: Respond Server team is reworking the service interface. This
            // will be reworked/simplified afterwards (cf ASOC-42078)
            id: typeof alert === 'string' ? alert : alert.id
          }))
        }
      }
    }).then((response) => {

      // response does not include the updated alert POJOs,
      // so set response.data = the same inputted alert POJOs,
      // but with their `partOfIncident` & `incidentId` props updated.
      response.data = alerts.map((alert) => ({
        ...alert,
        partOfIncident: true,
        incidentId
      }));

      return response;
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
    const incidentIdChunks = chunk(incidentId, 500);
    const requests = incidentIdChunks.map((chunk) => {
      const query = FilterQuery.create().addFilter('_id', chunk);
      return promiseRequest({
        method: 'deleteRecord',
        modelName: 'incidents',
        query: query.toJSON()
      });
    });

    return RSVP.allSettled(requests);
  },

  createIncidentFromAlerts(name, alertIds) {
    return promiseRequest({
      method: 'createRecord',
      modelName: 'incidents',
      query: {
        data: {
          entity: { name },
          associated: alertIds.map((id) => ({ id }))
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
    // TODO: Allow query builder utility to support regex filters and "or" filter operator
    return streamRequest({
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
      onInit,
      onResponse,
      onError,
      onCompleted
    });
  }
});

export default IncidentsAPI;