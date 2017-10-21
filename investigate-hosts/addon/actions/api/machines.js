import { promiseRequest, streamRequest } from 'streaming-data/services/data-access/requests';
import { buildSearchCriteria } from './utils';
import { addFilter } from 'investigate-hosts/actions/utils/query-util';

// NOOP function to replace Ember.K
const NOOP = () => { };

/**
 * Executes a websocket fetch call for all system filters and returns a Promise.
 *
 * @method getAllFilters
 * @public
 * @returns {Promise}
 */
const getAllFilters = () => {
  return promiseRequest({
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
const getPageOfMachines = (pageNumber, sort, expressionList) => {
  let query = {
    pageNumber: pageNumber + 1,
    pageSize: 100,
    sort
  };

  query = addFilter(query, expressionList);
  return promiseRequest({
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
   * @param selectedFilter {Object} selected system filter
   * @param columns {Array} only visible filters in the UI
   * @param sort The sorting information ({ id, isDescending }) for the result set
   * @param fields {Array} list of visible columns in machine table
   * @returns {Promise}
   */
const downloadMachine = (selectedFilter, columns, sort, fields) => {
  const data = { sort, fields };
  if (selectedFilter) {
    data.criteria = buildSearchCriteria(selectedFilter, columns);
  }

  return promiseRequest({
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

  return promiseRequest({
    method: 'saveFilter',
    modelName: 'endpoint',
    query: { data }
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
  return promiseRequest({
    method: 'machine-schema',
    modelName: 'endpoint',
    query: {}
  });
};

  /**
   * Executes a websocket call to start the request scan for selected hosts
   *
   * @method startScanRequest
   * @param data {Object} of agentIds and scan type
   * @public
   * @returns {Promise}
   */
const startScanRequest = (data) => {
  return promiseRequest({
    method: 'commandScan',
    modelName: 'agent',
    query: { data }
  });
};

  /**
   * Executes a websocket call to cancel the request scan for selected hosts
   *
   * @method stopScanRequest
   * @param data {Object} of agentIds and scanType
   * @public
   * @returns {Promise}
   */
const stopScanRequest = (data) => {
  return promiseRequest({
    method: 'stopScan',
    modelName: 'agent',
    query: { data }
  });
};

const deleteHosts = (data) => {
  return promiseRequest({
    method: 'deleteHosts',
    modelName: 'agent',
    query: { data }
  });
};

  /**
   * Executes a websocket call to notify the agent scan status
   *
   * @method notifyAgentStatus
   * @public
   * @returns {Promise}
   */
const notifyAgentStatus = ({ onResponse = NOOP, onError = NOOP, onInit = NOOP, onCompleted = NOOP }) => {
  return streamRequest({
    method: 'notify',
    modelName: 'agent',
    query: {},
    streamOptions: { requireRequestId: false },
    onInit,
    onResponse,
    onCompleted,
    onError
  });
};

export default {
  getAllFilters,
  getPageOfMachines,
  downloadMachine,
  createCustomSearch,
  getAllSchemas,
  startScanRequest,
  stopScanRequest,
  deleteHosts,
  notifyAgentStatus
};
