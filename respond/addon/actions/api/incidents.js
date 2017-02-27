import Ember from 'ember';
import { assert } from 'ember-metal/utils';
import { promiseRequest } from 'streaming-data/services/data-access/requests';
import { CANNED_FILTER_TYPES_BY_NAME } from 'respond/utils/canned-filter-types';
import { SORT_TYPES_BY_NAME } from 'respond/utils/sort-types';
import FilterQuery from 'respond/utils/filter-query';
import Indicator from 'respond/utils/indicator/indicator';

const {
  Object: EmberObject,
  isArray,
  isPresent,
  isEmpty,
  isNone,
  typeOf } = Ember;

const IncidentsAPI = EmberObject.extend({});

IncidentsAPI.reopenClass({
  /**
   * Executes a websocket Incidents fetch call and returns a Promise. Arguments include the filters that should be
   * applied against the incidents collection and the sort information for the returned incidents result set.
   *
   * @method getIncidents
   * @public
   * @param filters The filters to apply against the incidents collection
   * @param sort The sorting information ({ id, isDescending }) for the result set
   * @returns {Promise}
   */
  getIncidents(filters, sort) {
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
   * @param incidentId
   * @param field
   * @param updatedValue
   * @returns {*}
   */
  updateIncident(incidentId, field, updatedValue) {
    const incidentIds = isArray(incidentId) ? incidentId : [incidentId];

    return promiseRequest({
      method: 'updateRecord',
      modelName: 'incidents',
      query: {
        incidentIds,
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
    assert('bulkChangeIncidentPriority is provided array of incident IDs', isArray(incidentIds) && !isEmpty(incidentIds));
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
    assert('bulkChangeIncidentStatus is provided array of incident IDs', isArray(incidentIds) && !isEmpty(incidentIds));
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
      !isNone(newAssignee.firstName)) && !isNone(newAssignee.lastName) && !isNone(newAssignee.email);
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
    assert('bulkChangeIncidentAssignee is provided array of incident IDs', isArray(incidentIds) && !isEmpty(incidentIds));
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
      .addSortBy('event.timestamp', false)
      .addFilter('_id', incidentId);

    return promiseRequest({
      method: 'queryRecord',
      modelName: 'storyline',
      query: query.toJSON()
    })
      .then((response) => {
        const { data: { relatedIndicators = [] } } = response;

        // flatten `data` by one level
        response.data = relatedIndicators;

        // wrap the `indicator` attr of each array member
        relatedIndicators.forEach((item) => {
          item.indicator = Indicator.create(item.indicator);
        });
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
  deleteIncident(incidentId) {
    const query = FilterQuery.create()
      .addFilter('_id', incidentId);

    return promiseRequest({
      method: 'deleteRecord',
      modelName: 'incidents',
      query: query.toJSON()
    });
  },

  /**
   * Executes a websocket delete multiple incidents. This merely delegates to the deleteIncident() method, which
   * also supports an array of incident ids
   * @method bulkDeleteIncidents
   * @public
   * @param incidentIds Array of incident Ids to delete
   * @returns {Promise}
   */
  bulkDeleteIncidents(incidentIds) {
    return IncidentsAPI.deleteIncident(incidentIds);
  }
});

export default IncidentsAPI;