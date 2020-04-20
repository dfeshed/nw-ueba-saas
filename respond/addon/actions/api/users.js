import FilterQuery from 'respond-shared/utils/filter-query';
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
  }
};