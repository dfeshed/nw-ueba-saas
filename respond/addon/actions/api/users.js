import Ember from 'ember';
import { promiseRequest } from 'streaming-data/services/data-access/requests';

const { Object: EmberObject } = Ember;

const UsersAPI = EmberObject.extend({});

UsersAPI.reopenClass({
  /**
   * Executes a websocket fetch call for all Users and returns a Promise.
   *
   * @method getAllUsers
   * @public
   * @returns {Promise}
   */
  getAllUsers() {
    return promiseRequest({
      method: 'findAll',
      modelName: 'users',
      query: {}
    });
  }
});


export default UsersAPI;