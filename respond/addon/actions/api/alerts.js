import { lookup } from 'ember-dependency-lookup';
import buildExplorerQuery from './util/explorer-build-query';
import FilterQuery from 'respond-shared/utils/filter-query';
import chunk from 'respond/utils/array/chunk';
import RSVP from 'rsvp';

const NOOP = () => {};

export default {
  /**
   * Executes a websocket Incidents fetch call and returns a Promise. Arguments include the filters that should be
   * applied against the incidents collection and the sort information for the returned incidents result set.
   *
   * @method getAlerts
   * @public
   * @param filters The filters to apply against the alerts collection
   * @param sort The sorting information ({ id, isDescending }) for the result set
   * @param {function} onResponse The callback for the onNext/onResponse event (when data retrieved by chunk)
   * @param {function} onError The callback for any error during streaming
   * @returns {Promise}
   */
  getAlerts(filters, sort, { onResponse = NOOP, onError = NOOP, onInit = NOOP, onCompleted = NOOP }) {
    const request = lookup('service:request');
    const query = buildExplorerQuery(filters, sort, 'receivedTime');
    const streamOptions = { cancelPreviouslyExecuting: true };

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
   * Retrieves the total count of alerts for a query. This is separated from the getAlerts() call to improve
   * performance, allowing the first chunk of streamed results to arrive without waiting further for this call
   * @method getAlertsCount
   * @public
   * @param filters
   * @param sort
   * @returns {Promise}
   */
  getAlertsCount(filters, sort) {
    const request = lookup('service:request');
    const query = buildExplorerQuery(filters, sort, 'receivedTime');

    return request.promiseRequest({
      method: 'queryRecord',
      modelName: 'alerts-count',
      query: query.toJSON()
    });
  },

  /**
   * Retrieves the events for a given alert id.
   * @method getAlertEvents
   * @public
   * @param alertId
   * @returns {Promise}
   */
  getAlertEvents(alertId) {
    const request = lookup('service:request');
    const query = FilterQuery.create()
      .addSortBy('timestamp', false)
      .addFilter('_id', alertId);

    return request.promiseRequest({
      method: 'query',
      modelName: 'alerts-events',
      query: query.toJSON()
    });
  },

  /**
   * Executes a websocket delete alert call and returns a Promise. The alert ids submitted are split up into
   * chunks of 500 to ensure that the request size never exceeds the maximum of 16KB
   * @method delete
   * @public
   * @param alertId The id of the incident to delete
   * @returns {Promise}
   */
  delete(alertId) {
    const request = lookup('service:request');
    const alertIdChunks = chunk(alertId, 500);
    const requests = alertIdChunks.map((chunk) => {
      const query = FilterQuery.create().addFilter('_id', chunk);
      return request.promiseRequest({
        method: 'deleteRecord',
        modelName: 'alerts',
        query: query.toJSON()
      });
    });

    return RSVP.allSettled(requests);
  },

  /**
   * Executes a websocket fetch call to retrieve the details of one alert and returns a Promise.
   * @public
   * @param alertId
   * @returns {*}
   */
  getOriginalAlert(alertId) {
    const request = lookup('service:request');
    const query = FilterQuery.create();
    query.addFilter('_id', alertId);

    return request.promiseRequest({
      method: 'queryRecord',
      modelName: 'original-alert',
      query: query.toJSON()
    });
  }
};