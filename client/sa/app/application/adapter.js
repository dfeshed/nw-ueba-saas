/**
 * @file Application Adapter
 * Enables store queries to use sockets rather than AJAX.
 * This adapter will check the app's config for socket information for the requested modelName.  If found,
 * it will use sockets to do the query; otherwise, it will default to a standard AJAX REST call.
 * @public
 */
import Ember from 'ember';
import DS from 'ember-data';
import config from 'sa/config/environment';
import Stream from 'sa/utils/stream/base';

export default DS.RESTAdapter.extend({

  // sets the namespace for our api calls
  namespace: 'api',

  csrfKey: config['ember-simple-auth'].csrfLocalstorageKey,

  /* sets the csrf header for all AJAX calls */
  headers: Ember.computed(function() {
    let csrfValue = localStorage.getItem(this.get('csrfKey'));
    return {
      'X-CSRF-TOKEN': csrfValue
    };
  }),

  websocket: Ember.inject.service(),

  /**
   * Overrides the default findAll method, in order to support using sockets.
   * @see http://emberjs.com/api/data/classes/DS.RESTAdapter.html#method_findAll
   * @public
   */
  findAll(store, type) {
    return this._trySocket('findAll', store, type) || this._super(...arguments);
  },

  /**
   * Overrides the default query method, in order to support using sockets.
   * @see http://emberjs.com/api/data/classes/DS.RESTAdapter.html#method_query
   * @public
   */
  query(store, type, query) {
    return this._trySocket('query', store, type, query) || this._super(...arguments);
  },

  /**
   * Overrides the default query method, in order to support using sockets.
   * @see http://emberjs.com/api/data/classes/DS.RESTAdapter.html#method_queryRecord
   * @public
   */
  queryRecord(store, type, query) {
    return this._trySocket('queryRecord', store, type, query) || this._super(...arguments);
  },

  /**
   * Overrides the default findRecord method, in order to support using sockets.
   * @see http://emberjs.com/api/data/classes/DS.RESTAdapter.html#method_findRecord
   * @public
   */
  findRecord(store, type, id, snapshot) {
    return this._trySocket('findRecord', store, type, null, id, snapshot) || this._super(...arguments);
  },

  /**
   * Overrides the default updateRecord method, in order to support using sockets.
   * @see http://emberjs.com/api/data/classes/DS.RESTAdapter.html#method_updateRecord
   * @public
   * Overrides the adapters updateRecord method.
   */
  updateRecord(store, type, snapshot) {
    return this._trySocket('updateRecord', store, type, null, null, snapshot) || this._super(...arguments);
  },

  /**
   * Tries to use a socket. If successful, returns the socket promise; otherwise if a run-time error is caught, returns null.
   * Typically a run-time error would only be thrown if there is no config defined in `sa/config/environment.js` for
   * the requested modelName-method pair.
   * @param {string} method One of: 'query', 'queryRecord', 'findRecord', 'findAll', 'createRecord', 'updateRecord', 'deleteRecord'.
   * @returns {*}
   * @private
   */
  _trySocket(method, store, type, query, id, snapshot) {
    try {
      return this._useSocket(method, store, type, query, id, snapshot);
    } catch(e) {
      return null;
    }
  },

  /**
   * Simulates an AJAX REST query but submits the query over a socket.
   * Returns a promise that resolves with the (first and only the first) socket response that comes back.
   * Note that, as in AJAX REST, only one response message is expected. A Promise cannot be resolved multiple times.
   * If you expect that there may be multiple socket messages, use `store.stream()` instead.
   * Also note that Ember Data will auomatically pass the resolved value of this Promise to the application adapter's
   * `normalizeResponse` method, whose job is to convert the JSON into a JSON API document structure.
   * In SA, a successful response is expected to be structured like `{ code: 0, data: .., meta: .. }`, which does look
   * somewhat like a JSON API document, but the object(s) in inside `data` may need to be modified.
   * @returns {Promise} A promise that resolves with the socket response if successful, or rejects if it errors.
   * @private
   */
  _useSocket(method, store, type, query, id, snapshot) {
    query = query || (id && { id }) || (snapshot && { id: snapshot.id });

    // Set up a stream to fetch the data over socket.
    let stream = Stream.create({}).fromSocket({
      websocket: this.get('websocket'),
      socketConfigType: { modelName: type.modelName, method },
      socketRequestParams: Ember.merge({}, query)
    }).autoStart();

    // To start the stream, subscribe to it. Return a Promise wrapper to it.
    return new Ember.RSVP.Promise(function(resolve, reject) {
      stream.subscribe(resolve, reject);
    });
  }
});
