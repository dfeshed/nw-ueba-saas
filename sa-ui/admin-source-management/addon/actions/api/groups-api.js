// import buildExplorerQuery from './util/explorer-build-query';
import { lookup } from 'ember-dependency-lookup';
import { addSortBy, addFilter } from 'admin-source-management/actions/api/utils/query-util';

/**
 * Retrieves all matching groups from the server.
 * @returns Promise that will resolve with the server response.
 * @public
 */
function fetchGroups(pageNumber, sort, expressionList) {
  let query = {
    pageNumber: pageNumber || 0,
    pageSize: 1000
  };
  const request = lookup('service:request');
  const { sortField, isSortDescending: isDescending } = sort;
  query = addSortBy(query, sortField, isDescending);
  query = addFilter(query, expressionList);
  return request.promiseRequest({
    modelName: 'groups',
    method: 'fetchGroups',
    query: {
      data: query
    },
    streamOptions: {
      cancelPreviouslyExecuting: true
    }
  });
}

/**
 * Fetches a list of group summary objects.
 * @public
 */
function fetchGroupList() {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'groups',
    method: 'fetchGroupList',
    query: {}
  });
}

/**
 * Fetches a single group.
 * @public
 */
function fetchGroup(id) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'groups',
    method: 'fetchGroup',
    query: { data: id }
  });
}

/**
 * Fetches group ranking.
 * @public
 */
function fetchGroupRanking(sourceType) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'groups',
    method: 'fetchGroupRanking',
    query: { data: sourceType }
  });
}

function saveGroupRanking(groupRankingQuery) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'groups',
    method: 'saveGroupRanking',
    query: { data: groupRankingQuery }
  });
}

/**
 * Deletes one or more groups from list of group IDs.
 * The server API is the same for both...
 * @param {*} ids
 * @public
 */
function deleteGroups(ids) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'groups',
    method: 'remove',
    query: { data: ids }
  });
}

/**
 * Publishes one or more groups from list of group IDs.
 * The server API is the same for both...
 * @param {*} ids
 * @public
 */
function publishGroups(ids) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'groups',
    method: 'publish',
    query: { data: ids }
  });
}

/**
 * Creates or Updates the passed group.
 * The server API is the same for both...
 * @param {*} group
 * @public
 */
function saveGroup(group) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'groups',
    method: 'saveGroup',
    query: {
      data: group
    }
  });
}

/**
 * Saves and Publishes the passed group.
 * Published group is visible in the group policy document
 * @param {*} group
 * @public
 */
function savePublishGroup(group) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'groups',
    method: 'savePublishRecord',
    query: {
      data: group
    }
  });
}

export default {
  deleteGroups,
  fetchGroups,
  fetchGroupList,
  fetchGroup,
  publishGroups,
  saveGroup,
  savePublishGroup,
  fetchGroupRanking,
  saveGroupRanking
};
