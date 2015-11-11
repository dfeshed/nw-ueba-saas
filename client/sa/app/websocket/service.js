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
 * Hash of Promises for existing socket server connections. The hash keys are socket URLs. The hash values
 * are each a Promise, which resolves when the connection to the corresponding socket URL is successful.
 * Each promise is created by calling the service's connect() method. Each promise resolves with a reference
 * to a STOMP client object for that socket URL. Calling the client's disconnect() will destroy this promise.
 * @type Promise{}
 * @default null
 * @private
 */
var _connectionPromises = {},

    /**
     * Hash of STOMP connection APIs that are established by calling connect().
     * The hash keys are server URLs; the hash values are stomp client instances.
     * Used for communications in send(), subscribe() & disconnect().
     * @type Object{}
     * @private
     */
    _stompClients = {},

    /**
     * Hash of subscription objects that are established by calling subscribe().
     * The hash keys are sever URLs, the hash values are nested hashes. The nested hashes' keys are
     * destinations; the nested hashes' values are subscription objects.
     * @type Object{}
     * @private
     */
    _subscriptions = {},

    /**
     * Hash of arrays that are going to be populated by the results from on-going stream requests.
     * The hash keys are request ids, the hash values are arrays.  These arrays stay in the cache until
     * they are done streaming (i.e., an error occurs, or the stream has hit its limit, or all records have
     * arrived in the stream).
     * @type {{}}
     * @private
     */
    _streamResults = {},

    /**
     * Hashes of resolve() and reject() functions, respectively, for Promises that were kicked off for on-going
     * stream requests.
     * The hash keys are request ids, the hash values are functions.  These functions stay in the cache until
     * they are done streaming (i.e., an error occurs, or the stream has hit its limit, or all records have
     * arrived in the stream).
     * @type {{}}
     * @private
     */
    _streamResolves = {},
    _streamRejects = {},

    /**
     * Counter used for auto-generating request ids.
     * @type {number}
     * @private
     */
    _requestCounter = 0;

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
            (type.indexOf("application/json") >= 0) :
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

function _teardownStream(id) {
    delete _streamResults[id];
    delete _streamRejects[id];
    delete _streamResolves[id];
}

/**
 * Callback for handling responses from a stream request.
 * Pushes the response data into the corresponding results array for that request, and resolves the corresponding
 * promise for that request too.  Unless the response has an error code, in which case, rejects the promise.
 * In order to access the corresponding results array, resolve function & reject function, this function relies
 * on private caches of those things. These caches are populated at request time, and are then emptied out after
 * the stream is done.
 * This method assumes that:
 * (1) it is harmless to call resolve() on a promise repeatedly, if its data stream comes back in multiple batches;
 * (2) a promise should be resolved as soon as its first batch successfully returns;
 * (3) if subsequent batches have an error code, we call reject on that promise which was already resolved earlier,
 * and this might be ignored by the promise, but that's okay because (i) this is an unlikely scenario and (ii) we
 * also update the array object's "errorCode" attribute in this scenario anyway.
 * @param {object} message The STOMP message with a response for a data stream.
 * @private
 */
function _onStreamMessage(message) {

    // Validate the request id.
    var response = message && message.body,
        request = response && response.request,
        id = request && request.id,
        results = _streamResults[id];
    if (!results) {
        console.warn("Stream response with unexpected request id. Discarding it.", response);
        return;
    }


    if (response.code !== 0) {
        results.setProperties({
            isStreaming: false,
            errorCode: response.code
        });
        _streamRejects[id](response);
        _teardownStream(id);
    }
    else {

        // Update the results contents and meta.
        results.pushObjects(response.data);
        var count = results.get("length"),
            total = response.meta && response.meta.total,
            progress = total ? parseInt(100 * count / total, 10) : 100;
        results.setProperties({
            total: total,
            progress: progress,
            isStreaming: progress < 100
        });
        _streamResolves[id](results);
        if (progress >= 100) {
            _teardownStream(id);
        }
    }
}

