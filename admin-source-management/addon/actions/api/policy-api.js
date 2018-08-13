// import buildExplorerQuery from './util/explorer-build-query';
import { lookup } from 'ember-dependency-lookup';

/**
 * Fetches all policies.
 * @public
 */
function fetchPolicy(/* filters, sort */) {
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
  fetchPolicy,
  savePolicy
};
