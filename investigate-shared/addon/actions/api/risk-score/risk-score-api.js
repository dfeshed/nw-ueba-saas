import { lookup } from 'ember-dependency-lookup';

/**
 * Executes the web socket call for sending the checksum256 of selected files to reset risk score
 * @param data
 * @returns {*}
 * @public
 */
const sendDataToResetRiskScore = (query) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'resetRiskScore',
    modelName: 'risk-score-server',
    query
  });
};

/**
 * Executes a websocket fetch call for file context and returns a Promise.
 *
 * @method getRiskScoreContext
 * @param query
 * @public
 * @returns {Promise}
 */
const getRiskScoreContext = (query) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getFileContext',
    modelName: 'risk-score-server',
    query
  });
};

export default {
  sendDataToResetRiskScore,
  getRiskScoreContext
};