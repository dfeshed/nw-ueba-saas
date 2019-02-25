import Service from '@ember/service';
import Evented from '@ember/object/evented';
import { Promise } from 'rsvp';
import Channel from './transport/channel';
import * as FLAGS from './transport/nw-flags';
import parseFlags from './transport/parse-flags';
import ENV from 'ngcoreui/config/environment';

/**
 * Connected event. Fired when the websocket is connected.
 *
 * @event transport#connected
 */

/**
 * Close event. Fired when the websocket is closed.
 *
 * @event transport#close
 * @type {CloseEvent}
 * @property {Number} code - Why the websocket was closed. See https://developer.mozilla.org/en-US/docs/Web/API/CloseEvent
 */

/**
 * Error event. Fired when the websocket emits an error.
 *
 * @event transport#error
 * @type {Error}
 */

/**
 * @emits transport#connected
 * @emits transport#close
 * @emits transport#error
 */
export default Service.extend(Evented, {

  /**
   * @private
   * Controls where transport will establish a connection.
   */
  url: null,

  /**
   * @private
   * Once a connection is established, the websocket is stored here.
   */
  ws: null,

  /**
   * @private
   * This stores the previously used tid for establishing channels during the
   * session. When addChannel is called, it is first incremented, and then used.
   */
  lastTid: 0,

  /**
   * @private
   * Stores an object mapping the `.toString()` of a route to a Channel
   * object (see ./transport/channel.js). The route is the same as the route
   * property of the Channel instance. Channels are only placed in here when
   * they are established, and are removed once they are deleted.
   */
  channels: {},

  /**
   * @private
   * Stores an object mapping the `.toString()` of a tid used in the
   * creation of a new channel to a function to call with the new Channel
   * object as the first parameter. Once a channel is established, it is
   * removed from this object.
   */
  pendingChannels: {},

  /**
   * @private
   * Stores an array of messages queued for sending before the WS is fully connected.
   */
  messageQueue: [],

  /**
   * @public
   * The amount of time to wait after a failed reconnect attempt to try again. Doubled
   * each time up to reconnectMaxWaitMs.
   */
  reconnectBaseDelayMs: 500,

  /**
   * The maximum amount of time to wait after a failed reconnection attempt to try again.
   */
  reconnectMaxWaitMs: 30 * 1000,

  /**
   * @private
   * Sets up service variables
   */
  init() {
    // Set the websocket URL based on whether or not this is a production build
    const wsScheme = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    if (ENV.environment === 'production') {
      this.set('url', `${wsScheme}//${window.location.host}/connections/ws`);
    } else {
      // By default on a development build, set this to localhost.
      // If you're developing and want to test the UI on a different endpoint,
      // change this string.
      this.set('url', `${wsScheme}//localhost:50105/connections/ws`);
    }
  },

  /**
   * @public
   */
  connect() {
    const ws = new WebSocket(this.get('url'));
    ws.onopen = () => {
      // Wait until we successfully connect to send close events
      // Otherwise, if the WS isn't available upon initial load of the page, close events are sent
      ws.onclose = (closeEvent) => {
        this.trigger('close', closeEvent);
        this.reconnect();
      };
      this.trigger('connected');
      this.flushQueue();
    };
    ws.onmessage = (messageEvent) => {
      this.handleIncomingMessage(messageEvent);
    };
    ws.onclose = () => {
      this.reconnect();
    };
    ws.onerror = (err) => {
      this.trigger('error', err);
    };
    this.set('ws', ws);
    return this;
  },

  setUrl(url) {
    this.set('url', url);
  },

  /**
   *
   * @param {Number} delayMs - Will wait this long after a failed reconnection attempt to
   *   try to reconnect, doubled each time up to this.reconnectMaxWaitMs
   */
  reconnect(delayMs = this.get('reconnectBaseDelayMs')) {
    // This shouldn't do much other than setting this.ws to null, since we're
    // only calling it from onclose, but do it just to be sure
    this.disconnect().then(() => {
      const ws = new WebSocket(this.get('url'));
      ws.onopen = () => {
        // Wait until we successfully reconnect to start sending close events again
        ws.onclose = (closeEvent) => {
          this.trigger('close', closeEvent);
          this.reconnect();
        };
        this.trigger('connected');
        this.trigger('reconnected');
        // this.flushQueue();
        window.location.reload();
      };
      ws.onmessage = (messageEvent) => {
        this.handleIncomingMessage(messageEvent);
      };
      ws.onclose = () => {
        setTimeout(() => {
          this.reconnect(Math.min(delayMs * 2, this.get('reconnectMaxWaitMs')));
        }, delayMs);
      };
      ws.onerror = (err) => {
        this.trigger('error', err);
      };
      this.set('ws', ws);
    });
  },

  /**
   * @public
   * @returns {Promise}
   * Will disconnect the current websocket, if it is open
   */
  disconnect() {
    return new Promise((resolve) => {
      const ws = this.get('ws');
      if (ws !== null) {
        this.setProperties({
          channels: {},
          pendingChannels: {},
          lastTid: 0
        });
        switch (ws.readyState) {
          case WebSocket.CONNECTING:
          case WebSocket.OPEN:
          case WebSocket.CLOSING:
            {
              ws.onclose = () => {
                resolve();
              };
              ws.close(1000);
            }
            break;
          case WebSocket.CLOSED:
            resolve();
            break;
        }
        this.set('ws', null);
      } else {
        resolve();
      }
    });
  },

  /**
   * @public
   * @param {string} path
   * @param {Object} message
   * @returns {Promise}
   */
  send(path, message) {
    if (!this.assertConnected()) {
      return new Promise((resolve, reject) => {
        this.queue({
          type: 'send',
          path,
          message,
          resolve,
          reject
        });
      });
    } else {
      return this.addChannel(path)
        .then((channel) => {
          return channel.send(message);
        })
        .then(({ channel, message }) => {
          channel.delete();
          // Adding the path to the message object helps us
          return {
            ...message,
            path
          };
        });
    }
  },

  /**
   * @public
   * @param {Object} options
   * @param {string} options.path
   * @param {Object} options.message
   * @param {function} options.messageCallback
   * @param {function} options.errorCallback
   * @returns {function}
   */
  stream(options) {
    const { path, message, messageCallback, errorCallback } = options;
    let { tid } = options;
    // This is the tid that addChannel will use, we want to store it to
    // identify our stream
    tid = tid || this.incrementProperty('lastTid').toString();
    if (!this.assertConnected()) {
      this.queue({ ...options, tid, type: 'stream' });
      return tid;
    } else {
      this.addChannel(path, tid)
        .then((channel) => {
          channel.set('stream', tid);
          channel.send(message, ({ message }) => {
            messageCallback(message);
          }, errorCallback);
        });
    }
    // Return the tid used in the creation of the channel as a handle that
    // can be used to close the channel either before or after it is established
    return tid;
  },

  stopStream(tid) {
    if (!tid) {
      return;
    }
    const pendingChannels = this.get('pendingChannels');
    if (pendingChannels[tid]) {
      pendingChannels[tid] = {
        resolve: (channel) => channel.delete(),
        reject: (channel) => channel.delete()
      };
    } else {
      const channels = Object.values(this.get('channels'));
      for (let i = 0; i < channels.length; i++) {
        const channel = channels[i];
        if (channel.stream === tid) {
          channel.delete();
          return;
        }
      }
    }
  },

  queue(options) {
    this.get('messageQueue').push(options);
  },

  flushQueue() {
    const messages = this.get('messageQueue');
    while (messages.length > 0) {
      const message = messages.shift();
      if (message.type === 'stream') {
        this.stream(message);
      } else if (message.type === 'send') {
        this.send(message.path, message.message)
          .then((value) => {
            message.resolve(value);
          })
          .catch((err) => {
            message.reject(err);
          });
      }
    }
  },

  assertConnected() {
    const ws = this.get('ws');
    if (ws === null || ws.readyState !== WebSocket.OPEN) {
      return false;
    }
    return true;
  },

  /**
   * @private
   * @param {*} message
   */
  sendMessage(message, onError) {
    try {
      this.get('ws').send(JSON.stringify(message));
    } catch (err) {
      onError(`Error encoding outgoing JSON: ${err}`);
    }
  },

  /**
   * @private
   * @param {*} messageEvent
   */
  handleIncomingMessage(messageEvent) {
    // Parse incoming JSON data
    let messageData;
    try {
      messageData = JSON.parse(messageEvent.data);
    } catch (err) {
      // The server should never be sending us invalid JSON
      throw new Error('Server sent invalid JSON');
    }
    const { route } = messageData;
    const flags = parseFlags(messageData.flags);

    // Check to see if it belongs to a channel and pass it on
    if (route) {
      const channel = this.get('channels')[route.toString()];
      if (channel) {
        channel.handleIncomingChannelMessage(messageData);
        return;
      }
    }

    // Deal with incoming messages not destined for channels
    if ((flags.dataType & FLAGS.PARAMS) && messageData.params && messageData.params.tid) {
      this.establishChannel(messageData);
    } else if (flags.error) {
      // ESLint says "These characters are rarely used in JavaScript strings so a regular expression containing these characters is most likely a mistake."
      // and "If you need to use control character pattern matching, then you should turn this rule off."
      // eslint-disable-next-line no-control-regex
      const nodeDoesNotExist = new RegExp("The channel target node '.*' does not exist.*[\\u{0000}-\\u{0002}]+(\\d+)", 'us');
      const result = nodeDoesNotExist.exec(messageData.error.message);
      if (result) {
        const tid = parseInt(result[1], 10);
        this.get('pendingChannels')[tid].reject('Target node does not exist');
        delete this.get('pendingChannels')[tid];
      }
    }
  },

  /**
   * @private
   * @param {string} path
   * @returns {Promise}
   */
  addChannel(path, tid = this.incrementProperty('lastTid').toString()) {
    return new Promise((resolve, reject) => {
      const data = {
        message: 'addChan',
        params: {
          path,
          tid
        }
      };

      // Store the resolve & reject callbacks in the pending channels object
      // Call resolve when we hear back that the channel was created
      this.get('pendingChannels')[tid] = { resolve, reject };
      this.sendMessage(data, reject);
    });
  },

  /**
   * @private
   * @param {*} message
   */
  establishChannel(message) {
    const pendingPromiseResolve = this.get('pendingChannels')[message.params.tid].resolve;
    delete this.get('pendingChannels')[message.params.tid];
    const route = [parseInt(message.params.pid, 10), parseInt(message.params.target, 10)];
    const channel = Channel.create({
      transport: this,
      route
    });
    this.get('channels')[route.toString()] = channel;
    pendingPromiseResolve(channel);
  },

  /**
   * @private
   * @param {*} channel
   */
  unregisterChannel(channel) {
    delete this.get('channels')[channel.route.toString()];
  }
});
