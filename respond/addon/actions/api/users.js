import FilterQuery from 'respond/utils/filter-query';
import { lookup } from 'ember-dependency-lookup';

export default {
  /**
   * Executes a websocket fetch call for all known Users (enabled or disabled) and returns a Promise.
   *
   * @method getAllUsers
   * @public
   * @returns {Promise}
   */
  getAllUsers() {
    const query = FilterQuery.create().addSortBy('name', false);
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'findAll',
      modelName: 'users',
      query: query.toJSON()
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
    const query = FilterQuery.create().addSortBy('name', false).addFilter('status', 'enabled');
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'findAll',
      modelName: 'users',
      query: query.toJSON()
    });
  }
};