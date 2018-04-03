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
    socketUrl: '/api/investigate/socket',
    stream: {
      subscriptionDestination: '/user/queue/investigate/events',
      requestDestination: '/ws/investigate/events/stream'
    }
  },
}
```

# API

* [.streamRequest(opts)](#streamrequestopts-undefined)
* [.pagedStreamRequest(opts)](#pagedstreamrequestopts-cursor)
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

## `.pagedStreamRequest(opts): Cursor` (Pending)

`pagedStreamRequest` has the same API and behaves like a `streamRequest`, in fact it uses a `streamRequest` under the hood. `pagedStreamRequest`s are to be used on specific endpoints that provide streams of data in pages where the items of each page are of __indeterminate total size__. `pagedStreamRequest`s maintain an internal stack of page markers that allow for `next`, `previous`, `first` and `last` calls on the `Cursor` object that is returned by `pagedStreamRequest`.

### Pages Based on Content Size

In a typical pagination scenario, the items that make up a page are clearly defined and an API can indicate how many pages there are. This allows an API consumer to ask for the last page or "page 24". However, when the items that make up a page can vary widely in size (10b vs 500k), it makes sense for API and UI performance reasons to define a page based on the size of the items rather than the number of items. `pagedStreamRequest`'s pages are size-based. How big depends on the API, `pagedStreamRequest` does not care. `pagedStreamRequest` APIs work with a min and max size. A page cannot be smaller than the min or larger than the max.

- if a page min is 100k, page 1 could have just one item if the first item is 101k.
- if a page min is 100k, page 1 could have 11 items if the 11th item pushes the size over 100k
- if a page max is 500k, page 1 could have 1 item if the first item is 495k and the 2nd item is 10k.
- if a page max is 500k, page 1 could have 1 items if the very first item is over 500k. In this case the item returned will contain an indication that the item is too big to return. This is not considered an error, so instead of the `onError` handler being called, the `onResponse` handler would be called. The "too big item" is effectively a page unto itself and `pagedStreamRequest` treats it as such. It is up to the consumer to determine how to use this sort of page. After encountering this oversized page, `next` would move beyond it.

### How Paging Works

After the initial call of `pagedStreamRequest`, paging takes place via the returned `Cursor`. Each request to an API for a page returns a `marker` which indicates where the next page starts. If only the first page had been visited, `Cursor.next()` would send the first page's `marker` to the API, so the API knows to pick up after the first page. The value of `marker` isn't significant to the `Cursor`, only to the API in question. `marker` could be a simple page number, or it could be some other meaningful piece of data the API can use to keep track of response location. `marker`s are not exposed to the consumer of `pagedStreamRequest` or the `Cursor`, they are tracked internally.

When the last page is encountered, the API returns a flag which indicates it has reached the end of the result (and the `onCompleted` handler is called).

The `Cursor` keeps track of the `marker`s as it moves through the result. There are four paging functions on the `Cursor`. `first`, `previous`, `next`, and `last`. When needed -- as with `previous` and `last` -- the `Cursor` uses the stored `marker`s to traverse the result. But unless the `marker` has been encountered for the last page, `last` cannot be used.

If a `Cursor` function is called that does not logically make sense, the cursor will do nothing; for instance, if on the `first` page and `previous` is called or if on the `last` page and `next` is called the functions will NOOP. Additionally, you cannot call `last` until the last page has been encountered via `next`ing. A call to `last` when the last marker isn't known will also NOOP.

To help the consumer of the `Cursor` know what can be executed the `Cursor` exposes a set of flags -- `canFirst`, `canPrevious`, `canNext` and `canLast` -- which can be used to prevent certain pagination user interactions.

### When not to use

Do not use this if the API you intend to hit hasn't been coded to specifically handle `pagedStreamRequest` calls. No streaming endpoint can handle these sorts of requests unless it has been specifically coded to handle them.

### How it Differs from `streamRequest`

- Multiple messages are not sent per page. So `onResponse` is only called once per API/Cursor call.
- `batch` and `limit` stream parameters (for APIs) are ignored
- `pagedStreamRequest` does not keep subscriptions open for future messages, instead it assumes another page will not be requested and shuts down. This prevents users needing to maintain the subscription.
- `onCompleted` isn't called after each page, it is called only when the last page is returned. Even after `onCompleted` has been called, the `Cursor` is still usable.
- `onResponse` and `onInit` are not provided a callback to stop the stream since a request only has one response.
- There is no `onStopped` handler.

### Parameters

* `method`, `String`, __required__
  * used to indentify socket condfiguration in the app's `config/environment.js`
* `modelName`, `String`, __required__
  * the type of model (i.e., data record) that is being requested. This will be used look up a corresponding socket configuration in the app's `config/environment.js` file.
* `query`, `Object`, __required__
  * an arbitrary hash of inputs for the query
* `onResponse`, `Function`, __required__
  * A callback that is called when the stream returns data. The response object from the stream is passed to this callback as the only parameter.
* `streamOptions`, `Object`, optional
  * see [Stream Options](#streamoptions)
* `onInit`, `Function`, optional
  * An optional callback that is executed when the stream first starts. The callback is passed nothing.
* `onCompleted`, `Function`, optional
  * An optional callback that is called when the server indicates that the stream has completed sending data. Nothing is passed to this function.
* `onError`, `Function`, optional
   * An optional callback that is called if the stream errors out. The errored response is passed to this callback. If no `onError` is provided, the error will simply be logged.

### Example

```javascript
// a call to pagedStreamRequest retrieves the first page of results
const cursor = this.request.pagedStreamRequest({
  method: 'foo',
  modelName: 'foo-count',
  query: {},
  onResponse({ data, meta }) {
    if (meta.itemTooLarge) {
      // Only one result in response and it is too large to send
      addItemTooLargeToResults(data[0]);
    } else {
      // 1 - N items in results
      addItemsTooResults(data);
    }
  },
  onCompleted() {
    model.set('encounteredLastPage', true);
  }
});

// gets 2nd page
cursor.next();

// gets 3rd page
cursor.next();

// gets 2nd page
cursor.previous();

// gets 1st page
cursor.first();

// if the 3rd page was the last page, this gets the 3rd page,
// otherwise the last page has not been encountered so execution
// will not enter this if
if (cursor.canLast) {
  cursor.last();
}
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
  * Defaults to `true`
* `keepAliveOnRouteChange`, `Boolean`, optional
  * Defaults to `false`
  * When set to `false`, will keep track of stream and clean up on route changes.
  * When set to `true`, opts a stream out from caching and automatic closing on route change
* `keepAliveOnTransitionToChildRoute`, `Boolean`, optional
  * Defaults to `false`
  * When set to `false` a parent route will not be kept alive when transitioning to a new child. So, if going to `/foo` to `/foo/bar`, all streams associated with `/foo` will be cleaned up.
  * When set to `true` a parent route will be kept alive when transitioning to a new child. So, if going to `/foo` to `/foo/bar`, all streams associated with `/foo` will be left alone.
* `cancelPreviouslyExecuting`, `Boolean`, optional
  * Defaults to `false`
  * When `true`, if an API call using the same `method`/`modelName` is executed while the previous has not finished or is still open, the previous will be effectively cancelled.
    * For stream requests, no callback will be called.
    * For promise requests, the promise will neither be `resolve`d nor `reject`ed.

# Development

## Run Tests

Run tests like any other Ember project (`ember test`), but first...

* `node tests/server/server.js` to run mock server