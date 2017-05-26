import Ember from 'ember';
import FilterQuery from 'respond/utils/filter-query';
import { promiseRequest } from 'streaming-data/services/data-access/requests';

const { Object: EmberObject } = Ember;

const UsersAPI = EmberObject.extend({});

UsersAPI.reopenClass({
  /**
   * Executes a websocket fetch call for all known Users (enabled or disabled) and returns a Promise.
   *
   * @method getAllUsers
   * @public
   * @returns {Promise}
   */
  getAllUsers() {
    const query = FilterQuery.create().addSortBy('name', false);
    return promiseRequest({
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
    return promiseRequest({
      method: 'findAll',
      modelName: 'users',
      query: query.toJSON()
    });
  }
});

export default UsersAPI;