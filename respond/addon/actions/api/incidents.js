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
      .addSortBy('alert.timestamp', false)
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
        eventField = (deviceField === 'domain') ? deviceField : `${deviceField}.dns_domain`;
        break;
      case 'HOST':
        eventField = `${deviceField}.dns_hostname`;
        break;
      case 'USER':
        eventField = `${deviceField}.user.username`;
        break;
    }

    const query = FilterQuery.create()
      .addSortBy('alert.timestamp', false)
      .addSinceWhenFilter('alert.timestamp', sinceWhen)
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
  addAlertsToIncident(alerts, incidentId, incidentCreated) {

    return promiseRequest({
      method: 'updateRecord',
      modelName: 'alerts-associated',
      query: {
        data: {
          // entity = POJO with incident ID & created date
          entity: {
            id: incidentId,
            created: incidentCreated
          },
          // associated = array of POJOs with alert IDs
          associated: alerts.map((alert) => ({
            id: alert.id
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
  }
});

export default IncidentsAPI;