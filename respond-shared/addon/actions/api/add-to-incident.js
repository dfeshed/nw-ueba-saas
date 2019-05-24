import { lookup } from 'ember-dependency-lookup';

// NOOP function to replace Ember.K
const NOOP = () => {};

export default {

  /**
   * Executes a websocket promise request to add a given list of alerts to a given incident ID.
   * @param {string} data all the request parameters to be sent to Investigate Server
   * @returns {Promise}
   * @public
   */
  addEventsToIncident(data) {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'updateRecord',
      modelName: 'incident-events',
      query: {
        data
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
      modelName: 'searched-incidents',
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
      modelName: 'associated-alerts',
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
  }
};