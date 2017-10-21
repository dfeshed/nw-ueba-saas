import { promiseRequest, streamRequest } from 'streaming-data/services/data-access/requests';

// NOOP function to replace Ember.K
const NOOP = () => {};

/**
 * Executes a websocket fetch call for all system snapshots and returns a Promise.
 *
 * @method getAllSnapShots
 * @param data
 * @public
 * @returns {Promise}
 */
const getAllSnapShots = (data) => {
  return promiseRequest({
    method: 'getAllSnapShots',
    modelName: 'endpoint',
    query: { data }
  });
};

/**
 * Executes a websocket fetch call for Host (machine) details of given scanTime and machine id and returns a Promise.
 *
 * @method getHostDetails
 * @param data
 * @public
 * @returns {Promise}
 */
const getHostDetails = (data) => {
  return promiseRequest({
    method: 'getHostDetails',
    modelName: 'endpoint',
    query: { data }
  });
};

/**
 * Executes a websocket fetch call for host autoruns, services and tasks details of given scanTime and machine id.
 *
 * @method getFileContextList
 * @param data
 * @public
 * @returns {Promise}
 */
const getFileContextData = (data) => {
  return promiseRequest({
    method: 'getFileContextList',
    modelName: 'endpoint',
    query: { data }
  });
};

/**
   * Executes a websocket fetch call for host details export.
   *
   * @method exportFileContext
   * @param data
   * @public
   * @returns {Promise}
   */
const exportFileContext = (data) => {
  return promiseRequest({
    method: 'exportFileContext',
    modelName: 'endpoint',
    query: { data }
  });
};


const getFileSearchResults = (filterObj, { onResponse = NOOP, onInit = NOOP, onError = NOOP, onCompleted = NOOP }) => {
  const filter = [{
    field: 'keyword',
    value: filterObj.text
  },
  {
    field: 'machineAgentId',
    value: filterObj.agentId
  }];
  return streamRequest({
    method: 'fileContextSearch',
    modelName: 'endpoint',
    query: { filter },
    streamOptions: { requireRequestId: false },
    onInit,
    onResponse,
    onError,
    onCompleted
  });
};

export default {
  getAllSnapShots,
  getHostDetails,
  getFileContextData,
  exportFileContext,
  getFileSearchResults
};
