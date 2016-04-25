/**
 * @file Stream.fromSocket extension.
 * Enables a Stream class to instantiate a Stream using an RSA websocket stream request as its data source.
 * Each response (STOMP message) that is received for the request will be streamed to the stream's observers.
 * The socket stream is expected to follow the RSA stream protocol for SA UI; @see https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/docs/websocket-protocol.md
 * @public
 */
import Ember from 'ember';
import config from 'sa/config/environment';

// Utility that looks up the socket config for a modelName-method pair.
// Applies the default socketUrl for the modelName if missing from a modelName-method config.
function _findSocketConfig(modelName, method) {
  let modelConfig = ((config.socketRoutes || {})[modelName] || {}),
    methodConfig = modelConfig[method];
  if (methodConfig) {
    methodConfig.socketUrl = methodConfig.socketUrl || modelConfig.socketUrl;
  }
  return methodConfig;
}

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
let _requestCounter = 0,

  /**
   * Cache of instantiated stream objects.
   * Needed in order to search for a stream object by its current request id.
   * Stream instances add themselves to the cache when they are init'ed, and remove themselves from the
   * cache when they are destroyed.
   * @type []
   * @private
   */
  _socketStreams = [];

/**
 * Handler for server responses to a data stream request. Responsible for invoking appropriate callbacks of the
 * Stream object that requested this response.
 * If the response has an error code, invokes the Stream's onError handler.
 * Otherwise, invokes the Stream's onNext handler, and if the response has metadata that indicates that there
 * are no more chunks of data coming, then invokes the Stream's onComplete handler as well.
 * @param {object} message The STOMP message with a response for a data stream.
 * @private
 */
function _onmessage(message) {

  // To find the Stream instance that requested this response, lookup the request id.
  let response = message && message.body,
    request = response && response.request,
    id = request && request.id,
    stream = id && _socketStreams.find(function(obj) {
        return obj.get('socketRequestParams.id') === id;
      });

  if (!stream) {
    Ember.Logger.warn('Received stream response with unexpected request id. Discarding it.\n', response);
    return;
  }

  if (response.code !== 0) {

    // The response has an error code; update stream properties & notify observers.
    stream.setProperties({
      errorCode: response.code,
      isStreaming: false
    });
    stream.error(response);
  } else {

    // The response has no error code; update stream properties & notify observers.
    let { data, meta } = response,
      added = (data && data.length) || 0,
      count = added + stream.get('count'),
      total = meta && meta.total,
      goal = total;
    if (total && request) {
      let { page } = request;
      if (page) {
        goal = Math.min(page.size, total - page.index);
      }
    }
    let progress = goal ? parseInt(100 * count / goal, 10) : 100;

    stream.setProperties({
      count,
      goal,
      progress,
      total,
      isStreaming: progress < 100
    });

    stream.next(response);
    if (progress >= 100) {
      stream.completed(response);
    }
  }
}

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
    } else {
      this._super();
    }
  },

  // Extend stop by calling _stopFromSocket if we are streaming a socket.
  stop() {
    if (this._startedFromSocket) {
      this._startedFromSocket = false;
      this._stopFromSocket();
    } else {
      this._super();
    }
  },

  /**
   * Submits a server request for streaming data.  This involves 3 steps:
   * (1) obtaining a connection to the socket server;
   * (2) obtaining a subscription to a destination over that connection; and
   * (3) sending a message requesting the specific data query to be sent over that subscription.
   * Returns a Stream object immediately (synchronously) which is later populated as results arrive (asynchronously).
   * @returns {object} This instance, for chaining.
   * @private
   */
  _startFromSocket() {

    // If we are have already started, ignore & exit.
    if (this.get('isStreaming')) {
      return this;
    }

    // Validate configuration: cancelDestination is optional, but the other destinations aren't.
    let cfg = this._resolveSocketConfig();
    if (!cfg || !cfg.socketUrl || !cfg.subscriptionDestination || !cfg.requestDestination) {
      throw('Invalid socket stream configuration.');
    }

    // Validate params: Ensure given params always include an id & stream.limit; set them if necessary.
    let params = Ember.merge(
      Ember.merge({}, cfg.defaultQueryParams || {}),
      this.get('socketRequestParams') || {}
    );
    params.id = params.id || `req-${_requestCounter++}`;
    params.stream = params.stream || {};
    params.stream.limit = params.stream.limit || DEFAULT_STREAM_LIMIT;
    this.set('socketRequestParams', params);

    // Connect to socket server.
    let me = this;
    this._connection = this.get('websocket').connect(cfg.socketUrl)
      .then(function(conn) {

        // Subscribe to destination.
        let sub = conn.subscribe(cfg.subscriptionDestination, _onmessage);
        // Send query message for the stream.
        sub.send({}, params, cfg.requestDestination);
        me.setProperties({
          count: 0,
          progress: 0,
          isStreaming: true,
          page: params.page
        });
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
      let dest = this.get('config.cancelDestination'),
        id = this.get('params.id');
      if (dest && id) {
        this._connection.send(dest, {}, { id, cancel: true });
        this.set('isStreaming', false);
      }
    }
    return this;
  },

  /**
   * Reads config hash from `socketConfig`; if missing, tries to look it up in config/environment under the
   * keys specified by `socketConfigType`.
   * @returns {}
   * @private
   */
  _resolveSocketConfig() {
    let cfg = this.get('socketConfig');
    if (!cfg) {
      let { modelName, method } = this.get('socketConfigType') || {};
      cfg = _findSocketConfig(modelName, method);
    }
    return cfg;
  },

  init() {
    this._super(...arguments);
    _socketStreams.push(this);
  },

  destroy() {
    _socketStreams.removeObject(this);
    this._super(...arguments);
  }
});
