import Ember from 'ember';
import Thread from 'sa/utils/thread';
import config from 'sa/config/environment';
const { run } = Ember;

/* global MockServer */
/* global Stomp */

// Utility for slicing a given array according to given page parameters.
// If `page.size` is zero or unspecified, no size limit is applied.
function _getItemsPage(page, items) {
  page = page || {};
  let { index, size } = page;

  if (typeof index === 'number') {
    if ((typeof size === 'number') && size) {
      return items.slice(index, index + size);
    }
    return items.slice(index);
  } else {
    return items;
  }
}

// Utility that generates a function that will return true if it is passed a socket request matching
// the configuration settings under `config/environment.js#socketRoutes` for a given modelName+method pair.
function _makeFilterForRoute(modelName, method) {
  return function(message, frames) {
    try {
      return (frames[0].headers.destination === config.socketRoutes[modelName][method].requestDestination);
    } catch (err) {
      return false;
    }
  };
}

/**
 * Registers a mock handler function for a given modelName+method pair.
 * This method is essentially sugar on top of the `register()` method.
 *
 * For example, suppose your app makes a call to `this.store.findRecord('incident', 'id1')`. If you wish mirage to
 * mock the response for such a call, use: `mockserver.route('incident', 'findRecord', handler)` where `handler` is
 * a function that will be invoked to produce the mock response.
 *
 * @param {string} modelName The name of the Ember Data model that this socket request pertains to.
 * @param {string} method Either 'stream', 'query', 'findRecord', 'findAll', 'createRecord', 'updateRecord', or 'deleteRecord'.
 * @param {function} handler The callback function that will generate the mock response.  This function will be invoked
 * with these arguments:
 * `message` (type = string): an outbound STOMP request message;
 * `frames` (type = object[]): an array of STOMP frames derived from the unmarshalled `message`;
 * `server` (object): the MockServer instance.
 * @public
 */
MockServer.prototype.route = function(modelName, method, handler) {
  this.register({
    filter: _makeFilterForRoute(modelName, method),
    handler
  });
};

/**
 * Registers a filter-handler pair for mocking a socket request.
 *
 * Whenever the given mock server receives a socket request, it will test the request message
 * against any registered filter-handler pairs. For any pairs whose filter function returns true,
 * the server will call the corresponding handler function.
 *
 * The filter function & the handler function will each be invoked with three arguments:
 * `message` (type = string): an outbound STOMP request message;
 * `frames` (type = object[]): an array of STOMP frames derived from the unmarshalled `message`;
 * `server` (object): the MockServer instance.
 *
 * The handler function will be invoked if the filter function returns truthy.
 * Note that the handler function can invoke `server.sendFrame()` to mock a response response.
 * `server.sendFrame()` can be called once, multiple times, or not at all, if desired.
 *
 * @param {object} entry
 * @param {function} entry.filter The filter function.
 * @param {function} entry.handler The handler function.
 * @returns {object} This instance, for chaining.
 * @public
 */
MockServer.prototype.register = function(entry) {
  this._registry = this._registry || [];
  this._registry.push(entry);
  return this;
};

/**
 * Helper function that asks the given MockServer to send the given Stomp frame data back to the Stomp client,
 * after a brief delay. Marshalls (serializes) the given Stomp frame data, and uses a setTimeout for the delay.
 * If an Object/Array is given for the body param, this function will automatically stringify the JSON and add
 * a 'content-type: application/json' header to the Stomp frame being sent.
 * @param {String} command The Stomp frame's command.
 * @param {Object} [headers] The hash of the Stomp frame's headers.
 * @param {String|Object|Array} [body]
 * @param {Number} [delay] Optional delay (in millisec) before sending the data.
 * @public
 */
MockServer.prototype.sendFrame = function(command, headers, body, delay) {
  // Apply default delay if undefined.
  delay = (typeof delay === 'number') ? delay : 100;

  if (typeof (body) === 'object') {
    body = JSON.stringify(body);
    headers = headers || {};
    if (!headers['content-type']) {
      headers['content-type'] = 'application/json';
    }
  }

  let me = this;
  let doSend = function() {
    me.send(Stomp.Frame.marshall(command, headers, body));
  };

  if (delay) {
    run.later(this, doSend, delay);
  } else {
    doSend();
  }
};

/**
 * Sends a given array of items as a single socket response.
 * @public
 */
MockServer.prototype.sendList = function(items, page, total, frames, delay, notificationCode) {
  // Apply default arguments as needed.
  total = total || (items && items.length) || 0;
  let frame = (frames && frames[0]) || {};

  // If paging is requested, slice off the request subset of items.
  items = _getItemsPage(page, items);

  this.sendFrame(
    'MESSAGE',
    {
      subscription: (frame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: items || [],
      notificationCode,
      request: frame.body,
      meta: {
        total: total || (items && items.length) || 0
      }
    },
    delay
  );
};

/**
 * Sends a given array of items as a stream of socket responses.
 * @public
 */
MockServer.prototype.streamList = function(items, page, total, frames, delay) {

  // Apply default arguments as needed.
  delay = (typeof delay === 'number') ? delay : 100;
  total = total || (items && items.length) || 0;
  let notificationCode = (typeof items.notificationCode !== 'undefined') ? items.notificationCode : -1;

  // If paging is requested, slice off the request subset of items.
  items = _getItemsPage(page, items);

  // Send the requested items in batches of `rate` at a time, pausing between batches for `interval` millisec.
  let me = this;
  let rateValue = (frames[0].body.stream && frames[0].body.stream.limit) ? frames[0].body.stream.limit : 9;
  Thread.create({
    queue: items,
    interval: 17,
    rate: rateValue,
    delay,
    onNextBatch(arr) {
      me.sendList(arr, null, total, frames, 0, notificationCode);
    }
  }).start();
};

/**
 * Extends MockServer.prototype.init in order to invoke the server's .handle() method
 * whenever a message is received by the server.
 * @returns {object} This instance, for chaining.
 * @private
 */
let _super = MockServer.prototype.init;
MockServer.prototype.init = function() {
  if (_super) {
    _super.apply(this, arguments);
  }

  let me = this;
  this.on('message', (message) => {
    me._handle(message);
  });
  return this;
};

/**
 * Tests a given message against all registered filters. For any filters that returns true,
 * the server will call the corresponding handler function registered with that filter.
 * @private
 */
MockServer.prototype._handle = function(message) {
  let me = this;
  let { frames } = Stomp.Frame.unmarshall(message);
  let body = frames && frames[0] && frames[0].body;

  if (body && (typeof body === 'string')) {
    frames[0].body = JSON.parse(body);
  }
  (this._registry || [])
    .filter((reg) => {
      return reg.filter(message, frames, me);
    })
    .forEach((reg) => {
      reg.handler(message, frames, me);
    });
};

export default MockServer;
