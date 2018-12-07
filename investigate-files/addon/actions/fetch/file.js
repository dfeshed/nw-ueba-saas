import { lookup } from 'ember-dependency-lookup';
import {
  addSortBy,
  addFilter
} from 'investigate-shared/utils/query-util';

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
 * Retrieves all matching global files from the server.
 * @returns Promise that will resolve with the server response.
 * @public
 */
const fetchFiles = (pageNumber, sort, expressionList) => {
  let query = {
    pageNumber: pageNumber || 0,
    pageSize: 100
  };
  const request = lookup('service:request');
  const { sortField, isSortDescending: isDescending } = sort;
  query = addSortBy(query, sortField, isDescending);
  query = addFilter(query, expressionList);
  return request.promiseRequest({
    method: 'search',
    modelName: 'files',
    query: {
      data: query
    }
  });
};

/**
 * Exports filtered file entries to csv
 * @public
 */
const fileExport = (sort, expressionList, fields) => {
  let query = {};

  const request = lookup('service:request');
  const { sortField, isSortDescending: isDescending } = sort;
  query = addSortBy(query, sortField, isDescending);
  query = addFilter(query, expressionList);

  query.fields = fields;

  return request.promiseRequest({
    method: 'exportFile',
    modelName: 'files',
    query: { data: query }
  });
};

/**
 * Websocket call for custom search
 * @public
 */

const createCustomSearch = (filter, expressionList, filterTypeParameter) => {
  const request = lookup('service:request');
  const { id } = filter;
  const data = {
    id,
    name: filter.name.trim(),
    description: filter.description,
    filterType: filterTypeParameter
  };
  if (filter) {
    data.criteria = { expressionList, 'predicateType': 'AND' };

  }

  return request.promiseRequest({
    method: 'saveFilter',
    modelName: 'files',
    query: { data },
    streamOptions: {
      socketUrlPostfix: 'any'
    }
  });
};

const getSavedFilters = () => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getFilter',
    modelName: 'files',
    query: {},
    streamOptions: {
      socketUrlPostfix: 'any'
    }
  });
};

const deleteFilter = (id) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'deleteFilter',
    modelName: 'files',
    query: { data: { id } },
    streamOptions: {
      socketUrlPostfix: 'any'
    }
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

const fetchRemediation = (thumbprints) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getRemediation',
    modelName: 'files',
    query: { data: thumbprints }
  });
};

/**
 * get file properties for requested hash
 * @param checksum
 * @returns {*}
 * @public
 */
const getSelectedFileProperties = (checksum) => {
  const hashes = [checksum];
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getFiles',
    modelName: 'files',
    query: {
      data: { hashes }
    }
  });
};

export default {
  fetchFiles,
  fileExport,
  createCustomSearch,
  getSavedFilters,
  deleteFilter,
  getAllServices,
  getContext,
  fetchRemediation,
  getSelectedFileProperties
};