export default Ember.Service.extend({

    /**
     * Attempts to establish a server connection at a given server URL.
     * Submits a CONNECT message over STOMP, and returns a Promise which will be resolved when the corresponding
     * CONNECTED response is received.  Once connected, a STOMP client instance for that URL is cached for later use.
     * Indeed, the Promise will resolve with a reference to the STOMP client.
     * Calling connect again with the same URL will re-use the cached STOMP client, until that client's connection is
     * explicitly terminated by calling this.disconnect(url).
     * @param {String} url The server URL.
     * @param {Object} [headers] Optional key-value pairs to be included in the connection request's headers.
     * @returns {Promise} A promise that either resolves once the connection is made, or rejects if
     * an error occurs.
     */
    connect: function(url, headers) {
        var promise = _connectionPromises[url];
        if (!promise) {

            // We don't have a promise cached for this url, so request a new connection.
            promise = _connectionPromises[url] = new Ember.RSVP.Promise(function(resolve, reject) {
                var client = _stompClients[url] = Stomp.over(
                        new SockJS(url, {}, { protocols_whitelist: ['websocket'] })
                    ),
                    success = function(){
                        resolve(client);
                    },
                    fail = function(){
                        reject.apply(null, arguments || []);
                        delete _stompClients[url];
                    };
                client.debug = config.socketDebug ? console.debug.bind(console) : null;
                headers = headers || {};
                headers['X-CSRF-TOKEN'] = localStorage.getItem("rsa-x-csrf-token");
                client.connect(headers, success, fail);
            });
        }
        return promise;
    },

    /**
     * Closes the existing server connection to a given URL, if any; if none existing, completes without error.
     * @returns {Promise} A promise that resolves after the disconnect call completes.
     */
    disconnect: function(url) {
        return new Ember.RSVP.Promise(function(resolve){
            var client = _stompClients[url];
            if (client) {

                // We are connected, so disconnect and clear caches to connection.
                client.disconnect(resolve);
                delete _stompClients[url];
                delete _connectionPromises[url];
                delete _subscriptions[url];
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
     * @param {String} url The server URL.
     * @param {String} destination The destination which the message corresponds to; written in path format.
     * @param {Object} [headers] Optional key-value pairs to be included in the message's headers.
     * @param {Object|Array|String} [body] Optional message contents; typically an Object or Array. Will be serialized.
     * @returns {Promise} A promise that resolves once the message is sent (but does NOT wait for it to be received).
     */
    send: function(url, destination, headers, body) {
        switch (typeof body){
            case "object":
                body = JSON.stringify(body);
                break;
            case "undefined":
                body = "";
                break;
        }
        return this.connect(url).then(function(client) {
            return client.send(destination, headers || {}, body);
        });
    },

    /**
     * Subscribes a callback for messages from a given destination.  Waits for the connection to be completed
     * before sending the subscription request.
     * @param {String} url The server URL.
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
    subscribe: function(url, destination, callback, headers) {
        var subs = _subscriptions[url] = _subscriptions[url] || {},
            sub = subs[destination];
        if (!sub) {

            // We don't have this subscription cached, so create it now.
            var me = this;
            return this.connect(url).then(function(client) {
                var subscription = client.subscribe(destination, _wrapCallback(callback), headers || {});
                subscription.destination = destination;
                subscription.send = function (h, b, d) {
                    h = h || {};
                    h.id = h.id || subscription.id;
                    return me.send(url, d || this.destination, h, b);
                };
                subs[destination] = subscription;
                return subscription;
            });
        }
        else {
            return new Ember.RSVP.Promise(function(resolve){ resolve(sub); });
        }
    },

    /**
     * Submits a request for streaming data.  This involves 3 steps:
     * (1) a connection to a server URL must be established;
     * (2) a subscription to a destination must be established over that connection; and
     * (3) a message requesting the data must be sent over that subscription.
     * @param {object} config Configuration information for the stream.
     * @param {string} config.url The server URL for the CONNECT message.
     * @param {string} config.subscriptionDestination The destination for the SUBSCRIBE message.
     * @param {string} config.requestDestination The destination for the SEND message.
     * @param {object} [params] Optional request parameters.
     * @param {string} [id] The id of the request. If missing, one is auto-generated.
     * @returns {Promise} Promise that resolves with an Ember.Array of results when the first data records arrive.
     * The resolved Array will initially be empty, but its contents will continue to grow as they stream in.
     * Additionally, the Array will have the following attributes:
     * "id": (string) the auto-generated id for this request;
     * "params": (object) the request params;
     * "progress": (number) the percentage of total results that have streamed in (0-100);
     * "isStreaming": (boolean) true, until either (a) all records have been successfully fetched; or (b) data fetching
     * stops due to an error; or (c) the request is cancelled;
     * "cancel()": a method to abort any further processing of this request (in server & client).
     */
    stream: function(config, params, id) {

        // Ensure every request has an "id" param.
        params = params || {};
        id = id || params.id || ("req-" + _requestCounter++);
        params.id = params.id || id;

        // Define an array to store the results.
        var me = this,
            results = Ember.A().setProperties({
                "isStreaming": true,
                "progress": 0,
                "params": params
            });
        results.cancel = function() {
            if (this.get("isStreaming")) {
                me.send(
                    config.url,
                    config.cancelDestination,
                    {},
                    {id: id, cancel: true}
                );
            }
        };

        // Cache the array so it can be populated later with the response data.
        _streamResults[id] = results;

        return new Ember.RSVP.Promise(function(resolve, reject){
            if (!config || !config.url || !config.subscriptionDestination || !config.requestDestination) {
                reject("Invalid request configuration.");
                return;
            }

            // Ensure we have a subscription...
            me.subscribe(
                config.url,
                config.subscriptionDestination,
                _onStreamMessage
            )
            .then(function(sub) {

                // ...cache the resolve & reject functions for later use...
                _streamResolves[id] = resolve;
                _streamRejects[id] = reject;

                // ... and request the data stream.
                sub.send({}, params, config.requestDestination);
            })
            .catch(reject);
        });
    }
});
