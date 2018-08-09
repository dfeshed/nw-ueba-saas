# transport <!-- omit in toc -->

The transport service manages the WebSocket connection. It handles messaging, channel creation, and deletion for the application layer protocol.

- [Methods](#methods)
    - [`.connect(onConnected, onError)`](#connectonconnected-onerror)
        - [Parameters](#parameters)
        - [Example](#example)
    - [`.disconnect()`](#disconnect)
        - [Returns](#returns)
        - [Example](#example)
    - [`.send(path, message)`](#sendpath-message)
        - [Parameters](#parameters)
        - [Returns](#returns)
        - [Example](#example)
    - [`.stream(path, message, messageCallback, errorCallback)`](#streampath-message-messagecallback-errorcallback)
        - [Parameters](#parameters)
        - [Returns](#returns)
        - [Example](#example)

# Methods

## `.connect(onConnected, onError)`

Connects the service to the WebSocket backend.

### Parameters

| Parameter     | Type       | Details                                           |
| ------------- | ---------- | ------------------------------------------------- |
| `onConnected` | `function` | Gets called once when the WebSocket is connected. |
| `onError`     | `function` | Called if the WebSocket experiences an error.     |

### Example

```JavaScript
transport.connect(() => {
    console.log('WebSocket connected');
}, (err) => {
    console.log(`WebSocket error: ${err}`);
})
```

## `.disconnect()`

Disconnects the WebSocket connection, if one is active.

### Returns

`Promise` - Resolved when the WebSocket calls its `onclose` function.

### Example

```JavaScript
transport.disconnect()
.then(() => {
    console.log('WebSocket disconnected');
})
```

## `.send(path, message)`

Used to send a single message to a path.

### Parameters

| Parameter | Type     | Details                                                      |
| --------- | -------- | ------------------------------------------------------------ |
| `path`    | `string` | The path to send the message to.                             |
| `message` | `Object` | The message object. Usually has keys `message` and `params`. |

### Returns

`Promise` - Resolved when the response arrives, the payload is set to the response message in object form. Can possibly reject if the passed parameters were bad.

### Example

```JavaScript
transport.send('/', {
    message: 'ls'
})
.then((message) => {
    // Do something...
})
```

## `.stream(path, message, messageCallback, errorCallback)`

Used to send a message to a path that will result in multiple responses from the server. Requires manually closing the channel using the returned function.

### Parameters

| Parameter         | Type       | Details                                                                        |
| ----------------- | ---------- | ------------------------------------------------------------------------------ |
| `path`            | `string`   | The path to send the message to.                                               |
| `message`         | `Object`   | The message object. Usually has keys `message` and `params`.                   |
| `messageCallback` | `function` | Called with the message data each time a response is received from the server. |
| `errorCallback`   | `function` | Called if there is an error sending the message.                               |

### Returns

`Promsie` - Resolved when the internal channel is created. The payload contains a function which to call when you are done with the stream and wish to close it. **If this function is not called, the server may continue sending messages and consuming resources.** *This does not close the WebSocket, only the channel. If you're not familiar with how the channels are used, all you need to know is to call `close()` when you're done with the stream.*

### Example

```JavaScript
transport.stream('/sys/stats', {
    message: 'mon',
    params: {
        depth: '1'
    }
}, (update) => {
    // Do something (multiple times)
}, (err) => {
    // Handle error
})
.then((close) => {
    // Use this close() function when you're done with the stream
})
```