/**
 * @file socket
 * Implements a websocket API layer that wraps leverages Stomp over SockJS and returns Promises.
 * @public
 */
import Ember from 'ember';
import Client from './client';
import { Stream } from '../streams';
import config from 'ember-get-config';

const {
  RSVP,
  Logger
} = Ember;

/**
 * Hash of requested socket server clients. The hash keys are socket URLs. The hash values
 * are arrays, where each array item is an instance of Client class (imported).
 * @type {}
 * @private
 */
let _clients = {};

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
function connect(url, headers) {
  let client = Client.create({ url, headers });
  if (!_clients[url]) {
    _clients[url] = [];
  }
  _clients[url].push(client);
  return client.get('promise');
}

/**
 * Requests a disconnect from a given socket server URL.
 * Looks for all clients to the given URL. If found, calls each of their .disconnect() method; otherwise, exits successfully.
 * @param url
 * @returns {Promise} A promise that resolves with the disconnected clients after disconnecting successfully.
 * @public
 */
function disconnect(url) {
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

/**
 * Creates a stream from a given socket query whose response may arrive across multiple socket messages.
 * Returns a Stream instance which can then be started and subscribed to.
 *
 * @param {string} modelName The type of model (i.e., data record) that is being requested. This will be used
 * to look up a corresponding socket configuration in the app's `config\environment.js` file.
 * @param {object} query Arbitrary hash of inputs for the query.
 * @param {object} [streamOptions] Optional hash of configuration properties for the stream constructor.
 * @returns {object} A Stream instance.
 * @public
 */
function createStream(method, modelName, query, streamOptions = {}) {
  const cfg = _findSocketConfig(modelName, method);
  const stream = Stream.create(streamOptions).fromSocket({
    socketConfig: cfg,
    socketRequestParams: query,
    fetchSocketClient: () => connect(cfg.socketUrl)
  });
  return stream;
}

  /**
   * Utility that looks up the socket config for a modelName-method pair.
   *
   * Validates the config exists and that it has all the correct elements,
   * if it does not, throws an error.
   *
   * @param modelName {string}
   * @param method {string}
   * @private
   */
function _findSocketConfig(modelName, method) {
  let modelConfig = ((config.socketRoutes || {})[modelName] || {});
  let cfg = modelConfig[method];

  if (cfg) {
    cfg.socketUrl = modelConfig.socketUrl;
  }

  // no config? eject!
  if (!cfg || !cfg.socketUrl || !cfg.subscriptionDestination || !cfg.requestDestination) {
    const msg = `Invalid socket stream configuration:. model: ${modelName}, method: ${method}`;
    Logger.warn(msg);
    throw new WebsocketConfigurationNotFoundException(msg);
  }

  return cfg;
}

class WebsocketConfigurationNotFoundException extends Error {
  constructor(message) {
    super(message);
    this.message = message;
    this.name = 'WebsocketConfigurationNotFoundException';
  }
}

export default {
  connect,
  disconnect,
  createStream,
  _findSocketConfig // exported for testing purposes
};
