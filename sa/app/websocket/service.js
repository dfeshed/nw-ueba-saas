/**
 * @file websocket service
 * Implements a websocket API layer that wraps leverages Stomp over SockJS and returns Promises.
 * Assumes SockJS and Stomp are available as globals.
 * @public
 */
import Ember from 'ember';
import Client from './client';

const {
  Service,
  RSVP
} = Ember;

/**
 * Hash of requested socket server clients. The hash keys are socket URLs. The hash values
 * are arrays, where each array item is an instance of Client class (imported).
 * @type {}
 * @private
 */
let _clients = {};

export default Service.extend({

  /**
   * Requests a client at a given socket server URL.
   * Calling connect again with the same URL will NOT re-use the same client. It will create a new client.
   * We do this (for now, at least) to workaround issues where 2 streams using the same client, and the user cancelled
   * one stream (but not the other), thus disconnecting the client for both streams unintentionally.
   * @param {String} url The server URL.
   * @param {Object} [headers] Optional key-value pairs to be included in the client request's headers.
   * @returns {Promise} A promise that either resolves once the client is made, or rejects if
   * an error occurs. The promise resolves with a Client instance, which can be used to send messages, subscribe,
   * and disconnect.
   * @public
   */
  connect(url, headers) {
    let client = Client.create({ url, headers });
    if (!_clients[url]) {
      _clients[url] = [];
    }
    _clients[url].push(client);
    return client.get('promise');
  },

  /**
   * Requests a disconnect from a given socket server URL.
   * Looks for all clients to the given URL. If found, calls each of their .disconnect() method; otherwise, exits successfully.
   * @param url
   * @returns {Promise} A promise that resolves with the disconnected clients after disconnecting successfully.
   * @public
   */
  disconnect(url) {
    let clients = _clients[url];
    if (clients) {
      delete _clients[url];
      let promises = clients.map((client) => {
        return client.disconnect();
      });
      return RSVP.all(promises).then(() => {
        return clients;
      });
    } else {
      return RSVP.resolve([]);
    }
  }
});
