import Ember from 'ember';
import { StreamCache } from '../streams';
import Socket from '../sockets';

const {
  assert,
  Logger,
  RSVP,
  run
} = Ember;


/*
  * Base set of asserts for calls into promiseRequest and streamRequest
  * @private
  */
const _baseAsserts = (method, modelName, query, name) => {
  assert(`Cannot call ${name} without method`, method);
  assert(`Cannot call ${name} without modelName`, modelName);
  assert(`Cannot call ${name} without query`, query);
};

/**
 * API for interfacing with endpoints via streams.
 *
 * This takes various parameters used to interface/interact with streams on websockets,
 * but it abstracts away both websockets and streams (for the most part) from the person
 * using the function.
 *
 * Currently the only leaky concept from streams into code using streamRequest
 * is the `onInit` function is passed a function that can be used to arbitrarily stop
 * the stream. Consider using this when the user has, for instance, a means to trigger
 * a pause to a large amount of data streaming into the UI.
 *
 * To use `streamRequest`, a developer needs to add socket config properties in the `environment.js` file
 * under `socketRoutes[modelName][method]`.  The modelName and method are then provided to this function
 * as input. If this is not done, the call to `Socket.createStream` will throw an exception.
 *
 * @param {string} method used to indentify socket condfiguration in the app's config/environment.js
 * @param {string} modelName The type of model (i.e., data record) that is being requested. This will be used
 *  to look up a corresponding socket configuration in the app's `config/environment.js` file.
 * @param {object} query Arbitrary hash of inputs for the query.
 * @param {function} onResponse callback that is called when the stream returns data. The response object
 *  from the stream is passed to this callback
 * @param {object} [streamOptions] Optional hash of configuration properties for the stream constructor.
 * @param {function} [onInit] callback that is called when the stream is about to start. This callback is
 *  handled a function that can be used to stop the stream's execution
 * @param {function} [onStopped] callback that is called when the stream is stopped before completion. Nothing
 *  is passed to this function
 * @param {function} [onCompleted] callback that is called when the stream completes. Nothing is passed to
 *  this function
 * @param {function} [onError] callback that is called if the stream errors out. The bad response is passed
 *  to this callback
 * @param {string} routeName the route on which the request is being made
 * @returns {undefined}
 * @public
 */
const streamRequest = ({
    method,
    modelName,
    query,
    onResponse,
    streamOptions = {},
    onInit,
    onStopped,
    onCompleted,
    onError
  }, routeName) => {

  _baseAsserts(method, modelName, query, 'streamRequest');
  assert('Cannot call streamRequest without onResponse', onResponse);

  const stream = Socket.createStream(method, modelName, query, streamOptions);
  StreamCache.registerStream(stream, routeName, streamOptions);

  stream.autoStart()
    .subscribe({
      onInit,
      onNext: onResponse,
      onStopped,
      onCompleted,
      onError: onError || function(response) {
        Logger.error(
          `Unhandled error in stream, method: ${method}, modelName: ${modelName}, code: ${response.code}`,
          response);
      }
    });
};

/**
 * API for interfacing with endpoints via promises.
 *
 * This takes various parameters used to interface/interact with streams on websockets but allows
 * the consumer of the API to get a promise in return. The promise will resolve with the first
 * response from the server. If the stream errors out, reject will be called on the promise.
 *
 * Promises cannot resolve twice. So DO NOT use this function if you expect true streaming to occur
 * over time. Only use this if you know the first response from the stream is the only response.
 *
 * This APi abstracts away both websockets and streams from the person as they only need to deal with
 * the returned promise.
 *
 * To use `promiseRequest`, a developer needs to add socket config properties in the `environment.js` file
 * under `socketRoutes[modelName][method]`.  The modelName and method are then provided to this function
 * as input. If this is not done, the call to `Socket.createStream` will throw an exception.
 *
 * IMPORTANT: If `Socket.createStream` throws an exception, this function returns `null` instead of a promise.
 * This allows upstream functions to manage what to do next should there be no configuration.
 *
 * @param {string} method used to indentify socket condfiguration in the app's config/environment.js
 * @param {string} modelName The type of model (i.e., data record) that is being requested. This will be used
 *  to look up a corresponding socket configuration in the app's `config/environment.js` file.
 * @param {object} query Arbitrary hash of inputs for the query.
 * @param {function} [onInit] callback that is called when the stream is about to start. This callback is
 *  handled a function that can be used to stop the stream's execution
 * @param {object} [streamOptions] Optional hash of configuration properties for the stream constructor.
 * @returns {RSVP.Promise}
 * @public
 */
const promiseRequest = ({
    method,
    modelName,
    query,
    onInit,
    streamOptions = {}
  }, routeName) => {
  let stream;
  _baseAsserts(method, modelName, query, 'promiseRequest');

  try {
    stream = Socket.createStream(method, modelName, query, streamOptions);
  } catch (err) {
    return null;
  }

  StreamCache.registerStream(stream, routeName, streamOptions);

  return new RSVP.Promise((resolve, reject) => {
    let hangup;
    stream.autoStart().subscribe({
      onInit(stopStreaming) {
        hangup = stopStreaming;
        if (onInit) {
          onInit(stopStreaming);
        }
      },
      onNext() {
        resolve(...arguments);
        // promise requests are always done once the promise has resolved,
        // cannot resolve promise twice, so call hangup
        run(hangup);
      },
      onError: reject
    });
  });
};

export {
  promiseRequest,
  streamRequest
};