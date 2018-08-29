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
    method: 'findAll',
    query: {}
    // query: query.toJSON()
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
    method: 'saveRecord',
    query: {
      data: policy
    }
  });
}

export default {
  deletePolicies,
  fetchPolicies,
  publishPolicies,
  savePolicy
};
