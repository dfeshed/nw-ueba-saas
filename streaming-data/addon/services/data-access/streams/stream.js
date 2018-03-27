/**
 * @file Stream abstract base class
 * Represents an observable sequence of values (e.g., events, data objects, anything).
 * Essentially a much simpler, less-functional version of RxJS.Observable.
 * @see https://github.com/Reactive-Extensions/RxJS/blob/master/doc/api/core/observable.md
 * @public
 */
import EmberObject, { computed } from '@ember/object';
import { run } from '@ember/runloop';
import { typeOf } from '@ember/utils';
import { warn } from '@ember/debug';
import config from 'ember-get-config';

/**
 * Default limit on the number of records that a stream will transmit from server.
 * Used to avoid excessive memory consumption on browser. Can be overwritten by each
 * individual stream request via request.stream.limit.
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

export default EmberObject.extend({

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
   * If true, each socket response for this stream will be expected to have a
   * `request.id` that matches the original request's `id` param.  (If no `id`
   * property is provided in the `socketRequestParams`, then one will be automatically
   * generated.) Any response that does not satisfy this match will be discarded.
   * @type {boolean}
   * @default true
   * @public
   */
  requireRequestId: true,

  /**
   * Running count of data records that have been returned by the server so far.
   * @type number
   * @public
   */
  count: 0,

  /**
   * If true, indicates the stream has been stopped.
   * @type {boolean}
   * @private
   */
  hasBeenStopped: false,

  /**
   * Computes the resolved set of request parameters. This is computed by starting
   * with any defaults (`defaultQueryParams`) from the resolved `socketConfig`,
   * then overwriting them with any given `socketRequestParams`, and then lastly
   * applying auto-generated params `id` and `stream.limit` if they are missing.
   * @type {object}
   * @private
   */
  resolvedSocketRequestParams: computed('socketRequestParams', 'socketConfig', function() {

    // Merge `socketRequestParams` with defaults from the resolved socket config.
    const cfg = this.get('socketConfig');
    const params = {
      ...(cfg.defaultQueryParams || {}),
      ...(this.get('socketRequestParams') || {})
    };

    // Auto-generate a request id, if needed.
    if (this.get('requireRequestId')) {
      params.id = params.id || `req-${_requestCounter++}`;
    }

    // Apply the default stream limit, if needed.
    params.stream = params.stream || {};
    params.stream.limit = params.stream.limit || cfg.defaultStreamLimit || DEFAULT_STREAM_LIMIT;

    return params;
  }),

  /**
   * Registers an observer to this stream.
   * The observer will have its callbacks notified whenever this stream emits a value.
   * @public
   */
  subscribe(observer) {
    this.set('subscription', observer);
    run(this, 'start');
  },

  /**
   * Submits a server request for streaming data.  This involves 3 steps:
   * (1) obtaining a connection to the socket server;
   * (2) obtaining a subscription to a destination over that connection; and
   * (3) sending a message requesting the specific data query to be sent over
   *     that subscription.
   * @private
   */
  start() {
    // no config, don't bother (possibly just allows for unit tests)
    if (!this.get('socketConfig')) {
      return;
    }

    // call onInit callbacks, indicate stream is about to begin
    this.notify('onInit', [this.stop.bind(this)]);

    // Resolve request params: Ensure given params always include an
    // id & stream.limit; set them if necessary.
    const { resolvedSocketRequestParams: params, socketConfig, fetchSocketClient } =
      this.getProperties('resolvedSocketRequestParams', 'socketConfig', 'fetchSocketClient');

    // Connect to socket server.
    const onMessageCallback = run.bind(this, this.onMessage);

    fetchSocketClient()
      .then((websocketClient) => {
        // Subscribe to destination.
        const subscribe = websocketClient.subscribe(socketConfig.subscriptionDestination, onMessageCallback, null);

        // Keep track of the new subscription promise
        this.setProperties({
          socketSubscriptionPromise: subscribe,
          websocketClient
        });

        // Once we've received confirmation that the subscription has been processed by the server
        subscribe.then((subscription) => {
          // Send query message for the stream.
          subscription.send({}, params, socketConfig.requestDestination);
        });
      })
      .catch(this.error.bind(this));
  },

  /**
   * Stops the data flow in the stream.
   * The callbacks will be passed whatever params are passed into this method.
   * @public
   */
  stop() {
    // Do not process stop if we already have
    if (!this.get('hasBeenStopped')) {
      this.set('hasBeenStopped', true);
      this.unsubscribe(true);
      this.notify('onStopped');
    }
  },

  /**
   * Invokes the onResponse callbacks (if any) of all observers.
   * The callbacks will be passed whatever params are passed into this method.
   * @public
   */
  response(response) {
    this.notify('onResponse', [response, this.stop.bind(this)]);
  },

  /**
   * Invokes the onError callbacks (if any) of all observers, then disposes all subscriptions.
   * The callbacks will be passed whatever params are passed into this method.
   * @public
   */
  error() {
    this.notify('onError', arguments);
    this.dispose();
    this.unsubscribe(true);
  },

  /**
   * Invokes the onCompleted callbacks (if any) of all observers, then disposes all
   * subscriptions. The callbacks will be passed whatever params are passed into this method.
   * @public
   */
  completed(response) {
    this.notify('onCompleted', [response]);
    this.dispose();
    this.unsubscribe(false);
  },

  /**
   * Invokes the given type of callbacks (if any) of all observers for this stream
   * instance. The callbacks will be passed whatever args array passed into this method.
   * @param {string} type Either 'onResponse', 'onError' or 'onCompleted'
   * @param {*[]} args The arguments to pass to the callbacks.
   * @private
   */
  notify(type, args) {
    const subscription = this.get('subscription');
    if (subscription && subscription[type]) {
      const resolvedArgs = [].slice.call(args || []);
      subscription[type](...resolvedArgs);
    }
  },

  /**
   * Removes subscription from this stream instance.
   * @private
   */
  dispose() {
    this.set('subscription', undefined);
  },

  /**
   * If the stream is streaming, submits a cancel request to the
   * socket server before closing the connection; otherwise, exits
   * successfully.
   * @param {boolean} isClientInitiated Indicates if we are stopping
   *   because the client is forcing it (error, or user 'pauses' or
   *   cancels a request) or if it has come to a natural end.
   * @private
   */
  unsubscribe(isClientInitiated) {
    if (this.get('websocketClient')) {
      // If it is client initiated, then need to send cancel request,
      // otherwise the unsub is ocurring because the server indicated
      // it is done, no cancel needed.
      if (isClientInitiated) {
        const dest = this.get('socketConfig.cancelDestination');
        const id = this.get('resolvedSocketRequestParams.id');

        if (dest && id) {
          this.get('websocketClient').send(dest, {}, { id, cancel: true });
        }
      }

      // Release this STOMP client subscription, but don't disconnect the STOMP client
      // because it may be re-used by other requests.
      this.get('socketSubscriptionPromise').then((subscription) => subscription.unsubscribe());
    }
  },

  /**
   * Handler for server responses to the data stream request.
   * If the response has an error code, invokes the Stream instance's onError handler.
   * Otherwise, triggers response handling.
   * @param {object} message The STOMP message with a response for a data stream.
   * @private
   */
  onMessage(message) {
    const response = message && message.body;
    const request = response && response.request;

    // If we require response ids, validate that the response & request ids match.
    if (this.get('requireRequestId')) {
      if (this.get('resolvedSocketRequestParams.id') !== (request && request.id)) {
        if (config.socketDebug) {
          const warnResponse = JSON.stringify(response);
          warn(`Received stream response with unexpected request id. Discarding it.\n ${warnResponse}`,
            { id: 'streaming-data.services.data-access.streams.mixins.from-socket' });
        }
        return;
      }
    }

    if (typeOf(response.code) !== 'undefined' && response.code !== 0) {
      // The response has an error code; update stream properties & notify observers.
      this.error(response);
    } else {
      this.sendResponse(request, response);
    }
  },

  /**
   * Invokes the Stream instance's onResponse handler, and if the response
   * has metadata that indicates that there are no more chunks of data coming, then
   * invokes the Stream instance's onComplete handler as well.
   * @private
  */
  sendResponse(request, response) {
    // call response which triggers response handlers
    this.response(response);

    const { data, meta } = response;

    // If meta indicates we are done, exit early
    if (meta && meta.complete === true) {
      this.completed(response);
      return;
    }

    // if no total, then no need to go through count/total/goal processing
    if (!meta || !meta.total) {
      return;
    }

    // The `count` is just a running counter computed from measuring `response.data.length`.
    const added = (data && data.length) || 0;
    const count = added + this.get('count');
    this.set('count', count);

      // The `goal` property is derived from `total`, `page.index` & `page.size`, if given.
    let goal = meta.total;

    if (request) {
      const { page } = request;
      // Watch out for page.size === 0, which means fetch all records.
      if (page && page.size) {
        goal = Math.min(page.size, meta.total - page.index);
      }
    }

    // The `progress` property is derived from `count` and `goal`, if `goal` is known.
    // If `goal` is not known, `progress` should be left undefined; we don't want to
    // set it to 100 because then the stream will think it is complete, and will stop
    // listening for more messages.
    const progress = goal ? parseInt(100 * count / goal, 10) : undefined;

    // Fire completed if there is no more data coming.
    if (progress >= 100) {
      this.completed(response);
    }
  }
});
