import { promiseRequest } from 'streaming-data/services/data-access/requests';
import {
  addSortBy,
  addFilter
} from 'investigate-files/actions/utils/query-util';

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
  const { sortField, isSortDescending: isDescending } = sort;
  query = addSortBy(query, sortField, isDescending);
  query = addFilter(query, expressionList);
  return promiseRequest({
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

  const { sortField, isSortDescending: isDescending } = sort;
  query = addSortBy(query, sortField, isDescending);
  query = addFilter(query, expressionList);
  query.fields = fields;

  return promiseRequest({
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

  return promiseRequest({
    method: 'saveFilter',
    modelName: 'files',
    query: { data }
  });
};

const getSavedFilters = () => {
  return promiseRequest({
    method: 'getFilter',
    modelName: 'files',
    query: {}
  });
};

const deleteFilter = (id) => {
  return promiseRequest({
    method: 'deleteFilter',
    modelName: 'files',
    query: { data: { id } }
  });
};

const getPreferences = () => {
  return promiseRequest({
    method: 'getPreferences',
    modelName: 'filesPreferences',
    query: {}
  });
};

const setPreferences = (preferences) => {
  return promiseRequest({
    method: 'setPreferences',
    modelName: 'filesPreferences',
    query: {
      data: preferences
    }
  });
};

export default {
  fetchFiles,
  fileExport,
  createCustomSearch,
  getSavedFilters,
  deleteFilter,
  getPreferences,
  setPreferences
};