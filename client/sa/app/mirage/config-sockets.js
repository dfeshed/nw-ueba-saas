/**
 * @file MockSocket client and server initialization
 * Substitutes the native window class WebSocket with MockSocket. Also initializes a MockServer instance,
 * which is required to handle messages sent via MockSockets.
 */
/* global Stomp */
import config from "../config/environment";

/**
 * Helper function that asks the given MockServer to send the given Stomp frame data back to the Stomp client,
 * after a brief delay. Marshalls (serializes) the given Stomp frame data, and uses a setTimeout for the delay.
 * If an Object/Array is given for the body param, this function will automatically stringify the JSON and add
 * a "content-type: application/json" header to the Stomp frame being sent.
 * @param {Object} server The server to send the Stomp frame.
 * @param {String} command The Stomp frame's command.
 * @param {Object} [headers] The hash of the Stomp frame's headers.
 * @param {String|Object|Array} [body]
 * @param {Number} [delay] Optional delay (in millisec) before sending the data.
 * @private
 */
function _sendFrame(server, command, headers, body, delay){
    if (typeof(body) === "object") {
        body = JSON.stringify(body);
        headers = headers || {};
        if (!headers["content-type"]) {
            headers["content-type"] = "application/json";
        }
    }
    window.setTimeout(function(){
        server.send(Stomp.Frame.marshall(command, headers, body));
    }, delay || 0);
}

/**
 * Helper function that instanties a MockServer for the given URL. This simple server uses STOMP to
 * respond to CONNECT & DISCONNECT commands. It can also echo payloads that it receives in SEND commands.
 * @param {String} url The websocket URL that this server receives messages for.
 * @returns {Object} The MockServer instance.
 * @private
 */
function _initServer(url){
    var mockServer = new window.MockServer(url);
    mockServer.on("connection", function(server) {
        server.on("message", function(message){

            // What kind of message did this server receive?
            var _frames = Stomp.Frame.unmarshall(message).frames,
                _firstFrame = _frames && _frames[0];
            switch(_firstFrame && _firstFrame.command){

                // Received a STOMP CONNECT frame. Respond with a CONNECTED frame.
                case "CONNECT":
                    _sendFrame(server, "CONNECTED");
                    break;

                // Received a STOMP DISCONNECT frame. Respond with a RECEIPT frame with a matching receipt id.
                case "DISCONNECT":
                    _sendFrame(server, "RECEIPT", {"receipt-id": _firstFrame.headers.receipt || ""});
                    break;

                // Received a STOMP send, probably from a subscription channel. If the message has an
                // "echo" header, respond with a MESSAGE for that same subscription channel, which just
                // echoes whatever the received message's body was, if any.
                case "SEND":
                    if (_firstFrame.headers.echo) {
                        _sendFrame(server,
                            "MESSAGE",
                            {
                                "subscription": _firstFrame.headers.id,
                                "content-type": _firstFrame.headers["content-type"] || "",
                                "receipt-id": _firstFrame.headers.receipt || ""
                            },
                            _firstFrame.body,
                            500
                        );
                    }
                    break;
            }
        });
    });
    return mockServer;
}

export default function initSockets() {
    if (config.socketURL) {

        // According to mock-socket docs, we must first create a mock server before creating any mock sockets.
        // So create a simple server here, and cache handle to it so that subsequent code can enhance
        // it to handle whatever socket requests we decide to mock later.
        window.mockServer = _initServer(config.socketURL);

        // Substitute the mock socket class for the real socket class.
        window.WebSocket = window.MockSocket;

        // @workaround Explicitly substitute mock socket for SockJS too, if SockJS is defined. Without this,
        // SockJS will not use our MockSocket even though we've substituted it for WebSocket, not sure why.
        window.SockJS = window.SockJS && window.MockSocket;
    }
}
