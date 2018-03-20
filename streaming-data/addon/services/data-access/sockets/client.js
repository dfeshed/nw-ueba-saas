/**
 * @file Client
 * Represents a requested connection (either pending or connected) to a socket server at a given socket URL.
 * Essentially a wrapper for a STOMP client object, adding a Promise-friendly connection API and a subscriptions cache.
 * @public
 */
/* global SockJS */
/* global Stomp */
import { run } from '@ember/runloop';

import EmberObject, { computed } from '@ember/object';
import RSVP from 'rsvp';
import { isEmpty } from '@ember/utils';
import config from 'ember-get-config';
import SubscriptionsCache from './subscriptions-cache';
import { debug } from '@ember/debug';

const MAX_WEBSOCKET_FRAME_SIZE = 64 * 1024;

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
    const hdrs = message.headers;
    const type = hdrs && hdrs['content-type'];

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

/**
 * Utility that returns the receipt callback function for subscriptions. We need to use subscription receipts because
 * there is no guarantee when a SUBSCRIBE and a SEND are sent to a service that the SUBSCRIBE will be processed by the
 * server prior to the SEND. If the SEND request can be processed by the server very quickly, then it's possible that
 * the server will respond to the SEND before it has fully setup the SUBSCRIPTION, which means that the client may never
 * hear the response.
 *
 * Here, when a receipt message is received for a subscription, we lookup the subscription and invoke the promise
 * resolve function that has been placed on the subscription object. This allows the client to only SEND once the
 * subscription receipt has been received.
 * @param subscriptions
 * @returns {function(*=)}
 * @private
 */
function handleSubscriptionReceipt(subscriptions) {
  return (frame = {}) => {
    const headers = frame.headers || {};
    const { destination, id } = headers;

    if (destination && id) {
      // use the destination and the subscription id to lookup the subscription from the subscription-cache
      const sub = subscriptions.find(destination, id, 'id');
      if (sub && sub.receipt) {
        // resolve the promise that will trigger the callbacks for SENDing the request now that the subscription
        // has been confirmed
        sub.receipt.resolve(sub);
      }
    }
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
   * If true, indicates that CONNECT message has been sent but a response has not yet been received.
   * If the STOMP client object already exposed such a property, we wouldn't need this; but it
   * doesn't, so we do.
   * @type {boolean}
   * @public
   */
  isConnecting: false,

  /**
   * Opens a server connection to this instance's `url`.
   * Essentially a wrapper to the STOMP client's `connect` method, but adds a promise API, which resolves with this
   * instance when a CONNECTED message has been received from the socket server.  The promise is also cached in this
   * instance's `promise` attribute, for future reference.
   * This method also sets this instance's `isConnecting` attr to `true` while awaiting the response to the CONNECT message,
   * and then back to `false` if/when the response is received.
   * @returns Promise
   * @public
   */
  connect() {
    const headers = this.headers || {};

    const me = this;
    return this.set('promise',
      new RSVP.Promise(function(resolve, reject) {

        me.set('isConnecting', true);
        me.get('stompClient').connect(headers, function() {
          me.set('isConnecting', false);
          resolve(me);
        }, function(e) {
          me.set('isConnecting', false);
          reject(e);
        });
      })
    );
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
    const me = this;
    if (me.get('disconnected')) {
      return RSVP.Promise.resolve(me);
    } else {
      me.set('disconnected', true);
      return new RSVP.Promise(function(resolve) {
        me.get('stompClient').disconnect(function() {
          resolve(me);
        });
      });
    }
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
    headers = headers || {};
    // ensure that a receipt is included in the subscription headers
    headers.receipt = headers.receipt || `receipt_${destination}`;

    // Previously, we checked here for the requested subscription in the cache.
    // But we are going to stop re-using subscriptions now, so that server requests have
    // a 1-to-1 mapping with subscriptions, which we hope will allow us to re-use
    // a single STOMP client for multiple requests to a microservice.
    const me = this;
    const subs = this.get('subscriptions');
    // STOMP gives us the subscription object.
    const sub = this.get('stompClient').subscribe(destination, _wrapCallback(callback), headers);

    // Create a deferred promise that will be resolved once receipt of the subscription is confirmed. A consumer can then
    // check that the promise has been resolved before sending messages. Otherwise, it is possible that the server
    // could receive messages before it has fully processed the subscription
    sub.receipt = RSVP.defer();
    // We enhance the subscription object with a little extra logic & properties.
    sub.destination = destination;
    sub.send = function(h, b, d) {
      h = h || {};
      h.id = h.id || sub.id;
      me.send(d || this.destination, h, b);
    };
    const unsub = sub.unsubscribe;
    sub.unsubscribe = function() {
      unsub.apply(this, []);
      me.get('subscriptions').remove(this.destination, this);
    };

    subs.add(destination, callback, sub);

    return sub.receipt.promise;
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
    const subscriptions = this.get('subscriptions');

    const url = this.get('url');
    if (!url) {
      throw ('Invalid socket URL for STOMP client connection.');
    }

    const csrfToken = localStorage.getItem('rsa-x-csrf-token');

    if (!isEmpty(csrfToken)) {
      this.headers = {
        'X-CSRF-TOKEN': csrfToken
      };
    }
    const stompClient = Stomp.over(
      new SockJS(url, {}, { transports: ['websocket'] })
    );
    stompClient.onreceipt = handleSubscriptionReceipt(subscriptions);
    stompClient.maxWebSocketFrameSize = MAX_WEBSOCKET_FRAME_SIZE; // change/increase the default max websocket frame size
    stompClient.debug = config.socketDebug ? debug.bind(debug) : null;
    this.set('stompClient', stompClient);

    this.connect();
  }
});
