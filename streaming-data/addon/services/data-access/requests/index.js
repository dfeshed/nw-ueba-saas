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
 * For details see streaming-data README
 *
 * @param {string} options.method used to indentify socket condfiguration in the app's config/environment.js
 * @param {string} options.modelName The type of model (i.e., data record) that is being requested. This will be used
 *  to look up a corresponding socket configuration in the app's `config/environment.js` file.
 * @param {object} options.query Arbitrary hash of inputs for the query.
 * @param {function} options.onResponse callback that is called when the stream returns data. The response object
 *  from the stream is passed to this callback
 * @param {object} [options.streamOptions] Optional hash of configuration properties for the stream constructor.
 * @param {function} [options.onInit] callback that is called when the stream is about to start.
 * @param {function} [options.onCompleted] callback that is called when the stream completes. Nothing is passed to
 *  this function
 * @param {function} [options.onError] callback that is called if the stream errors out. The bad response is passed
 *  to this callback
 * @param {string} routeName the route on which the request is being made
 * @returns {undefined}
 * @public
 */
const pagedStreamRequest = (options, routeName) => {
  return function() {

    assert('Cannot call pagedStreamRequest without onResponse', options.onResponse);
    _baseAsserts(options.method, options.modelName, options.query, 'pagedStreamRequest');

    const onReject = options.onError || function(response) {
      warn(
        `Unhandled error in stream, method: ${options.method}, modelName: ${options.modelName}, code: ${response.code}`,
        { id: 'stremaing-data.request.pagedStreamRequest' }
      );
    };

    // no marker for first page, so leave that undefined
    const markers = [undefined];

    // Indicates if the last page has been received.
    // Once set to true, stays true, so if last page
    // is received again, we do not duplicate
    // 'completed' functionality
    let hasBeenCompleted = false;

    // A 1-based page number
    let currentPage = 1;

    const originalFilters = options.query.filter ? [ ...options.query.filter ] : [];

    const functionMakePageRequest = () => {

      const pageMarker = markers[currentPage - 1];

      // undefined means first page, no marker
      if (pageMarker === undefined) {
        options.query.filter = originalFilters;
      } else {
        // otherwise need to create a marker for the SEND request for subsequent calls
        // eg: { "filter": [.... {"field": "marker","value": "44"}]
        options.query.filter = [
          ...originalFilters,
          {
            field: 'marker',
            value: pageMarker
          }
        ];
      }

      // close over currentPage so subsequent requests do not alter it
      // while this request is still being processed
      const promiseResponse = function(currentPage) {

        return (response) => {

          // Much depends on meta being present
          if (response.meta) {
            const { complete } = response.meta;
            // Check if MT flagging this response as too big
            const isItemTooLarge = response.meta['REACTIVE-MESSAGE-TRUNCATED'];
            // If ths is the first time the stream has indicated it
            // it is complete, then fire the callback
            if (complete === true && !hasBeenCompleted) {
              hasBeenCompleted = true;
              if (options.onCompleted) {
                options.onCompleted();
              }
            }

            // if the current page we are requesting is
            // for a page of results we haven't seen yet,
            // and the meta has a marker in it, then
            // append the new page marker to the marker list
            if (currentPage === markers.length) {
              markers.push(response.meta.marker);
            }

            setCursorFlags(response.meta.marker, isItemTooLarge);
          }

          options.onResponse(response);
        };
      }(currentPage);

      promiseRequest(options, routeName, 'pagedStreamRequest')
        .then(promiseResponse)
        .catch(onReject);
    };

    // returned to client, is the API to interact with pages of
    // the stream
    const cursor = {
      canFirst: false,
      canPrevious: false,
      canNext: false,
      canLast: false,
      itemTooLarge: false,
      first() {
        if (cursor.canFirst) {
          currentPage = 1;
          functionMakePageRequest();
        }
      },
      previous() {
        if (cursor.canPrevious) {
          currentPage--;
          functionMakePageRequest();
        }
      },
      next() {
        if (cursor.canNext) {
          currentPage++;
          functionMakePageRequest();
        }
      },
      last() {
        if (!hasBeenCompleted) {
          throw new Error('Last page has not yet been encountered, you cannot call Cursor.last()');
        }

        if (cursor.canLast) {
          currentPage = markers.length - 1;
          functionMakePageRequest();
        }
      }
    };

    // set flags inside the cursor for use both internally and by user
    const setCursorFlags = (currentMarker, isItemTooLarge = false) => {
      const numberOfMarkers = markers.length;
      const indexOfCurrentMarker = markers.lastIndexOf(currentMarker);
      // The caller to the pagedStreamRequest needs to know if the item is too big so that it can be handled correctly with an appropriate message on the UI.
      cursor.itemTooLarge = isItemTooLarge;
      // only 1 marker then all flags stay with default (false)
      if (numberOfMarkers > 1) {
        const notOnFirstPage = indexOfCurrentMarker > 1;
        cursor.canFirst = notOnFirstPage;
        cursor.canPrevious = notOnFirstPage;

        const onLastPage = hasBeenCompleted && indexOfCurrentMarker === numberOfMarkers - 1;
        cursor.canNext = !onLastPage;

        cursor.canLast = hasBeenCompleted && indexOfCurrentMarker < numberOfMarkers - 1;
      }
    };

    // kick off the first request and return the cursor,
    // subsequent requests take place via cursor function calls
    functionMakePageRequest();

    return cursor;
  }();
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
 * @param {string} rootCall where promiseRequest call originated from, used when promiseRequest executed
 *  internally to provide better error messaging
 * @returns {RSVP.Promise}
 * @public
 */
const promiseRequest = ({
    method,
    modelName,
    query,
    onInit,
    streamOptions = {}
  }, routeName, rootCall = 'promiseRequest') => {

  if (routeName === undefined || routeName === '') {
    routeName = _missingRouteNameWarning(rootCall, method, modelName);
  }

  _baseAsserts(method, modelName, query, rootCall);

  // Create the object required to create correct socket connection
  const stream = Socket.createStream(method, modelName, query, streamOptions);

  // register the stream with the cache for future maintenance
  StreamCache.registerStream(stream, method, modelName, routeName, streamOptions);

  return new RSVP.Promise((resolve, reject) => {
    let hangup;

    // Start stream/socket connection
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
      .done((response) => {
        // If socket url mapping not configured in nignx, then nginx is serving the html file. So need to reject
        if (Object.prototype.toString.call(response) === '[object String]' && response.indexOf('!DOCTYPE html') > 0) {
          reject();
        } else {
          resolve();
        }
      })
      .fail(() => {
        reject();
      });
  });
};

export {
  promiseRequest,
  streamRequest,
  pagedStreamRequest,
  ping
};
