# NetWitness UI Mock Server

`mock-server` is node.js package that you can use in your project to provide mock websocket endpoints to the Netwitness UI. It uses `Express`, a node.js web application framework, to serve up the endpoints.

## Usage

1. In your project's `package.json`, add `"mock-server": "../mock-server"` to `devDependencies`
2. Create a `tests/server/server.js` file that will be used to start the mock server. Unless it is absolutely necessary to change it, keep the file named the same and in that location so that the jenkins build can easily find it later.
3. `require` in the `mock-server` and pass the `startServer` function the path to the directory where your [subscriptions](#creating-subscriptions) are contained.  Ex: `require('mock-server').startServer(__dirname);`.  `__dirname` is a node.js reserved variable representing the directory the current file is in. You can also pass an array of paths to directories.
4. Run your server: `node server.js`. The `mock-server` configured with your project's subscriptions will be started on port `9999`.
5. In your project's `environment.js`, ensure the `socketUrl` contains the url of your server, to include the port it is started on.

### Coding your `environment.js`

The mock server starts on a different port than Ember does, so when providing a `socketUrl`, you need to alter the port. You'll need to include code in your `environment.js` that checks the environment and dynamically builds the `socketUrl` based on that environment. The mock server's `socketUrl` should take the form of `'http://localhost:' + mockPort + '/socket/'` where `mockPort` can be altered at the command line when running `ember test` or `ember s`.

### Altering the mock port

By default `mock-server` starts on port `9999`. You should not need to alter this port, but if you absolutely need to...

1. When running the mock server, set the `MOCK_PORT` environment variable to the desired port: `MOCK_PORT=1234 node server.js`.
2. In your `environment.js`, point at the same port with your `socketUrl`.
```javasript
var mockPort = process.env.MOCK_PORT || 9999;
var socketUrl = 'http://localhost:' + mockPort + '/socket/';
```
3. Run `ember test` and `ember s` passing in the same `MOCK_PORT` environment variable: `MOCK_PORT=1234 ember s`.

## Functions

The `mock-server` node package provides the following functions.

* `startServer(Array | String)`
  * This will start the server after loading all your subscription files. This function takes either a `String`, for the root directory of your subscription files, or an `Array` for a group of subscription file locations.
* `prepareMessage(context, frame, body)`
  * Creates an appropriate `Sock`/`Stomp` message to be returned to the client.
  * `context`, `Object`, should be the subscription file object itself (usually its just `this`).
  * `frame`, `Object`, is the frame being processed as passed to `createSendMessage` (discussed below)
  * `body`, `Object`, is the payload of the response that the `mock-server` will return to the client. `body` must contain the fields below.
    * `code`, `Number`, the response code. `0`: no error. Put in a non-zero code to test error handing in your UI code.
    * `data`, type variable, the data returned with this request
    * `request`, `Object`, the input frame's body as an object. This is required to be included in the `body`.

## Creating Subscriptions

Each subscription file presents an endpoint you want to surface for your instance of the `mock-server`. Every entry in your `environment.js` will probably need a subscription file. A subscription file is a node.js file that must export an object containing specific properties.

The `mock-server` will discover your subscription files inside the directory/directories you pass it when calling `startServer`. The `mock-server` uses babel to transpile your subscription files, so you can use all the same code conventions you use in the web application.

### Subscription File Parameters
* `subscriptionDestination`, `String`, __required__
  * matches the `subscriptionDestination` for the desired enpoint in your project's `environment.js`
* `requestDestination`, `String`, __required__
  * matches the `requestDestination` for the desired enpoint in your project's `environment.js`
* `createSendMessage(frame)`, `Function`, __required__
  * takes a websocket Frame as input and should return a properly formatted `Sock`/`Stomp` string that is the response to send to the client.
* `delay`, `Number`, optional
  * how long the mock server should delay before sending the response (from `prepareSendMessage`)

### Example Subscription File

```javascript
import { prepareMessage } from 'mock-server';

export default {
  subscriptionDestination: '/test/subscription/promise/_1',
  requestDestination: '/test/request/promise/_1',
  createSendMessage(frame) {
    const body = {
      code: 0,
      data: [1, 1, 1, 1, 1],
      request: JSON.parse(frame.body)
    };
    return prepareMessage(this, frame, body);
  }
};
```



