/**
 * @file Client
 * Represents a requested connection (either pending or connected) to a socket server at a given socket URL.
 * Essentially a wrapper for a STOMP client object, adding a Promise-friendly connection API and a subscriptions cache.
 * @public
 */
/* global SockJS */
/* global Stomp */
import Ember from 'ember';
import config from 'sa/config/environment';
import SubscriptionsCache from './subscriptions-cache';

const {
  run,
  Object: EmberObject,
  computed,
  RSVP,
  Logger
} = Ember;

/**
 * Utility that determines whether or not a received socket message needs to have its body parsed from a JSON string to
 * javascript variable.  This is done by first checking if there is a message.headers.content-type; if so, only
 * content-type equal to 'application/JSON' will be parsed. If there is no content-type header, we will try
 * to parse any message body of type String.
 * @param {Object} message A STOMP message frame.
 * @returns {boolean}
 * @private
 */
function _shouldParseBody(message) {
  if (message) {
    let hdrs = message.headers;
    let type = hdrs && hdrs['content-type'];

    return type ?
      (type.indexOf('application/json') >= 0) :
      (typeof message.body === 'string');
  }
  return false;
}

/**
 * Utility that wraps the given callback with a JSON parser so callback can receive objects, not JSON strings.
 * Note that in some cases the JSON parsing may fail (e.g., the content-type header is missing so we try
 * to parse the body string anyway); in those cases, the original message body is still preserved.
 * @param {Function} callback An onmessage handler that responds to STOMP messages.
 * @returns {Function} The wrapped message handler.
 * @private
 */
function _wrapCallback(callback) {
  return function(message) {
    run(() => {
      if (_shouldParseBody(message) && message.body) {
        try {
          message.body = JSON.parse(message.body);
        } catch (e) {
          // do nothing
        }
      }
      return callback(message);
    });
  };
}

