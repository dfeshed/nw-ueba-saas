# SA-UI Streaming Data Service

## What It Does: Requesting Data from the Server

With the exception of ember-data AJAX calls, this service in the means by which the sa application and all of its addons access the backend. It hides the complexity of the management and creation of websocket connections and streams behind a simple API.

## What It Does: Managing Sockets/Streams by Route

This service not only executes requests using streams over websockets, but also monitors your requests on a per route basis. Each socket connection opened is registered/cached by this service for that route. Every time a route changes, this service checks to see if any socket connections need to be cleaned up and closes them.

# Usage

The NetWitness UI Streaming Data API is provided via an Ember Service. It is automatically injected into every route and available via `this.request`. It can also be used by injecting the `request` service into your Ember class.

## `environment.js`

To use `streamRequest` and `promiseRequest`, a developer needs to add socket config properties in the `config/environment.js` file under `socketRoutes[modelName][method]`.  The `modelName` and `method` are then provided to this function as input.

### Example

In the example below, `core-event` is the `modelName` and `stream` is the method. The data for `socketUrl` and the `Destination` fields are required and if they are not provided `streamRequest` will error out and `promiseRequest` will return `null` instead of a Promise.

```javascript
socketRoutes: {
  ...
  'core-event': {
    socketUrl: '/investigate/socket',
    stream: {
      subscriptionDestination: '/user/queue/investigate/events',
      requestDestination: '/ws/investigate/events/stream'
    }
  },
}
```

# API

* [.streamRequest(opts)](#streamrequestopts-undefined)
* [.promiseRequest(opts)](#promiserequestopts-rsvppromise)

## `.streamRequest(opts): undefined`

`streamRequest` provides wrapper around raw access to streaming data on a websocket. It allows for providing fine grained hooks to be notified of various stream events and allows for streams to be halted arbitrarily.

If a proper `environment.js` entry is not in place, `streamRequest` will error out with a `WebsocketConfigurationNotFoundException` Error.

### When to use

Use `streamRequest` when you need to:

* keep a connection open for a long period of time for future communication over a websocket from the server
* stream a large amount of data in chunks

### When not to use

If you have a request that returns all of its data at once and then ends, `streamRequest` may be used, but `promiseRequest` may be an easier interface as it provides a simple `RSVP.Promise` `then`able API with the need to provide callbacks.

### Parameters

* `method`, `String`, __required__
  * used to indentify socket condfiguration in the app's `config/environment.js`
* `modelName`, `String`, __required__
  * the type of model (i.e., data record) that is being requested. This will be used look up a corresponding socket configuration in the app's `config/environment.js` file.
* `query`, `Object`, __required__
  * an arbitrary hash of inputs for the query
* `onResponse`, `Function`, __required__
  * A callback that is called when the stream returns data. The response object from the stream is passed to this callback. As a 2nd parameter to this callback, a function to stop the stream is provided.
* `streamOptions`, `Object`, optional
  * see [Stream Options](#streamoptions)
* `onInit`, `Function`, optional
  * An optional callback that is executed when the stream first starts. This callback is passed as its first parameter a function that can be used to stop the stream and terminate the websocket.
* `onStopped`, `Function`, optional
  * An optional callback that is called when the stream is stopped before completion (usually via the function provided to both `onInit` and `onResponse`. Nothing is passed to this function.
* `onCompleted`, `Function`, optional
  * An optional callback that is called when the server indicates that the stream has completed sending data. Nothing is passed to this function.
* `onError`, `Function`, optional
   * An optional callback that is called if the stream errors out. The errored response is passed to this callback. If no `onError` is provided, the error will simply be logged.

### Example

```javascript
this.request.streamRequest({
  method: 'foo',
  modelName: 'foo-count',
  query: {},
  onResponse({ data }, stopStreaming) {
    model.set('data', data);
    if (data.length > MAX_RESULT_LENGTH) {
      // have enough data, we're done
      stopStreaming();
    }
  },
  onError({ code }) {
    model.setProperties({
      status: 'rejected',
      reason: code
    });
  }
});
```

## `.promiseRequest(opts): RSVP.Promise`

`promiseRequest` executes a websocket request like `streamRequest` does, but it returns a [RSVP.Promise](http://emberjs.com/api/classes/RSVP.Promise.html) for easy `.then` chaining. Under the hood streams are still used to retrieve data from the server. The `RSVP.Promise` that is returned is `resolve`d with the first data returned over the stream. Because promises can only `resolve` once, this is also when the websocket connection is terminated.

As with every all types of requests in the Data Service, `promiseRequest` will attempt to look up your `environment.js` socket configuration using the parameters passed to it. If it cannot find a configuration, it will return `null`, rather than error out. The fact that the configuration was not found will be logged to the console.

### When to use

`promiseRequest` is meant to mimic a simple AJAX REST call over a websocket. Use it when you have a socket endpoint that returns a small amount of data all at once.

### When not to use

You cannot use `promiseRequest` if you need to:

* keep a connection open for a long period of time for future communication over a websocket from the server
* stream a large amount of data in chunks

### Parameters

* `method`, `String`, __required__
  * used to indentify socket condfiguration in the app's `config/environment.js`
* `modelName`, `String`, __required__
  * the type of model (i.e., data record) that is being requested. This will be used look up a corresponding socket configuration in the app's `config/environment.js` file.
* `query`, `Object`, __required__
  * an arbitrary hash of inputs for the query
* `onInit`, `Function`, optional
  * An optional callback that is executed when the stream first starts. This callback is passed as its first parameter a function that can be used to stop the stream and terminate the websocket. Since `promiseRequest`s stop themselves, this is to be used in case any long running requests need immediate cancelling.
* `streamOptions`, `Object`, optional
  * see [Stream Options](#streamoptions)

### Example

```javascript
this.request.promiseRequest({
  method: 'foo',
  modelName: 'foo-count',
  query: {
    param1: 'param1'
  }
}).then(({ data }) => this.get('model').set('data', data));
```

## streamOptions

Each API request takes a `streamOptions` object.

### Parameters
* `requireRequestId`, `Boolean`, optional
  * Defaults to `false`
* `keepAliveOnRouteChange`, `Boolean`, optional
  * Defaults to `false`
  * When set to `false`, will keep track of stream and clean up on route changes.
  * When set to `true`, opts a stream out from caching and automatic closing on route change
* `keepAliveOnTransitionToChildRoute`, `Boolean`, optional
  * Defaults to `false`
  * When set to `false` a parent route will not be kept alive when transitioning to a new child. So, if going to `/foo` to `/foo/bar`, all streams associated with `/foo` will be cleaned up.
  * When set to `true` a parent route will be kept alive when transitioning to a new child. So, if going to `/foo` to `/foo/bar`, all streams associated with `/foo` will be left alone.


# Development

## Run Tests

Run tests like any other Ember project (`ember test`), but first...

* `node tests/server/start.js` to run mock server
* Count to 10, then run tests.
  * WHY? If tests are run immediately, they may give false negative. Still trying to figure out why this happens. Every subsequent test will run as expected. And if you delay slightly to run the first test, those should be ok too.
