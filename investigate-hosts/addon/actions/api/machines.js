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
  return request.promiseRequest({
    method: 'findAll',
    modelName: 'investigate-service',
    query: {}
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

  return request.promiseRequest({
    method: 'saveFilter',
    modelName: 'endpoint',
    query: { data },
    streamOptions: {
      socketUrlPostfix: 'any'
    }
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
  return request.promiseRequest({
    method: 'machine-schema',
    modelName: 'endpoint',
    query: {},
    streamOptions: {
      socketUrlPostfix: 'any'
    }
  });
};

/**
 * Executes a websocket call to start the request scan for selected hosts
 *
 * @method startScanRequest
 * @param data {Object} of agentIds
 * @public
 * @returns {Promise}
 */
const startScanRequest = (agentIds) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'commandScan',
    modelName: 'agent',
    query: { data: { agentIds, scanCommandType: 'QUICK_SCAN' } }
  });
};

/**
 * Executes a websocket call to cancel the request scan for selected hosts
 *
 * @method stopScanRequest
 * @param agentIds {Object} of agentIds
 * @public
 * @returns {Promise}
 */
const stopScanRequest = (agentIds) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'stopScan',
    modelName: 'agent',
    query: { data: { agentIds, scanCommandType: 'CANCEL_SCAN' } }
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

export default {
  getAllServices,
  getAllFilters,
  getPageOfMachines,
  downloadMachine,
  createCustomSearch,
  getAllSchemas,
  startScanRequest,
  stopScanRequest,
  deleteHosts,
  pollAgentStatus,
  getContext
};