export default EmberObject.extend({

  /**
   * The requested socket server URL.
   * @type string
   * @public
   */
  url: '',

  /**
   * Optional set of headers for the CONNECT message to the socket server.
   * @type {}
   * @public
   */
  headers: null,

  /**
   * The STOMP client object for sending messages via this connection.
   * For STOMP client object's API, @see http://jmesnil.net/stomp-websocket/doc/
   * @type Object
   * @public
   */
  stompClient: null,

  /**
   * Cache of subscriptions currently open on this connection. Each hash key is a subscription destination;
   * each hash value is an array. The array's items are objects (POJOs) with the properties {callback, sub}, where
   * `callback` is a reference to the callback function that was given for the requested subscription, and
   * `sub` is the resultant subscription object provided by the STOMP client for that destination+callback.
   * @type {}
   * @public
   */
  subscriptions: computed(function() {
    return SubscriptionsCache.create();
  }),

  /**
   * A Promise that resolves when the connection has been established (i.e., a CONNECTED message has been received
   * from the socket server). Resolves with this instance.
   * @type Promise
   * @public
   */
  promise: null,

  /**
   * If true, indicates that `disconnect()` has been called on this client, and therefore it should not be asked
   * to send any subsequent messages to the server.
   * @type {boolean}
   * @private
   */
  disconnected: false,

  /**
   * Opens a server connection to this instance's `url`.
   * Essentially a wrapper to the STOMP client's `connect` method, but adds a promise API, which resolves with this
   * instance when a CONNECTED message has been received from the socket server.  The promise is also cached in this
   * instance's `promise` attribute, for future reference.
   * @returns Promise
   * @public
   */
  connect() {
    let headers = this.headers || {};

    let accessToken = localStorage.getItem('rsa-oauth2-jwt-access-token');

    headers.Upgrade = 'websocket';
    headers.Authorization = `Bearer ${accessToken}`;
    let me = this;
    return this.set('promise',
      new RSVP.Promise(function(resolve, reject) {
        me.get('stompClient').connect(headers, function() {
          resolve(me);
        }, reject);
      }));
  },

  /**
   * Closes the existing server connection to a given URL, if it exists; if none exists, completes without error.
   * Essentially a wrapper to the STOMP client's `disconnect` method, but adds a promise API.
   * This function also marks the `disconnected` property of this instance as `true`, which allows the service to
   * know that this client is now useless and should not be reused from the service's internal cache.
   * @returns {Promise} A promise that resolves after the disconnect call completes. Resolves with this instance.
   * @public
   */
  disconnect() {
    let me = this;
    me.set('disconnected', true);
    return new RSVP.Promise(function(resolve) {
      me.get('stompClient').disconnect(function() {
        resolve(me);
      });
    });
  },

  /**
   * Sends a message to a given destination. Serializes the body into JSON if it is an Object or Array.
   * Throws an error if called before the connection is ready.
   * @param {String} destination The destination which the message corresponds to; written in path format.
   * @param {Object} [headers] Optional key-value pairs to be included in the message's headers.
   * @param {Object|Array|String} [body] Optional message contents; typically an Object or Array. Will be serialized.
   * @returns {object} An object with the given destination, headers, body.
   * @public
   */
  send(destination, headers, body) {
    headers = headers || {};
    switch (typeof body) {
      case 'object':
        body = JSON.stringify(body);
        break;
      case 'undefined':
        body = '';
        break;
    }

    this.get('stompClient').send(destination, headers, body);
    return { destination, headers, body };
  },

  /**
   * Subscribes a callback for messages from a given destination.
   * Throws an error if called before the connection is ready.
   * @param {String} destination The destination for which the callback would like to be notified.
   * @param {Function} callback The function to be notified. The callback will receive a single 'message' argument,
   * which is a STOMP Frame object with the following properties:
   * (1) 'command': {String} the name of the frame (e.g., 'CONNECT', 'SEND', etc);
   * (2) 'headers': {Object} the message headers, a hash of key-value pairs, possibly empty; and
   * (3) 'body': {String|*} the message contents, possibly missing.
   * If the message headers.contentType equals 'JSON', then the body will be parsed as a JSON string into
   * a javascript variable (Object|Array|String|Number|etc).
   * @param {Object} [headers] Optional key-value pairs to be included in the subscribe request's headers.
   * @returns {Promise} A promise that either resolves with a subscription object, if successful; or rejects if an
   * error occurs. The subscription object has the following properties:
   * (1) 'id': an auto-generated ID for this subscription;
   * (2) 'destination': the destination that given to the subscribe() call;
   * (3) 'unsubscribe()': a method for cancelling this subscription;
   * (4) 'send(header, body, [destination])': a helper method for sending a message for this subscription.
   * The subscription's send() helper is an alternative to using this service's send(). It has two benefits:
   * (i) it will automatically submit the subscription's ID in the headers for you (unless your override the id
   * by providing it in the input param); and
   * (ii) it will automatically default to the subscription's destination (unless you override the destination
   * by providing it in the input param).
   * @public
   */
  subscribe(destination, callback, headers) {

    // Check for the requested subscription in the cache.
    let me = this;
    let subs = this.get('subscriptions');
    let sub = subs.find(destination, callback);

    // We don't have this subscription cached, so create it and cache it now.
    if (!sub) {

      // STOMP gives us the subscription object.
      sub = this.get('stompClient').subscribe(destination, _wrapCallback(callback), headers || {});

      // We enhance the subscription object with a little extra logic & properties.
      sub.destination = destination;
      sub.send = function(h, b, d) {
        h = h || {};
        h.id = h.id || sub.id;
        me.send(d || this.destination, h, b);
      };
      let unsub = sub.unsubscribe;
      sub.unsubscribe = function() {
        unsub.apply(this, []);
        me.get('subscriptions').remove(this.destination, this);
      };

      subs.add(destination, callback, sub);
    }
    return sub;
  },

  /**
   * Attempts to establish a connection to the socket server at the given URL:
   * (1) Sets this.stompClient to a new STOMP client for the URL; then
   * (2) Submits a CONNECT message over STOMP client.
   * After the CONNECTED response is received, this instance can used to send messages, subscribe, and disconnect.
   * @public
   */
  init() {
    this._super(...arguments);

    let url = this.get('url');
    if (!url) {
      throw ('Invalid socket URL for STOMP client connection.');
    }

    let stompClient = Stomp.over(
      new SockJS(url, {}, { protocols_whitelist: ['websocket'] })
    );
    stompClient.debug = config.socketDebug ? Logger.debug.bind(Logger) : null;
    this.set('stompClient', stompClient);

    this.connect();
  }
});
