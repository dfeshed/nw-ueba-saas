# Configuration

## Transport Options
These are the system properties that can be overridden to configure the various CARLOS transport settings.

### NW Protocol
|Property|Default Value|Description|
|--------|-------------|-----------|
|carlos.transport.nw.enabled|true|A boolean indicating if this transport type should be enabled.|
|carlos.transport.nw.connectionTimeout|30_000 (30 seconds)|The time (in milliseconds) to wait while trying to establish a connection before terminating the attempt and generating an error.|
|carlos.transport.nw.socketTimeout|60_000 (60 seconds)|The time (in milliseconds) to wait for a response when communicating with a remote service before generating an error.|

### JMS Protocol
|Property|Default Value|Description|
|--------|-------------|-----------|
|carlos.transport.jms.enabled|true|A boolean indicating if this transport type should be enabled.|
|carlos.transport.jms.memoryLimit|268_435_456 (256MB)|The memory limit for the client in bytes.  Reaching this limit will prevent subsequent messages from being accepted.|
|carlos.transport.jms.sendFailIfNoSpaceAfterTimeout|300_000 (5 minutes)|Timeout to wait for space to be available on the broker in milliseconds. This property causes the send() operation to fail with an exception on the client-side, but only after waiting the given amount of time. If space on the broker is still not freed after the configured amount of time, only then does the send() operation fail with an exception to the client-side.|
|carlos.transport.jms.queuePrefetch|1|The number of messages to fetch from the queue at a time. The queue prefetch limit controls how many messages can be streamed to a consumer at any point in time. Once the prefetch limit is reached, no more messages are dispatched to the consumer until the consumer starts sending back acknowledgements of messages (to indicate that the message has been processed).|

### AMQP Protocol
|Property|Default Value|Description|
|--------|-------------|-----------|
|carlos.transport.amqp.enabled|true|A boolean indicating if this transport type should be enabled.|

## Web Socket Options
|Property|Default Value|Description|
|--------|-------------|-----------|
|websocket.sockjs.heartbeatInterval|10_000 (10 seconds)|The amount of time in milliseconds when the server has not sent any messages and after which the server should send a heartbeat frame to the client in order to keep the connection from breaking.|

## Threats - Incident Options
|Property|Default Value|Description|
|--------|-------------|-----------|
|threats.incident.controller.batchSize|300|The number of incidents to send, per message, when streaming the incident to a client.|
