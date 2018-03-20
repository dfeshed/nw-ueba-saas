/**
 * @file Stream.fromSocket extension.
 * Enables a Stream class to instantiate a Stream using an RSA websocket stream request as its data source.
 * Each response (STOMP message) that is received for the request will be streamed to the stream's observers.
 * The socket stream is expected to follow the RSA stream protocol for SA UI; @see https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/docs/websocket-protocol.md
 * @public
 */
import Mixin from '@ember/object/mixin';

import { computed } from '@ember/object';
import { merge } from '@ember/polyfills';
import { run } from '@ember/runloop';
import { typeOf } from '@ember/utils';
import Ember from 'ember';
import { warn } from '@ember/debug';
import config from 'ember-get-config';

const {
  String: EmberString
} = Ember;

/**
 * Default limit on the number of records that a stream will transmit from server. Used to avoid excessive
 * memory consumption on browser. Can be overwritten by each individual stream request via request.stream.limit.
 * @type {number}
 * @default 100000
 * @private
 */
const DEFAULT_STREAM_LIMIT = 100000;

/**
 * Counter used for auto-generating request ids.
 * @type {number}
 * @private
 */
let _requestCounter = 0;

export default Mixin.create({

  /**
   * Hashtable of configuration properties for the stream.  Includes the following properties:
   * .socketUrl: (string) The connection URL for the socket server.
   * .subscriptionDestination: (string) The destination for the SUBSCRIBE message.
   * .requestDestination: (string) The destination for the SEND message to kick off the request.
   * .cancelDestination: (string) Optional destination for the SEND message to cancel the request.
   * @type {}
   * @public
   */
  socketConfig: null,

  /**
   * callback provided to Stream to retrieve websocket client
   * @type {function}
   * @public
  */
  fetchSocketClient: null,

  /**
   * Optional hash of request parameters; typically includes properties like "filter" & "sort".
   * @type {}
   * @public
   */
  socketRequestParams: null,

  /**
   * If true, each socket response for this stream will be expected to have a `request.id` that matches the original
   * request's `id` param.  (If no `id` property is provided in the `socketRequestParams`, then one will be automatically
   * generated.) Any response that does not satisfy this match will be discarded.
   * @type {boolean}
   * @default true
   * @public
   */
  requireRequestId: true,

  /**
   * If true, before the request goes out the `stream` and `stream.limit` params
   * will be inspected, and if they are not there, default properties will be added.
   * If false, no default stream properties will be added.
   * @type {boolean}
   * @default true
   * @public
   */
  applyStreamParams: true,

  /**
   * If true, indicates that data has been requested and the request has not yet completed.
   * Initially, before calling .start(), `isStreaming` is false. After calling .start(), `isStreaming` will change to
   * true and remain true until either all the requested data has arrived, or the request is cancelled.
   * @type boolean
   * @readonly
   * @public
   */
  isStreaming: false,

  /**
   * Running count of data records that have been returned by the server so far.
   * @type number
   * @public
   */
  count: 0,

  /**
   * A number (between 0-100) indicating what percentage of the expected results have arrived.
   * @type number
   * @readonly
   * @public
   */
  progress: 0,

  /**
   * Time in milliseconds to wait until triggering a timeout
   * @type number
   * @public
   */
  timeoutWait: 10000,

  /**
   * If true, indicates that a message has been received.
   * @type {boolean}
   * @private
   */
  hasReceivedMessage: false,

  /**
   * When true, indicates that a timeout verification is required
   * @type {boolean}
   * @public
   */
  isTimeoutEnabled: false,
  /**
   * Computes the resolved set of request parameters. This is computed by starting with any defaults (`defaultQueryParams`)
   * from the resolved `socketConfig`, then overwriting them with any given `socketRequestParams`, and then lastly
   * applying auto-generated params `id` and `stream.limit` if they are missing.
   * @type {object}
   * @private
   */
  _resolvedSocketRequestParams: computed('socketRequestParams', 'socketConfig', function() {

    // Merge `socketRequestParams` with defaults from the resolved socket config.
    const cfg = this.get('socketConfig');
    const params = merge(
      merge({}, cfg.defaultQueryParams || {}),
      this.get('socketRequestParams') || {}
    );

    // Auto-generate a request id, if needed.
    if (this.get('requireRequestId')) {
      params.id = params.id || `req-${_requestCounter++}`;
    }

    // Add stream params?
    if (this.get('applyStreamParams')) {
      // Apply the default stream limit, if needed.
      params.stream = params.stream || {};
      params.stream.limit = params.stream.limit || cfg.defaultStreamLimit || DEFAULT_STREAM_LIMIT;
    }
    return params;
  }),

  /**
   * @param {object} opts Options hash, to be passed into Stream constructor.
   * @param {object|string} opts.socketConfig a hashtable of configuration properties
   * for the stream. For details about Stream configuration, @see Stream.config.
   * @param {object} opts.socketRequestParams Optional hash of request parameters. @see Stream.params
   * @param {object} opts.fetchSocketClient function to retrieve the websocket client
   * @returns {object} This instance, for chaining.
   * @public
   */
  fromSocket(opts = {}) {
    this.setProperties(opts);
    return this;
  },

  // Extend start by calling _startFromSocket if we have a socket config.
  start() {
    if (!this.get('isStreaming') && this.get('socketConfig')) {
      this._startedFromSocket = true;
      this._startFromSocket();
    }
    return this._super();
  },

  // Extend stop by calling _stopFromSocket if we are streaming a socket.
  stop() {
    if (this._startedFromSocket) {
      this._startedFromSocket = false;
      this._stopFromSocket();
    }
    return this._super();
  },

  /**
   * Extends base method by unsubscribing from the Stomp client after notifying listeners.
   * @public
   */
  error() {
    const ret = this._super(...arguments);
    this._stopFromSocket();
    return ret;
  },

  /**
   * Extends base method by unsubscribing from the Stomp client after notifying listeners.
   * @public
   */
  completed() {
    const ret = this._super(...arguments);
    this.set('isStreaming', false);
    this._stopFromSocket();
    return ret;
  },

  /**
   * Submits a server request for streaming data.  This involves 3 steps:
   * (1) obtaining a connection to the socket server;
   * (2) obtaining a subscription to a destination over that connection; and
   * (3) sending a message requesting the specific data query to be sent over that subscription.
   * Note that this Stream object is returned immediately (synchronously) to be populated later as results arrive (asynchronously).
   * @private
   */
  _startFromSocket() {

    // If we are have already started, ignore & exit.
    if (this.get('isStreaming')) {
      return;
    }

    // Resolve request params: Ensure given params always include an id & stream.limit; set them if necessary.
    const params = this.get('_resolvedSocketRequestParams');

    // Initialize properties before connecting to socket server.
    // This allows subscribers to access state even while awaiting socket responses.
    this.setProperties({
      count: 0,
      progress: 0,
      isStreaming: true,
      hasReceivedMessage: false,
      page: params.page
    });

    const cfg = this.get('socketConfig');
    let { subscriptionDestination } = cfg;
    if (params.subDestinationUrlParams) {
      subscriptionDestination = EmberString.loc(cfg.subscriptionDestination, params.subDestinationUrlParams);
    }

    // Connect to socket server.
    const timeoutWait = this.get('timeoutWait');
    const isTimeoutEnabled = this.get('isTimeoutEnabled');
    const callback = run.bind(this, this._onmessage);

    this.get('fetchSocketClient')()
      .then((websocketClient) => {
        this._websocketClient = websocketClient;

        if (isTimeoutEnabled && typeOf(timeoutWait) === 'number') {
          run.later(() => {
            if (!this.get('hasReceivedMessage')) {
              this.timeout();
            }
          }, timeoutWait);
        }

        // Subscribe to destination.
        const subscribe = websocketClient.subscribe(subscriptionDestination, callback, null);
        // Keep track of the new subscription promise
        this._socketSubscription = subscribe;
        // Once we've received confirmation that the subscription has been processed by the server
        subscribe.then((subscription) => {
          // Send query message for the stream.
          subscription.send({}, params, cfg.requestDestination);
        });
      })
      .catch(this.error.bind(this));
    return this;
  },

  /**
   * If the stream is streaming, submits a cancel request to the socket server before closing the connection; otherwise, exits successfully.
   * @returns {object} This instance, for chaining.
   * @private
   */
  _stopFromSocket() {
    if (this._websocketClient) {
      if (this.get('isStreaming')) {
        const dest = this.get('socketConfig.cancelDestination');
        const id = this.get('_resolvedSocketRequestParams.id');

        if (dest && id) {
          this._websocketClient.send(dest, {}, { id, cancel: true });
          this.set('isStreaming', false);
        }
      }

      // Release this STOMP client subscription, but don't disconnect the STOMP client
      // because it may be re-used by other requests.
      this._socketSubscription.then((subscription) => subscription.unsubscribe());
    }
    return this;
  },

  /**
   * Handler for server responses to the data stream request.
   * If the response has an error code, invokes the Stream instance's onError handler.
   * Otherwise, invokes the Stream instance's onNext handler, and if the response has metadata that indicates that there
   * are no more chunks of data coming, then invokes the Stream instance's onComplete handler as well.
   * @param {object} message The STOMP message with a response for a data stream.
   * @private
   */
  _onmessage(message) {
    this.set('hasReceivedMessage', true);
    const response = message && message.body;
    const request = response && response.request;

    // If we require response ids, validate that the response & request ids match.
    if (this.get('requireRequestId')) {
      if (this.get('_resolvedSocketRequestParams.id') !== (request && request.id)) {
        if (config.socketDebug) {
          const warnResponse = JSON.stringify(response);
          warn(`Received stream response with unexpected request id. Discarding it.\n ${warnResponse}`, { id: 'streaming-data.services.data-access.streams.mixins.from-socket' });
        }
        return;
      }
    }

    if (typeOf(response.code) !== 'undefined' && response.code !== 0) {
      // The response has an error code; update stream properties & notify observers.
      this.setProperties({
        errorCode: response.code,
        isStreaming: false
      });
      this.error(response);

    } else {

      // The response has no error code; update stream properties & notify observers.
      const { data, meta } = response;

      // The `count` is just a running counter computed from measuring `response.data.length`.
      const added = (data && data.length) || 0;
      const count = added + this.get('count');

      // The `total` property is read from the optional `response.meta`, if given.
      const total = meta && meta.total;

        // The `goal` property is derived from `total`, `page.index` & `page.size`, if given.
      let goal = total;

      if (total && request) {
        const { page } = request;
        if (page && page.size) {  // Watch out for page.size === 0, which means fetch all records.
          goal = Math.min(page.size, total - page.index);
        }
      }

      // The `progress` property is derived from `count` and `goal`, if `goal` is known. If `goal` is not known,
      // `progress` should be left undefined; we don't want to set it to 100 because then the stream will think it
      // is complete, and will stop listening for more messages.
      const progress = goal ? parseInt(100 * count / goal, 10) : undefined;

      // Store these properties on the stream instance, as well as any properties in the `response.meta`.
      this.setProperties(
        merge({
          count,
          total,
          goal,
          progress,
          isStreaming: (progress === undefined) || (progress < 100)
        }, meta || {}));

      // pass response and mechanism to stop stream if required
      this.next(response, this.stop.bind(this));

      // Fire completed if there is no more data coming. In order to detect that, sometimes we can compute `progress`;
      // other times we can't, but the response will tell us it is complete by include `meta.complete: true`.
      if ((progress >= 100) || (meta && meta.complete === true)) {
        this.completed(response);
      }
    }
  }
});
