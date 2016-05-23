/**
 * @file Stream.fromSocket extension.
 * Enables a Stream class to instantiate a Stream using an RSA websocket stream request as its data source.
 * Each response (STOMP message) that is received for the request will be streamed to the stream's observers.
 * The socket stream is expected to follow the RSA stream protocol for SA UI; @see https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/docs/websocket-protocol.md
 * @public
 */
import Ember from 'ember';
import StreamHelper from 'sa/utils/stream/helpers';

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

export default Ember.Mixin.create({

  // The websocket service, used for server communication.
  // @workaround Ember.inject.service() won't work here, so we should pass this attr into the Stream constructor.
  websocket: null,

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
   * Alternative to `socketConfig`; this attribute specifies a configuration identifier from config/environment's
   * `socketStreams` hash. The identifier is an object with properties `modelName` (e.g., 'incident') and `method`
   * (e.g., `query`, `fetchRecord`, `updateRecord`, `deleteRecord`). Only used if `socketConfig` attribute is not specified.
   * @type {{ modelName: string, method: string }}
   * @default null
   * @public
   */
  socketConfigType: null,

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
   * Reads config hash from `socketConfig`. If missing, tries to look it up in config/environment under the
   * keys specified by `socketConfigType`.
   * @type {object}
   * @private
   */
  _resolvedSocketConfig: Ember.computed('socketConfig', 'socketConfigType', function() {
    let cfg = this.get('socketConfig');
    if (!cfg) {

      // No `socketConfig` given, so lookup one by the socketConfigType's modelName and method.
      let { modelName, method } = this.get('socketConfigType') || {};
      cfg = StreamHelper.findSocketConfig(modelName, method);
    }
    return cfg || {};
  }),

  /**
   * Computes the resolved set of request parameters. This is computed by starting with any defaults (`defaultQueryParams`)
   * from the resolved `socketConfig`, then overwriting them with any given `socketRequestParams`, and then lastly
   * applying auto-generated params `id` and `stream.limit` if they are missing.
   * @type {object}
   * @private
   */
  _resolvedSocketRequestParams: Ember.computed('socketRequestParams', '_resolvedSocketConfig', function() {

    // Merge `socketRequestParams` with defaults from the resolved socket config.
    let cfg = this.get('_resolvedSocketConfig'),
      params = Ember.merge(
        Ember.merge({}, cfg.defaultQueryParams || {}),
        this.get('socketRequestParams') || {}
      );

    // Auto-generate a request id, if needed.
    if (this.get('requireRequestId')) {
      params.id = params.id || `req-${_requestCounter++}`;
    }

    // Apply the default stream limit, if needed.
    params.stream = params.stream || {};
    params.stream.limit = params.stream.limit || DEFAULT_STREAM_LIMIT;
    return params;
  }),

  /**
   * Instantiates a new Stream object, with methods for submitting & cancelling a request for a websocket data stream.
   * @param {object} opts Options hash, to be passed into Stream constructor.
   * @param {object|string} opts.config Either (i) a hashtable of configuration properties for the stream, or
   * (ii) the id of such a configuration from the config file (under `socketStreams`).
   * For details about Stream configuration, @see Stream.config
   * @param {object} opts.params Optional hash of request parameters. @see Stream.params
   * @returns {object} This instance, for chaining.
   * @public
   */
  fromSocket(opts) {
    this.setProperties(opts);
    return this;
  },

  // Extend start by calling _startFromSocket if we have a socket config.
  start() {
    if (!this.get('isStreaming') && (this.get('socketConfig') || this.get('socketConfigType'))) {
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

    // Validate configuration: cancelDestination is optional, but the other destinations aren't.
    let cfg = this.get('_resolvedSocketConfig');
    if (!cfg || !cfg.socketUrl || !cfg.subscriptionDestination || !cfg.requestDestination) {
      let { modelName, method } = this.get('socketConfigType') || {};
      Ember.Logger.error(`Invalid socket stream configuration: ${modelName} ${method}`);
      throw(`Invalid socket stream configuration: ${modelName} ${method}`);
    }

    // Resolve request params: Ensure given params always include an id & stream.limit; set them if necessary.
    let params = this.get('_resolvedSocketRequestParams');

    // Initialize properties before connecting to socket server.
    // This allows subscribers to access state even while awaiting socket responses.
    this.setProperties({
      count: 0,
      progress: 0,
      isStreaming: true,
      page: params.page
    });

    let { subscriptionDestination } = cfg;
    if (params.subDestinationUrlParams) {
      subscriptionDestination = Ember.String.loc(cfg.subscriptionDestination, params.subDestinationUrlParams);
    }

    // Connect to socket server.
    let me = this,
      callback = Ember.run.bind(this, this._onmessage);
    this.get('websocket').connect(cfg.socketUrl)
      .then(function(conn) {
        me._connection = conn;

        // Subscribe to destination.
        let sub = conn.subscribe(subscriptionDestination, callback);

        // Send query message for the stream.
        sub.send({}, params, cfg.requestDestination);

        me = null;
      });
    return this;
  },

  /**
   * If the stream is streaming, submits a cancel request to the socket server; otherwise, exits successfully.
   * @returns {object} This instance, for chaining.
   * @private
   */
  _stopFromSocket() {
    if (this.get('isStreaming')) {
      let dest = this.get('_resolvedSocketConfig.cancelDestination'),
        id = this.get('_resolvedSocketRequestParams.id');
      if (dest && id) {
        this._connection.send(dest, {}, { id, cancel: true });
        this.set('isStreaming', false);
      }
    }
    if (this._connection) {
      this._connection.disconnect();
      this._connection = null;
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
    let response = message && message.body,
      request = response && response.request;

    // If we require response ids, validate that the response & request ids match.
    if (this.get('requireRequestId')) {
      if (this.get('_resolvedSocketRequestParams.id') !== (request && request.id)) {
        Ember.Logger.warn('Received stream response with unexpected request id. Discarding it.\n', response);
        return;
      }
    }

    if (Ember.typeOf(response.code) !== 'undefined' && response.code !== 0) {
      // The response has an error code; update stream properties & notify observers.
      this.setProperties({
        errorCode: response.code,
        isStreaming: false
      });
      this.error(response);

    } else {

      // The response has no error code; update stream properties & notify observers.
      let { data, meta } = response,

        // The `count` is just a running counter computed from measuring `response.data.length`.
        added = (data && data.length) || 0,
        count = added + this.get('count'),

        // The `total` property is read from the optional `response.meta`, if given.
        total = meta && meta.total,

        // The `goal` property is derived from `total`, `page.index` & `page.size`, if given.
        goal = total;
      if (total && request) {
        let { page } = request;
        if (page && page.size) {  // Watch out for page.size === 0, which means fetch all records.
          goal = Math.min(page.size, total - page.index);
        }
      }

      // The `progress` property is derived from `count` and `goal`, if `goal` is known. If `goal` is not known,
      // `progress` should be left undefined; we don't want to set it to 100 because then the stream will think it
      // is complete, and will stop listening for more messages.
      let progress = goal ? parseInt(100 * count / goal, 10) : undefined;

      // Store these properties on the stream instance, as well as any properties in the `response.meta`.
      this.setProperties(
        Ember.merge({
          count,
          total,
          goal,
          progress,
          isStreaming: (progress === undefined) || (progress < 100)
        }, meta || {}));

      this.next(response);
      if (progress >= 100) {
        this.completed(response);
      }
    }
  }
});
