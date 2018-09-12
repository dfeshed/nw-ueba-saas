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
 * Gets a single group.
 * @public
 */
function getGroup(id) {
  const request = lookup('service:request');
  return request.promiseRequest({
    modelName: 'groups',
    method: 'getGroup',
    query: { data: id }
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

export default {
  deleteGroups,
  fetchGroups,
  getGroup,
  publishGroups,
  saveGroup
};
