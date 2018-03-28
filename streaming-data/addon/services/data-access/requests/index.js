import { assert } from '@ember/debug';
import { deprecate } from 'ember-deprecations';
import { warn } from 'ember-debug';
import RSVP from 'rsvp';
import { run } from '@ember/runloop';
import { StreamCache } from '../streams';
import Socket from '../sockets';
import $ from 'jquery';
import config from 'ember-get-config';

const _missingRouteNameWarning = (fn, method, modelName) => {
  deprecate(`Direct importing of ${fn} is deprecated, please use request service`,
    false,
    {
      id: `${method}.${modelName}`,
      until: '8.0',
      url: 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/investigate-events/addon/actions/fetch/utils.js'
    }
  );
};

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

  if (routeName === undefined || routeName === '') {
    routeName = _missingRouteNameWarning('streamRequest', method, modelName);
  }

  _baseAsserts(method, modelName, query, 'streamRequest');
  assert('Cannot call streamRequest without onResponse', onResponse);

  const stream = Socket.createStream(method, modelName, query, streamOptions);
  StreamCache.registerStream(stream, method, modelName, routeName, streamOptions);

  stream.subscribe({
    onInit,
    onResponse,
    onStopped,
    onCompleted,
    onError: onError || function(response) {
      warn(
        `Unhandled error in stream, method: ${method}, modelName: ${modelName}, code: ${response.code}`,
        { id: 'stremaing-data.request.streamRequest' }
      );
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

  if (routeName === undefined || routeName === '') {
    routeName = _missingRouteNameWarning('promiseRequest', method, modelName);
  }

  _baseAsserts(method, modelName, query, 'promiseRequest');

  try {
    stream = Socket.createStream(method, modelName, query, streamOptions);
  } catch (err) {
    return null;
  }

  StreamCache.registerStream(stream, method, modelName, routeName, streamOptions);

  return new RSVP.Promise((resolve, reject) => {
    let hangup;
    stream.subscribe({
      onInit(stopStreaming) {
        hangup = stopStreaming;
        if (onInit) {
          onInit(stopStreaming);
        }
      },
      onResponse() {
        resolve(...arguments);
        // promise requests are always done once the promise has resolved,
        // cannot resolve promise twice, so call hangup
        run(hangup);
      },
      onError: reject
    });
  });
};

/**
 * Returns the url for the socket info endpoint, which is used here as a health check against the service to ensure
 * that it is running and available.
 * @method _findPingUrl
 * @private
 */
const _findPingUrl = (modelName) => {
  const pingConfig = config.socketRoutes[modelName];
  return `${pingConfig.socketUrl}/info`;
};

/**
 * API for ping to an endpoint. Ping is to test whether endpoint exists or it has access or testing the health of server
 *
 * Input to the ping api is socket configuration model name. Need to add ping socket configuration in environment.js.
 * After finding the socket url (endpoint url) ping will make the ajax call to endpoint and returns the promise to the
 * api consumer. If the endpoint is running and heath is ok then promise will resolve to success else promise will be rejected
 *
 * Promise will be rejected for all the error status (like 403, 404, 500 etc)
 *
 * @param modelName for health check of the endpoint
 * @returns {RSVP.Promise}
 * @public
 */
const ping = (modelName) => {

  assert('Cannot call ping without modelName', modelName);

  const url = _findPingUrl(modelName);
  return new RSVP.Promise((resolve, reject) => {
    $.ajax({ url, cache: false })
      .done(() => {
        resolve();
      })
      .fail(() => {
        reject();
      });
  });
};

export {
  promiseRequest,
  streamRequest,
  ping
};
