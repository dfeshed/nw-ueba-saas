import { lookup } from 'ember-dependency-lookup';
import { addFilter } from 'investigate-shared/utils/query-util';

/**
 * Executes a websocket fetch call for all services and returns a Promise.
 *
 * @method getAllServices
 * @param data
 * @public
 * @returns {Promise}
 */
const getAllServices = () => {
  const request = lookup('service:request');
  const streamSelector = lookup('service:stream-selector');

  const modelName = 'investigate-service';
  const method = 'findAll';

  return request.promiseRequest({
    method,
    modelName,
    query: {},
    streamOptions: streamSelector.streamOptionSelector({ modelName, method })
  });
};

/**
 * Executes a websocket fetch call for all system filters and returns a Promise.
 *
 * @method getAllFilters
 * @public
 * @returns {Promise}
 */
const getAllFilters = () => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getAllFilters',
    modelName: 'endpoint',
    query: {}
  });
};


/**
 * Executes a websocket fetch call for all/custom machines and returns a Promise.
 * @returns Promise that will resolve with the server response.
 * @public
 */
const getPageOfMachines = (pageNumber, [{ key, descending } = {}], expressionList) => {
  let query = {
    pageNumber: pageNumber + 1,
    pageSize: 100,
    sort: { keys: [key], descending }
  };

  query = addFilter(query, expressionList, 'host');
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'machines',
    modelName: 'endpoint',
    query: {
      data: query
    }
  });
};

/**
 * Executes a websocket fetch call for all downloads and returns a Promise.
 * @returns Promise that will resolve with the server response.
 * @public
 */
const getPageOfDownloadsApi = (pageNumber, key, descending, expressionList, socketUrlPostfix) => {
  let data = {
    pageNumber: pageNumber || 0,
    pageSize: 100,
    sort: { keys: [key], descending }
  };

  data = addFilter(data, expressionList);

  const request = lookup('service:request');
  const streamSelector = lookup('service:stream-selector');
  const modelName = 'endpoint';
  const method = 'hostDownload';

  return request.promiseRequest({
    method,
    modelName,
    query: {
      data
    },
    streamOptions: streamSelector.streamOptionSelector({ modelName, method, customOptions: { socketUrlPostfix } })
  });
};

/**
 * Executes a websocket fetch call for all machines and returns a Promise.
 *
 * @method downloadMachine
 * @public
 * @param expressionList {Object} for selected criteria
 * @param columns {Array} only visible filters in the UI
 * @param sort The sorting information ({ id, isDescending }) for the result set
 * @param fields {Array} list of visible columns in machine table
 * @returns {Promise}
 */
const downloadMachine = (expressionList = [], columns, [{ key, descending }], fields) => {
  const data = { sort: { keys: [key], descending }, fields };
  if (expressionList.length) {
    data.criteria = { expressionList, 'predicateType': 'AND' };
  }

  const request = lookup('service:request');

  return request.promiseRequest({
    method: 'export',
    modelName: 'endpoint',
    query: { data }
  });
};


/**
 * Websocket call for create custom search
 * @public
 */

const createCustomSearch = (filter, expressionList, filterTypeParameter) => {
  const { id, description, name } = filter;
  const data = {
    id,
    name: name.trim(),
    description,
    filterType: filterTypeParameter
  };
  if (filter) {
    data.criteria = { expressionList, 'predicateType': 'AND' };
  }

  const request = lookup('service:request');
  const streamSelector = lookup('service:stream-selector');

  const modelName = 'endpoint';
  const method = 'saveFilter';

  return request.promiseRequest({
    method,
    modelName,
    query: { data },
    streamOptions: streamSelector.streamOptionSelector({ modelName, method })
  });
};


/**
 * Executes a websocket call to get all the schemas
 *
 * @method getAllSchemas
 * @public
 * @returns {Promise}
 */
const getAllSchemas = () => {
  const request = lookup('service:request');
  const streamSelector = lookup('service:stream-selector');

  const modelName = 'endpoint';
  const method = 'machine-schema';

  return request.promiseRequest({
    method,
    modelName,
    query: {},
    streamOptions: streamSelector.streamOptionSelector({ modelName, method })
  });
};

/**
 * Executes a websocket call to start the request scan for selected hosts
 *
 * @method startScanRequest
 * @param agentIds {Object} of agentIds
 * @param serverId {String} post to a specific server
 * @public
 * @returns {Promise}
 */
const startScanRequest = (agentIds, serverId) => {
  const request = lookup('service:request');
  const streamOptions = serverId ? { socketUrlPostfix: serverId, requiredSocketUrl: 'endpoint/socket' } : null;
  return request.promiseRequest({
    method: 'commandScan',
    modelName: 'agent',
    query: { data: { agentIds, scanCommandType: 'QUICK_SCAN' } },
    streamOptions
  });
};

/**
 * Executes a websocket call to cancel the request scan for selected hosts
 *
 * @method stopScanRequest
 * @param agentIds {Object} of agentIds
 * @param serverId {String} post to a specific server
 * @public
 * @returns {Promise}
 */
