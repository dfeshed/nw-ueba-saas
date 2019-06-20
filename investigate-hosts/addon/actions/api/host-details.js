import { lookup } from 'ember-dependency-lookup';

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
  const request = lookup('service:request');
  const streamSelector = lookup('service:stream-selector');

  const modelName = 'endpoint';
  const method = 'getAllSnapShots';

  return request.promiseRequest({
    method,
    modelName,
    query: { data },
    streamOptions: streamSelector.streamOptionSelector({ modelName, method })
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
  const request = lookup('service:request');
  return request.promiseRequest({
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
  const request = lookup('service:request');
  return request.promiseRequest({
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
  const request = lookup('service:request');
  return request.promiseRequest({
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
  const request = lookup('service:request');
  const streamSelector = lookup('service:stream-selector');

  const modelName = 'endpoint';
  const method = 'fileContextSearch';
  const streamConfig = { modelName, method, customOptions: { requireRequestId: false } };

  return request.streamRequest({
    method,
    modelName,
    query: { filter },
    streamOptions: streamSelector.streamOptionSelector(streamConfig),
    onInit,
    onResponse,
    onError,
    onCompleted
  });
};

const policyDetails = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getPolicyDetails',
    modelName: 'endpoint',
    query: { data }
  });
};

const fetchRemediation = (thumbprints) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getRemediation',
    modelName: 'endpoint',
    query: { data: thumbprints }
  });
};

const sendFileDownloadToServerRequest = (selectedFileDetails) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'downloadFileToServer',
    modelName: 'agent',
    query: { data: selectedFileDetails }
  });
};

const deleteSelectedFiles = (socketUrlPostfix, ids = []) => {
  const request = lookup('service:request');
  const streamSelector = lookup('service:stream-selector');
  const method = 'hostDownloadDelete';
  const modelName = 'endpoint';

  return request.promiseRequest({
    method,
    modelName,
    query: { data: { ids } },
    streamOptions: streamSelector.streamOptionSelector({ modelName, method, customOptions: { socketUrlPostfix } })
  });
};

const saveLocalMFTCopy = (socketUrlPostfix, data) => {
  const request = lookup('service:request');
  const streamSelector = lookup('service:stream-selector');
  const method = 'saveLocalMFTCopy';
  const modelName = 'endpoint';

  return request.promiseRequest({
    method,
    modelName,
    query: { data },
    streamOptions: streamSelector.streamOptionSelector({ modelName, method, customOptions: { socketUrlPostfix } })
  });
};

export default {
  getAllSnapShots,
  getHostDetails,
  getFileContextData,
  exportFileContext,
  getFileSearchResults,
  policyDetails,
  fetchRemediation,
  sendFileDownloadToServerRequest,
  deleteSelectedFiles,
  saveLocalMFTCopy
};
