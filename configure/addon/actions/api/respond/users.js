import { promiseRequest } from 'streaming-data/services/data-access/requests';

export default {
  /**
   * Executes a websocket fetch call for all known Users (enabled or disabled) and returns a Promise.
   *
   * @method getAllUsers
   * @public
   * @returns {Promise}
   */
  getAllUsers() {
    return promiseRequest({
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
  },

  /**
   * Executes a websocket fetch call for all enabled Users and returns a Promise.
   *
   * @method getAllEnabledUsers
   * @public
   * @returns {Promise}
   */
  getAllEnabledUsers() {
    return promiseRequest({
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
};
