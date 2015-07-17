/**
 * @file websocket service
 * Implements a websocket API layer that wraps leverages Stomp over SockJS and returns Promises.
 * Assumes SockJS and Stomp are available as globals.
 */
/* global SockJS */
/* global Stomp */
import Ember from "ember";
import config from "../config/environment";

/**
 * Promise that resolves when socket server connection is successful. This promise is created
 * by calling the connect() method. Calls to send() and subscribe() will piggy back on top of this promise.
 * because they must wait for the connection to be established first. Calling disconnect() will
 * destroy this promise.
 * @type Promise
 * @default null
 * @private
 */
var _connectionPromise,

    /**
     * Local stomp connection API that is established over SockJS. Established by calling connect().
     * Used for communications in send(), subscribe() & disconnect().
     * @type Object
     * @private
     */
    _stompClient;

/**
 * Determines whether or not a received socket message needs to have its body parsed from a JSON string to
 * javascript variable.  This is done by first checking if there is a message.headers.content-type; if so, only
 * content-type equal to "application/JSON" will be parsed. If there is no content-type header, we will try
 * to parse any message body of type String.
 * @param {Object} message A STOMP message frame.
 * @returns {boolean}
 * @private
 */
function _shouldParseBody(message){
    if (message) {
        var hdrs = message.headers,
            type = hdrs && hdrs["content-type"];
        return type?
            (type === "application/json") :
            (typeof message.body === "string");
    }
    return false;
}

/**
 * Wraps the given callback with a JSON parser so callback can receive objects, not JSON strings.
 * Note that in some cases the JSON parsing may fail (e.g., the content-type header is missing so we try
 * to parse the body string anyway); in those cases, the original message body is still preserved.
 * @param {Function} callback An onmessage handler that responds to STOMP messages.
 * @returns {Function} The wrapped message handler.
 * @private
 */
function _wrapCallback(callback) {
    return function(message){
        if (_shouldParseBody(message) && message.body) {
            try {
                message.body = JSON.parse(message.body);
            }
            catch(e) {}
        }
        return callback(message);
    };
}

export default Ember.Service.extend({

    /**
     * Attempts to establish a server connection. The server URL is read from a default, which can
     * be overridden in the config/environment settings under "socketURL". If connect is called
     * a second time, the first connection is re-used.
     * @param {Object} [headers] Optional key-value pairs to be included in the connection request's headers.
     * @returns {Promise} A promise that either resolves once the connection is made, or rejects if
     * an error occurs.
     */
    connect: function(headers) {
        if (!_connectionPromise) {
            _connectionPromise = new Ember.RSVP.Promise(function(resolve, reject){
                _stompClient = Stomp.over(new SockJS(config.socketURL));
                _stompClient.debug = null;  // to disable stomp library's debug messages in console
                _stompClient.connect(headers || {}, resolve, reject);
            });
        }
        return _connectionPromise;
    },

    /**
     * Closes the existing server connection, if any. If there is no connection, the call completes without error.
     * @returns {Promise} A promise that resolves after the disconnect call completes.
     */
    disconnect: function() {
        return new Ember.RSVP.Promise(function(resolve){
            if (_connectionPromise) {

                // We are connected, so disconnect and clear caches to connection.
                _stompClient.disconnect(resolve);
                _stompClient = null;
                _connectionPromise = null;
            }
            else {

                // We don't have a connection; complete without error.
                resolve();
            }
        });
    },

    /**
     * Sends a message to a given destination. Waits for the connection to be completed before
     * sending the message. Serializes the body into JSON if it is an Object or Array.
     * @param {String} destination The destination which the message corresponds to; written in path format.
     * @param {Object} [headers] Optional key-value pairs to be included in the message's headers.
     * @param {Object|Array|String} [body] Optional message contents; typically an Object or Array. Will be serialized.
     * @returns {Promise} A promise that resolves once the message is sent (but does NOT wait for it to be received).
     */
    send: function(destination, headers, body) {
        switch (typeof body){
            case "object":
                body = JSON.stringify(body);
                break;
            case "undefined":
                body = "";
                break;
        }
        return this.connect().then(function() {
            return _stompClient.send(destination, headers || {}, body);
        });
    },

    /**
     * Subscribes a callback for messages from a given destination.  Waits for the connection to be completed
     * before sending the subscription request.
     * @param {String} destination The destination for which the callback would like to be notified.
     * @param {Function} callback The function to be notified. The callback will receive a single "message" argument,
     * which is a STOMP Frame object with the following properties:
     * (1) "command": {String} the name of the frame (e.g., "CONNECT", "SEND", etc);
     * (2) "headers": {Object} the message headers, a hash of key-value pairs, possibly empty; and
     * (3) "body": {String|*} the message contents, possibly missing.
     * If the message headers.contentType equals "JSON", then the body will be parsed as a JSON string into
     * a javascript variable (Object|Array|String|Number|etc).
     * @param {Object} [headers] Optional key-value pairs to be included in the subscribe request's headers.
     * @returns {Promise} A promise that either resolves with a subscription object, if successful; or rejects if an
     * error occurs. The subscription object has the following properties:
     * (1) "id": an auto-generated ID for this subscription;
     * (2) "destination": the destination that given to the subscribe() call;
     * (3) "unsubscribe()": a method for cancelling this subscription;
     * (4) "send(header, body, [destination])": a helper method for sending a message for this subscription.
     * The subscription's send() helper is an alternative to using this service's send(). It has two benefits:
     * (i) it will automatically submit the subscription's ID in the headers for you (unless your override the id
     * by providing it in the input param); and
     * (ii) it will automatically default to the subscription's destination (unless you override the destination
     * by providing it in the input param).
     */
    subscribe: function(destination, callback, headers) {
        var me = this;
        return this.connect().then(function() {
            var subscription = _stompClient.subscribe(destination, _wrapCallback(callback), headers || {});
            subscription.destination = destination;
            subscription.send = function (h, b, d) {
                h = h || {};
                h.id = h.id || subscription.id;
                return me.send(d || this.destination, h, b);
            };
            return subscription;
        });
    }
});
