import Ember from 'ember';
import DS from 'ember-data';
import Stream from 'sa/utils/stream/base';

export default DS.Store.extend({

  websocket: Ember.inject.service(),

  /**
   * Creates a stream from a given socket query whose response may arrive across multiple socket messages.
   * Returns a Stream instance which can then be subscribed to in order to listen for responses.
   *
   * Does not start the query yet; that can be done by calling methods on the returned stream object.
   * Does not push any of the responses to the store's cache, nor does it instantiate Models for the responses.
   * Any caching, modeling or other processing that is desired for the responses can be handled by subscribers
   * to the stream.
   *
   * This method is a lightweight alternative to the out of the box methods from Ember.  Those methods, such as
   * `query`, `findRecord`, etc, go through Ember Data Adapters and through the store cache. This method essentially
   * by-passes those layers and gives the caller direct access to the (unmodified) JSON responses from the server.
   *
   * Additionally, this method is different from all the Ember store methods in that it supports multiple responses.
   * In order to do that, it avoids using Promises, since Promises can only be resolved once per lifetime. Instead
   * of Promises, this method leverages the Stream utility class.
   *
   * To use this method, a developer simply needs to add socket config properties in the `environment.js` file
   * under `socketRoutes[modelType].stream`.
   *
   * @param {string} modelName The type of model (i.e., data record) that is being requested. This will be used
   * to look up a corresponding socket configuration in the app's `config\environment.js` file.
   * @param {object} query Arbitrary hash of inputs for the query.
   * @param {object} [streamOptions] Optional hash of configuration properties for the stream constructor.
   * @returns {object} A Stream instance.
   * @public
   */
  stream(modelName, query, streamOptions) {
    return Stream.create(streamOptions || {}).fromSocket({
      websocket: this.get('websocket'),
      socketConfigType: { modelName, method: 'stream' },
      socketRequestParams: query
    });
  },

  notify(modelName, query, streamOptions) {
    return Stream.create(streamOptions || {}).fromSocket({
      websocket: this.get('websocket'),
      socketConfigType: { modelName, method: 'notify' },
      socketRequestParams: query
    });
  }
});
