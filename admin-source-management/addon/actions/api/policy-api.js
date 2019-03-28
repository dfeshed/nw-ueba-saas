// import buildExplorerQuery from './util/explorer-build-query';
import { lookup } from 'ember-dependency-lookup';
import { addSortBy, addFilter } from 'admin-source-management/actions/api/utils/query-util';

/**
 * Retrieves all matching groups from the server.
 * @returns Promise that will resolve with the server response.
 * @public
 */
function fetchPolicies(pageNumber, sort, expressionList) {
  let query = {
    pageNumber: pageNumber || 0,
    pageSize: 1000
  };
  const request = lookup('service:request');
  const { sortField, isSortDescending: isDescending } = sort;
  query = addSortBy(query, sortField, isDescending);
  query = addFilter(query, expressionList);
  return request.promiseRequest({
    modelName: 'policy',
    method: 'fetchPolicies',
    query: {
      data: query
    }
  });
}

/**
 * Fetches a list of policy summary objects.
 * @public
 */
function fetchPolicyList() {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'policy',
    method: 'fetchPolicyList',
    query: {}
  });
}

/**
 * Fetches a single policy.
 * @public
 */
function fetchPolicy(id) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'policy',
    method: 'fetchPolicy',
    query: { data: id }
  });
}

/**
 * Deletes one or more polices from list of policy IDs.
 * The server API is the same for both...
 * @param {*} ids
 * @public
 */
function deletePolicies(ids) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'policy',
    method: 'remove',
    query: { data: ids }
  });
}

/**
 * Publishes one or more polices from list of policy IDs.
 * The server API is the same for both...
 * @param {*} ids
 * @public
 */
function publishPolicies(ids) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'policy',
    method: 'publish',
    query: { data: ids }
  });
}

/**
 * Creates or Updates the passed policy.
 * The server API is the same for both...
 * @param {*} policy
 * @public
 */
function savePolicy(policy) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'policy',
    method: 'savePolicy',
    query: {
      data: policy
    }
  });
}

/**
 * Saves and Publishes the passed policy.
 * Published policy is visible in the group policy document
 * @param {*} policy
 * @public
 */
function savePublishPolicy(policy) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'policy',
    method: 'savePublishRecord',
    query: {
      data: policy
    }
  });
}

/**
 * Get a list of endpoint servers that the
 * agent will push the policy to.
 * @public
 */
function fetchEndpointServers() {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'policy',
    method: 'fetchEndpointServers',
    query: {}
  });
}

/**
 * Get a list of log servers that the
 * agent will push the policy to.
 * @public
 */
function fetchLogServers() {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'policy',
    method: 'fetchLogServers',
    query: {}
  });
}

/**
* Get policy resolution from group ranking.
* Contains policy and origins object that has group, policy and conflict elements for each policy setting
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
  deletePolicies,
  fetchPolicies,
  fetchPolicyList,
  fetchPolicy,
  publishPolicies,
  savePolicy,
  savePublishPolicy,
  fetchEndpointServers,
  fetchLogServers,
  fetchRankingView
};
