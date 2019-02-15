import { lookup } from 'ember-dependency-lookup';

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
 * Executes the web socket call for sending the agent id of selected host to reset risk score
 * @param data
 * @returns {*}
 * @public
 */
const sendHostDataToResetRiskScore = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'resetHostRiskScore',
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

const getHostFileRiskScoreContext = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getHostFileContext',
    modelName: 'respond-server',
    query: { data }
  });
};

const getDetailedHostRiskScoreContext = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getDetailHostContext',
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

const getDetailedFileRiskScoreContext = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getDetailFileContext',
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
const getAlertEvents = (alerts) => {
  const request = lookup('service:request');
  const query = { field: 'ids', values: alerts.mapBy('id') };
  return request.promiseRequest({
    method: 'get-events',
    modelName: 'respond-server',
    query: {
      filter: [query]
    }
  });
};

export default {
  sendDataToResetRiskScore,
  getRiskScoreContext,
  getDetailedFileRiskScoreContext,
  getHostRiskScoreContext,
  getHostFileRiskScoreContext,
  getDetailedHostRiskScoreContext,
  getAlertEvents,
  sendHostDataToResetRiskScore
};
