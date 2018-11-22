import { lookup } from 'ember-dependency-lookup';
import filterQuery from 'respond-shared/utils/filter-query';

/**
 * Executes the web socket call for sending the checksum256 of selected files to reset risk score
 * @param data
 * @returns {*}
 * @public
 */
const sendDataToResetRiskScore = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'resetRiskScore',
    modelName: 'respond-server',
    query: { data }
  });
};

/**
 * Executes a websocket fetch call for host context and returns a Promise.
 *
 * @method getRiskScoreContext
 * @param query
 * @public
 * @returns {Promise}
 */
const getHostRiskScoreContext = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getHostContext',
    modelName: 'respond-server',
    query: { data }
  });
};

/**
 * Executes a websocket fetch call for host context and returns a Promise.
 *
 * @method getRiskScoreContext
 * @param query
 * @public
 * @returns {Promise}
 */
const getRiskScoreContext = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getFileContext',
    modelName: 'respond-server',
    query: { data }
  });
};

/**
 * Retrieves the events for a given alert id.
 * @method getAlertEvents
 * @public
 * @param alertId
 * @returns {Promise}
 */
const getAlertEvents = (alertId) => {
  const request = lookup('service:request');
  const query = filterQuery.create()
    .addSortBy('timestamp', false)
    .addFilter('_id', alertId);

  return request.promiseRequest({
    method: 'alert-events',
    modelName: 'respond-server',
    query: query.toJSON()
  });
};


export default {
  sendDataToResetRiskScore,
  getRiskScoreContext,
  getHostRiskScoreContext,
  getAlertEvents
};
