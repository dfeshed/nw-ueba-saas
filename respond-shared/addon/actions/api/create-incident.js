import { lookup } from 'ember-dependency-lookup';
import FilterQuery from 'respond-shared/utils/filter-query';

export default {

  /**
   * Executes a websocket fetch call for all enabled Users and returns a Promise.
   *
   * @method getAllEnabledUsers
   * @public
   * @returns {Promise}
   */
  getAllEnabledUsers() {
    const query = FilterQuery.create().addSortBy('name', false).addFilter('status', 'enabled');
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'findAll',
      modelName: 'users',
      query: query.toJSON()
    });
  },

  /**
   * Executes a websocket fetch call for all priority types that can be applied to an incident.
   *
   * @method getAllPriorityTypes
   * @public
   * @returns {Promise}
   */
  getAllPriorityTypes() {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'findAll',
      modelName: 'priority-types',
      query: {}
    });
  },

  /**
   * Executes a websocket fetch call for the hierarchical (two-level) set of categories (e.g., Error/Malfunction,
   * Environmental/Flood, etc) which can be tagged to Incidents
   * @method getAllCategories
   * @public
   * @returns {*}
   */
  getAllCategories() {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'findAll',
      modelName: 'category-tags',
      query: {}
    });
  },

  /**
   * Executes a websocket promise request to add a given list of alerts to a given incident ID.
   * @param {[]} alertIds The alertIds for the alerts to be added to the incident.
   * @param {string} incidentId The ID of the incident to be added to.
   * @returns {Promise}
   * @public
   */
  createIncidentFromEvents(data) {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'createRecord',
      modelName: 'incident-events',
      query: {
        data
      }
    });
  },

  createIncidentFromAlerts(incidentDetails, alertIds) {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'createRecord',
      modelName: 'incident-alerts',
      query: {
        data: {
          entity: incidentDetails,
          associated: alertIds
        }
      }
    });
  }
};