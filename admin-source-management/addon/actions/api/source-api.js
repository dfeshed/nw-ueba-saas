// import buildExplorerQuery from './util/explorer-build-query';
import { lookup } from 'ember-dependency-lookup';
import { addSortBy, addFilter } from 'admin-source-management/actions/api/utils/query-util';

/**
 * Retrieves all matching groups from the server.
 * @returns Promise that will resolve with the server response.
 * @public
 */
function fetchSources(pageNumber, sort, expressionList) {
  let query = {
    pageNumber: pageNumber || 0,
    pageSize: 1000
  };
  const request = lookup('service:request');
  const { sortField, isSortDescending: isDescending } = sort;
  query = addSortBy(query, sortField, isDescending);
  query = addFilter(query, expressionList);
  return request.promiseRequest({
    modelName: 'source',
    method: 'fetchSources',
    query: {
      data: query
    }
  });
}

/**
 * Fetches a list of source summary objects.
 * @public
 */
function fetchSourceList() {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'source',
    method: 'fetchSourceList',
    query: {}
  });
}

/**
 * Fetches a single source.
 * @public
 */
function fetchSource(id) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'source',
    method: 'fetchSource',
    query: { data: id }
  });
}

/**
 * Deletes one or more polices from list of source IDs.
 * The server API is the same for both...
 * @param {*} ids
 * @public
 */
function deleteSources(ids) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'source',
    method: 'remove',
    query: { data: ids }
  });
}

/**
 * Publishes one or more polices from list of source IDs.
 * The server API is the same for both...
 * @param {*} ids
 * @public
 */
function publishSources(ids) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'source',
    method: 'publish',
    query: { data: ids }
  });
}

/**
 * Creates or Updates the passed source.
 * The server API is the same for both...
 * @param {*} source
 * @public
 */
function saveSource(source) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'source',
    method: 'saveSource',
    query: {
      data: source
    }
  });
}

/**
 * Saves and Publishes the passed source.
 * Published source is visible in the group source document
 * @param {*} source
 * @public
 */
function savePublishSource(source) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'source',
    method: 'savePublishRecord',
    query: {
      data: source
    }
  });
}

/**
 * Get a list of endpoint servers that the
 * agent will push the source to.
 * @public
 */
function fetchEndpointServers() {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'source',
    method: 'fetchEndpointServers',
    query: {}
  });
}

/**
 * Get a list of log servers that the
 * agent will push the source to.
 * @public
 */
function fetchLogServers() {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'source',
    method: 'fetchLogServers',
    query: {}
  });
}

/**
* Get source resolution from group ranking.
* Contains source and origins object that has group, source and conflict elements for each source setting
*/
function fetchRankingView(groupRankingViewQuery) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'groups',
    method: 'fetchRankingView',
    query: { data: groupRankingViewQuery }
  });
}

export default {
  deleteSources,
  fetchSources,
  fetchSourceList,
  fetchSource,
  publishSources,
  saveSource,
  savePublishSource,
  fetchEndpointServers,
  fetchLogServers,
  fetchRankingView
};