const stopScanRequest = (agentIds, serverId) => {
  const request = lookup('service:request');
  const streamOptions = serverId ? { socketUrlPostfix: serverId, requiredSocketUrl: 'endpoint/socket' } : null;
  return request.promiseRequest({
    method: 'stopScan',
    modelName: 'agent',
    query: { data: { agentIds, scanCommandType: 'CANCEL_SCAN' } },
    streamOptions
  });
};

const deleteHosts = (agentIds) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'deleteHosts',
    modelName: 'agent',
    query: { data: { machineAgentIds: agentIds } }
  });
};

/**
 * Executes a websocket call to poll the agent scan status
 *
 * @method pollAgentStatus
 * @public
 * @returns {Promise}
 */
const pollAgentStatus = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getAgentStatus',
    modelName: 'agent',
    query: { data }
  });
};

const getContext = (query, handlers) => {
  const request = lookup('service:request');
  return request.streamRequest({
    method: 'stream',
    modelName: 'context-service',
    query,
    onInit: handlers.initState,
    streamOptions: { requireRequestId: true },
    onResponse: handlers.onResponse,
    onError: handlers.onError
  });
};

const downloadMFT = (data) => {
  const request = lookup('service:request');
  const streamSelector = lookup('service:stream-selector');

  const modelName = 'endpoint';
  const method = 'downloadMFT';
  const { serverId: socketUrlPostfix } = data;

  return request.promiseRequest({
    method,
    modelName,
    query: { data },
    streamOptions: streamSelector.streamOptionSelector({ modelName, method, customOptions: { socketUrlPostfix } })
  });
};

const downloadSystemDump = (data, socketUrlPostfix) => {
  const request = lookup('service:request');
  const streamSelector = lookup('service:stream-selector');
  const modelName = 'endpoint';
  const method = 'downloadSystemDump';

  if (socketUrlPostfix) {
    return request.promiseRequest({
      method,
      modelName,
      query: { data },
      streamOptions: streamSelector.streamOptionSelector({ modelName, method, customOptions: { socketUrlPostfix } })
    });
  }
  return request.promiseRequest({
    method,
    modelName,
    query: { data }
  });
};


/**
 * Executes a websocket fetch call for all subfolders and returns a Promise.
 * @returns Promise that will resolve with the server response.
 * @public
 */
const getMFTSubfolders = (pageNumber, pageSize, key, descending, expressionList, socketUrlPostfix) => {

  let data = {
    pageNumber: pageNumber || 0,
    pageSize: pageSize || 100,
    sort: { keys: [key], descending }
  };

  data = addFilter(data, expressionList, 'mft');
  const request = lookup('service:request');
  const streamSelector = lookup('service:stream-selector');

  const method = 'mftGetRecords';
  const modelName = 'endpoint';

  return request.promiseRequest({
    method,
    modelName,
    query: { data },
    streamOptions: streamSelector.streamOptionSelector({ modelName, method, customOptions: { socketUrlPostfix } })
  });
};


/**
 * Executes a websocket call to isolate selected hosts
 *
 * @method isolateHostRequest
 * @param data {Object} of agentId, exclusion list and comment
 * @param serverId {String} post to a specific server
 * @public
 * @returns {Promise}
 */

const isolateHostRequest = (data, serverId) => {
  const request = lookup('service:request');
  const streamOptions = serverId ? { socketUrlPostfix: serverId, requiredSocketUrl: 'endpoint/socket' } : null;
  return request.promiseRequest({
    method: 'isolateHost',
    modelName: 'agent',
    query: { data },
    streamOptions
  });
};

/**
 * Executes a websocket call to edit exclusion list added for isolate host
 *
 * @method editExclusionListRequest
 * @param data {Object} of agentId, exclusion list and comment
 * @param serverId {String} post to a specific server
 * @public
 * @returns {Promise}
 */

const editExclusionListRequest = (data, serverId) => {
  const request = lookup('service:request');
  const streamOptions = serverId ? { socketUrlPostfix: serverId, requiredSocketUrl: 'endpoint/socket' } : null;
  return request.promiseRequest({
    method: 'editExclusionList',
    modelName: 'agent',
    query: { data },
    streamOptions
  });
};

/**
 * Executes a websocket call to cancel the request scan for selected hosts
 *
 * @method stopIsolationRequest
 * @param agentId and comment added
 * @param serverId {String} post to a specific server
 * @public
 * @returns {Promise}
 */
const stopIsolationRequest = (data, serverId) => {
  const request = lookup('service:request');
  const streamOptions = serverId ? { socketUrlPostfix: serverId, requiredSocketUrl: 'endpoint/socket' } : null;
  return request.promiseRequest({
    method: 'stopIsolation',
    modelName: 'agent',
    query: { data },
    streamOptions
  });
};

export default {
  getAllServices,
  getAllFilters,
  getPageOfMachines,
  getPageOfDownloadsApi,
  downloadMachine,
  createCustomSearch,
  getAllSchemas,
  startScanRequest,
  stopScanRequest,
  deleteHosts,
  pollAgentStatus,
  getContext,
  downloadMFT,
  getMFTSubfolders,
  isolateHostRequest,
  downloadSystemDump,
  editExclusionListRequest,
  stopIsolationRequest
};
