// import buildExplorerQuery from './util/explorer-build-query';
import { lookup } from 'ember-dependency-lookup';

/**
 * Fetches all policies.
 * @public
 */
function fetchPolicies(/* filters, sort */) {
  const request = lookup('service:request');
  // const query = buildExplorerQuery(filters, sort, 'name');
  return request.promiseRequest({
    modelName: 'policy',
    method: 'fetchPolicies',
    query: {}
    // query: query.toJSON()
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

export default {
  deletePolicies,
  fetchPolicies,
  fetchPolicyList,
  fetchPolicy,
  publishPolicies,
  savePolicy,
  savePublishPolicy
};
