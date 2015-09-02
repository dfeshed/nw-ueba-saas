/**
 * @file MockSocket client and server initialization
 * Substitutes the native window class WebSocket with MockSocket. Also initializes a MockServer instance,
 * which is required to handle messages sent via MockSockets.
 */
/* global Stomp */
/* global MockServer */
import config from "../config/environment";

/**
 * Helper function that asks the given MockServer to send the given Stomp frame data back to the Stomp client,
 * after a brief delay. Marshalls (serializes) the given Stomp frame data, and uses a setTimeout for the delay.
 * If an Object/Array is given for the body param, this function will automatically stringify the JSON and add
 * a "content-type: application/json" header to the Stomp frame being sent.
 * @param {String} command The Stomp frame's command.
 * @param {Object} [headers] The hash of the Stomp frame's headers.
 * @param {String|Object|Array} [body]
 * @param {Number} [delay] Optional delay (in millisec) before sending the data.
 */
MockServer.prototype.sendFrame = function(command, headers, body, delay){
    if (typeof(body) === "object") {
        body = JSON.stringify(body);
        headers = headers || {};
        if (!headers["content-type"]) {
            headers["content-type"] = "application/json";
        }
    }
    var me = this,
        doSend = function(){
            me.send(Stomp.Frame.marshall(command, headers, body));
        };
    if (delay) {
        window.setTimeout(doSend, delay);
    }
    else {
        doSend();
    }
};


/**
 * Helper function that registers a message handler for a MockServer instance. Used in conjunction with the
 * generic "message" event handler, which will simply fire these registered message handlers.
 * @param {Function} callback
 */
MockServer.prototype.addMessageHandler = function(callback){
    if (typeof callback === "function") {
        if (!this._messageHandlers) {
            this._messageHandlers = [];
        }
        this._messageHandlers.push(callback);
    }
};

/**
 * Helper function that sets the message handling for a MockServer instance. This simple implementation delegates
 * the logic for handling messages to a configurable list of registered callbacks.
 * @returns {Object} The MockServer instance.
 * @private
 */
MockServer.prototype.initHandlers = function(){

    this.on("connection", function(server) {
        server.on("message", function(message) {

            // Fire the registered message handlers on this instance.
            var frames = Stomp.Frame.unmarshall(message).frames,
                handlers = this._messageHandlers || [];
            for (var i = 0, len = handlers.length; i < len; i++) {
                if (handlers[i].apply(this, [message, frames]) === true) {

                    // If a handler returns exactly false, stop calling the remaining handlers.
                    break;
                }
            }
        });
    });
};

export default function initSockets(){
    var server;
    if (config.socketURL) {

        // According to mock-socket docs, we must first create a mock server before creating any mock sockets.
        // So create a simple server here, and cache handle to it so that subsequent code can enhance
        // it to handle whatever socket requests we decide to mock later.
        server = window.mockServer = new MockServer(config.socketURL);
        server.initHandlers();

        // Substitute the mock socket class for the real socket class.
        window.WebSocket = window.MockSocket;

        // @workaround Explicitly substitute mock socket for SockJS too, if SockJS is defined. Without this,
        // SockJS will not use our MockSocket even though we've substituted it for WebSocket, not sure why.
        window.SockJS = window.SockJS && window.MockSocket;
    }
    return server;
}
