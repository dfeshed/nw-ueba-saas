// import buildExplorerQuery from './util/explorer-build-query';
import { lookup } from 'ember-dependency-lookup';

/**
 * Fetches all groups.
 * @public
 */
function fetchGroups(/* filters, sort */) {
  const request = lookup('service:request');
  // const query = buildExplorerQuery(filters, sort, 'name');
  return request.promiseRequest({
    modelName: 'groups',
    method: 'findAll',
    query: {}
    // query: query.toJSON()
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
    method: 'updateRecord',
    query: {
      data: group
    }
  });
}

export default {
  fetchGroups,
  saveGroup
};
