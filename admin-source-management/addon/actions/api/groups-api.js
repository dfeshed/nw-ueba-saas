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
    method: 'fetchGroups',
    query: {}
    // query: query.toJSON()
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
  savePublishGroup
};
