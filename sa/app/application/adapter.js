/**
 * @file Application Adapter
 * Enables store queries to use sockets rather than AJAX.
 * This adapter will check the app's config for socket information for the requested modelName.  If found,
 * it will use sockets to do the query; otherwise, it will default to a standard AJAX REST call.
 * @public
 */
import Ember from 'ember';
import RESTAdapter from 'ember-data/adapters/rest';
import config from 'sa/config/environment';

const {
  computed,
  inject: {
    service
  },
  merge
} = Ember;

export default RESTAdapter.extend({

  request: service(),

  // sets the namespace for our api calls
  namespace: 'api',

  csrfKey: config['ember-simple-auth'].csrfLocalstorageKey,

  /* sets the csrf header for all AJAX calls */
  headers: computed(function() {
    const csrfValue = localStorage.getItem(this.get('csrfKey'));
    return {
      'X-CSRF-TOKEN': csrfValue
    };
  }),

  /**
   * Overrides the default findAll method, in order to support using sockets.
   * @see http://emberjs.com/api/data/classes/DS.RESTAdapter.html#method_findAll
   * @public
   */
  findAll(store, type) {
    return this._useSocket('findAll', type) || this._super(...arguments);
  },

  /**
   * Overrides the default query method, in order to support using sockets.
   * @see http://emberjs.com/api/data/classes/DS.RESTAdapter.html#method_query
   * @public
   */
  query(store, type, query) {
    return this._useSocket('query', type, query) || this._super(...arguments);
  },

  /**
   * Overrides the default query method, in order to support using sockets.
   * @see http://emberjs.com/api/data/classes/DS.RESTAdapter.html#method_queryRecord
   * @public
   */
  queryRecord(store, type, query) {
    return this._useSocket('queryRecord', type, query) || this._super(...arguments);
  },

  /**
   * Overrides the default findRecord method, in order to support using sockets.
   * @see http://emberjs.com/api/data/classes/DS.RESTAdapter.html#method_findRecord
   * @public
   */
  findRecord(store, type, id, snapshot) {
    return this._useSocket('findRecord', type, null, id, snapshot) || this._super(...arguments);
  },

  /**
   * Overrides the default updateRecord method, in order to support using sockets.
   * @see http://emberjs.com/api/data/classes/DS.RESTAdapter.html#method_updateRecord
   * Overrides the adapters updateRecord method.
   * @param {DS.Store} store
   * @param {DS.Model} type
   * @param {DS.Snapshot} snapshot
   * @param {Object} query - hash of socket request params
   * @public
   */
  updateRecord(store, type, snapshot, query = null) {
    return this._useSocket('updateRecord', type, query, null, snapshot) || this._super(...arguments);
  },


  /**
   * Overrides the default deleteRecord method, in order to support using sockets.
   * @see http://emberjs.com/api/data/classes/DS.RESTAdapter.html#method_deleteRecord
   * @public
   * Overrides the adapters deleteRecord method.
   * @param {DS.Store} store
   * @param {DS.Model} type
   * @param {DS.Snapshot} snapshot
   * @param {Object} query - hash of socket request params
   */
  deleteRecord(store, type, snapshot, query = null) {
    return this._useSocket('deleteRecord', type, query, null, snapshot) || this._super(...arguments);
  },

  /**
   * Overrides the default createRecord method, in order to support using sockets.
   * @see http://emberjs.com/api/data/classes/DS.RESTAdapter.html#method_createRecord
   * @public
   * Overrides the adapters createRecord method.
   * @param {DS.Store} store
   * @param {DS.Model} type
   * @param {DS.Snapshot} snapshot
   */
  createRecord(store, type, snapshot, query = null) {
    return this._useSocket('createRecord', type, query, null, snapshot) || this._super(...arguments);
  },

  /**
   * Will attempt to simulate an AJAX REST query but submits the query over a socket.
   * Returns a promise that resolves with the (first and only the first) socket response that comes back.
   * Note that, as in AJAX REST, only one response message is expected. A Promise cannot be resolved multiple times.
   * If you expect that there may be multiple socket messages, use `request.streamRequest()` instead.
   *
   * Also note that Ember Data will auomatically pass the resolved value of this Promise to the application adapter's
   * `normalizeResponse` method, whose job is to convert the JSON into a JSON API document structure.
   * In SA, a successful response is expected to be structured like `{ code: 0, data: .., meta: .. }`, which does look
   * somewhat like a JSON API document, but the object(s) in inside `data` may need to be modified.
   *
   * If a run-time error is caught, returns null. Typically a run-time error would only be thrown if there is no config
   * defined in `sa/config/environment.js` for the requested modelName-method pair.
   *
   * @returns {Promise} A promise that resolves with the socket response if successful, or rejects if it errors.
   * @private
   */
  _useSocket(method, type, query, id, snapshot) {
    query = query || (id && { id }) || (snapshot && { id: snapshot.id });
    query = merge({}, query);

    const requestInputs = {
      method,
      modelName: type.modelName,
      query
    };

    let promiseReturn = null;
    try {
      promiseReturn = this.get('request').promiseRequest(requestInputs);
    } catch (err) {
      // do nothing, let promiseReturn remain null
    }

    return promiseReturn;
  }

});
