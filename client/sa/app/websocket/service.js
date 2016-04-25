/**
 * @file websocket service
 * Implements a websocket API layer that wraps leverages Stomp over SockJS and returns Promises.
 * Assumes SockJS and Stomp are available as globals.
 * @public
 */
import Ember from 'ember';
import Client from './client';

/**
 * Hash of requested socket server clients. The hash keys are socket URLs. The hash values
 * are instances of Client class (see below).
 * @type {}
 * @private
 */
let _clients = {};

export default Ember.Service.extend({

  /**
   * Requests a client at a given socket server URL.
   * Calling connect again with the same URL will re-use the same client, until that client is
   * explicitly terminated by calling the client object's .disconnect().
   * Note: if this method is called for the same URL twice, the second call's headers param will be ignored.
   * @param {String} url The server URL.
   * @param {Object} [headers] Optional key-value pairs to be included in the client request's headers.
   * @returns {Promise} A promise that either resolves once the client is made, or rejects if
   * an error occurs. The promise resolves with a Client instance, which can be used to send messages, subscribe,
   * and disconnect.
   * @public
   */
  connect(url, headers) {
    let client = _clients[url];
    if (!client || client.get('disconnected')) {
      client = _clients[url] = Client.create({ url, headers });
    }
    return client.get('promise');
  },

  /**
   * Requests a disconnect from a given socket server URL.
   * Looks for a client to the given URL. If found, calls its .disconnect() method; otherwise, exits successfully.
   * @param url
   * @returns {Promise} A promise that resolves with the disconnected client after disconnecting successfully.
   * @public
   */
  disconnect(url) {
    let client = _clients[url];
    if (client) {
      delete _clients[url];
      return client.disconnect();
    } else {
      return new Ember.RSVP.Promise(function(resolve) {
        resolve(client);
      });
    }
  }
});
