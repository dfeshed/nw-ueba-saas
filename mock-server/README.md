# NetWitness UI Mock Server

`mock-server` is node.js package that you can use in your project to provide mock websocket endpoints to the Netwitness UI. It uses `Express`, a node.js web application framework, to serve up the endpoints.

## Usage

If you prefer to learn by reading code, check out [streaming-data's](https://github.rsa.lab.emc.com/asoc/sa-ui/tree/master/streaming-data/tests/server) usage of the mock server.

1. In your project's `package.json`, add `"mock-server": "../mock-server"` to `devDependencies`
2. Create a `tests/server/server.js` file that will be used to start the mock server. The file name and location is important so that the jenkins build can easily find it later.
3. `require` in the `mock-server` and pass the `startServer` function the path to the directory where your [subscriptions](#creating-subscriptions) are contained.  Ex: `require('mock-server').startServer(__dirname);`.  `__dirname` is a node.js reserved variable representing the directory the current file is in. You can also pass an array of paths to directories.
4. Run your server: `node server.js`. The `mock-server` configured with your [project's subscriptions](#creating-subscriptions) will be started on port `9999`.
5. In your project's `environment.js`, ensure the `socketUrl` contains the url of your server, to include the port (`9999`) it is started on.

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

The `mock-server` node package provides the following function.

* `startServer(Array | String)`
  * This will start the server after loading all your subscription files.
  * This function takes either a `String`, for the root directory of your subscription files, or an `Array` for a group of subscription file locations.
* `shared.subscriptions`
  * This is a hash of shared/reusable subscription files, check `/shared/subscriptions`
* `mock-server/util: determineSocketUrl(environment, productionPath)`
  * Not used via `require('mock-server')`, instead it is used via `require('mock-server/util')`
    * See that file for why
  * This function is a utility for Ember apps for calculating socketUrls on startup.
  * This function takes the environment (`development`, `test`, `production`), the desired production socketUrl, inspects node.js process variables, and calculates the appropriate `socketUrl` to use.
  * If in `development` or `test` this function will calculate a URL that points to the `mock-server`.
  * To NOT point at the `mock-server` in `development` or `test`, start `ember` with the `NOMOCK` environment variable set to anything.
    * For example: `NOMOCK=1 ember s`

## Creating Subscription.

Each subscription file presents an endpoint you want to surface for your instance of the `mock-server`. Every entry in your `environment.js` will probably need a subscription file. A subscription file is a node.js file that must export an object containing specific properties.

The `mock-server` will discover your subscription files inside the directory/directories you pass it when calling `startServer`. The `mock-server` uses babel to transpile your subscription files, so you can use all the same code conventions you use in the web application.

### Subscritption Watching

Normally with a node.js server, if something on the server changes (like a subscription file) the server has to be restarted. This isn't the case with `mock-server`. `mock-server` will watch the directories you provide it and re-`require` and re-calculate your project's subscriptions when __any__ file in directories you pass `startServer` changes. This means that as you are tweaking/crafting tests, there's no need to constantly bounce your mock server.

### Subscription File Parameters

* `subscriptionDestination`, `String`, __required__
  * matches the `subscriptionDestination` for the desired enpoint in your project's `environment.js`
* `requestDestination`, `String`, __required__
  * matches the `requestDestination` for the desired enpoint in your project's `environment.js`
* `message(frame)`, `Function`,
  * Takes a websocket Frame as input
  * Should `return` a [body object](#body-object).
  * Either `message` or `page` is required. If both are provided, the subscription will be ignored.
* `page(frame, sendMessageCallback)`, `Function`,
  * Takes a websocket Frame as input
  * Takes a `sendMessageCallback` which you call when you want to send a message to the client.
    * The `sendMessageCallback` should be provided a [body object](#body-object).
    * You can call this callback as many times and as frequently as you'd like. Each time it is called a message, with the body you provide the callback, is sent over the same websocket connection to the client.
  * Does not have a `return`
  * Either `message` or `page` is required. If both are provided, the subscription will be ignored.
* `delay`, `Number`, optional
  * how long the mock server should delay before sending the response

### Body Object

The following are the valid fields for the `body` object either returned by `message` or sent to the `page` callback function.

* `data`, type variable, the data returned with this request
  * This is the field you'll care about more often than not, many tests will return `{ data: <<somedata>> }` and nothing else.
* `meta`, `Object`, meta information about the response.
  * Set `meta.complete = true` if you would like to indicate to the client that the message being sent is the last message (important for `page`);
* `code`, `Number`, the response code. Put in a non-zero code to test error handing in your UI code.
  * If no `code` is provided, a default of `0` is used.
  * Alter this default if you want to validate error handling in your tests.
* `request`, `Object`, the input frame's body as an object. This is required to be included in the `body`.
  * If no `request` is provided, a default of the original request is used.
  * This default should not need to change unless you are testing the `streaming-data` addon directly.


### Example Subscription Files

#### With `message`

```javascript
import { prepareMessage } from 'mock-server';

export default {
  subscriptionDestination: '/test/subscription/promise/_1',
  requestDestination: '/test/request/promise/_1',
  message(frame) {
    return {
      data: [1, 1, 1, 1, 1]
    };
  }
};
```

#### With `page`

```javascript
const outData = [
  [1, 2, 3, 4, 5],
  [6, 7, 8, 9, 10]
];

export default {
  subscriptionDestination: '/test/subscription/stream/_2',
  requestDestination: '/test/request/stream/_2',
  page(frame, sendMessage) {
    // send one at time = 0 and time = 1000
    for (let i = 0; i < 2; i++) {
      setTimeout(function() {
        sendMessage({
          data: outData[i]
        });
      }, i * 1000);
    }
  }
};
```



