/**
 * @file socket
 * Implements a websocket API layer that wraps leverages Stomp over SockJS and returns Promises.
 * @public
 */
import RSVP from 'rsvp';

import Ember from 'ember';
import Client from './client';
import { Stream } from '../streams';
import config from 'ember-get-config';

const {
  Logger
} = Ember;

/**
 * Hash of requested socket server clients. The hash keys are socket URLs. The hash values
 * are arrays, where each array item is an instance of Client class (imported).
 * @type {}
 * @private
 */
const _clients = {};

/**
 * Requests a client at a given socket server URL.
 * Calling connect again with the same URL WILL re-use the same client. In that case, the given `headers` argument
 * is ignored and the original client's headers are used instead.
 * Avoids re-using prior client if that client is both neither connected nor awaiting a connection.
 * That can happen if an error on the server has collapsed a previously open connection.
 * @param {String} url The server URL.
 * @param {Object} [headers] Optional key-value pairs to be included in the client request's headers.
 * @returns {Promise} A promise that either resolves once the client is made, or rejects if
 * an error occurs. The promise resolves with a Client instance, which can be used to send messages, subscribe,
 * and disconnect.
 * @public
 */
function connect(url, headers) {
  let client = _clients[url];
  const hasStomp = client && client.stompClient;
  const hasStompConnection = hasStomp && client.stompClient.connected;
  const awaitsStompConnection = hasStomp && client.get('isConnecting');

  if (!hasStompConnection && !awaitsStompConnection) {
    client = Client.create({ url, headers });
    _clients[url] = client;
  }
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
  const client = _clients[url];
  if (client) {
    delete _clients[url];
    return client.disconnect();
  } else {
    return RSVP.resolve([]);
  }
}

/**
 * Requests a disconnect from all the registered socket server URLs.
 * @public
 */
function disconnectAll() {
  Object.keys(_clients).forEach(disconnect);
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
  const modelConfig = ((config.socketRoutes || {})[modelName] || {});
  const cfg = modelConfig[method];

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
  disconnectAll,
  createStream,
  _findSocketConfig // exported for testing purposes
};
