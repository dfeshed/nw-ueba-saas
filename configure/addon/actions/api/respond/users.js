import { lookup } from 'ember-dependency-lookup';

/**
 * Executes a websocket fetch call for all known Users (enabled or disabled) and returns a Promise.
 *
 * @method getAllUsers
 * @public
 * @returns {Promise}
 */
function getAllUsers() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName: 'users',
    query: {
      filter: [],
      sort: [
        {
          'descending': false,
          'field': 'name'
        }
      ]
    }
  });
}

/**
 * Executes a websocket fetch call for all enabled Users and returns a Promise.
 *
 * @method getAllEnabledUsers
 * @public
 * @returns {Promise}
 */
function getAllEnabledUsers() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName: 'users',
    query: {
      filter: [
        {
          'field': 'status',
          'value': 'enabled'
        }
      ],
      sort: [
        {
          'descending': false,
          'field': 'name'
        }
      ]
    }
  });
}

export default {
  getAllUsers,
  getAllEnabledUsers
};