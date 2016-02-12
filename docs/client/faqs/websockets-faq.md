# FAQ: Ember, WebSockets & STOMP

## What are WebSockets?

A WebSocket is a two-way connection between a client and server.  A WebSocket is initiated by the client, but once the connection is established, either the client or the server can send messages to each other.  This is useful, because it allows us to push data proactively from server to client in our apps.

## How do I create a WebSocket connection?

JavaScript can initiate the connection from the browser. The [HTML5 WebSocket API](http://www.sitepoint.com/introduction-to-the-html5-websockets-api/) for browsers is pretty straightforward (despite the hard to read [documentation](https://html.spec.whatwg.org/multipage/comms.html#websocket)).  This simplified pseudo-code snippet below illustrates the basics:

```js
// Request a connection to a socket server url:
var connection = new WebSocket(url, protocol);

// Set a callback to be invoked once the connection succeeds:
connection.onopen = function(){..};

// Set a callback in case there are errors:
connection.onerror = function(err){..};

// Set a callback to handle messages that are pushed to client from server:
connection.onmessage = function(messageEvent){ console.log(messageEvent.data); };

// Send a message from client to server:
connection.send("..");

// Close the connection:
connection.close();
```

## Does Ember require WebSockets?

No, but we are choosing to use WebSockets in our Security Analytics Ember UI. In our client we are leveraging the open source JavaScript library [SockJS](https://github.com/sockjs/sockjs-client) to handle cross-browser inconsistencies in the WebSocket API.

## What is STOMP?

STOMP is a simple text-based messaging protocol. It is applied onto WebSocket messages to give them some structure.

WebSocket messages are typically just text strings. The text can be pretty much anything.  It's up to the client and server to agree on the structure of the text content and then generate/parse the text messages accordingly.

STOMP defines a straightforward message structure that our WebSocket messages can follow.  Open source libraries are available for generating & parsing STOMP text messages, thus saving the developer some work in setting up WebSocket communications for their app.

## What do STOMP messages look like?

Generally, STOMP messages have a *command*, a set of *headers*, and a *body*.  For example:

```text
COMMAND
header1:value1
header2:value2

Body^@
```

The `Body` is typically a JSON string. Typically, `COMMAND` is either `CONNECT`, `SUBSCRIBE`, `SEND`, `MESSAGE` or `DISCONNECT`.  
You can see the full list of available commands and other details in the [STOMP spec](http://stomp.github.io/stomp-specification-1.2.html).

# Does Ember require STOMP?

No, but we are choosing to use it for our the Security Analytics Ember UI.  In our client we are leveraging the open source JavaScript library [stompJS](http://jmesnil.net/stomp-websocket/doc/).
