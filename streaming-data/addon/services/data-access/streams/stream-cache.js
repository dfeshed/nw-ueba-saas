/**
 * @file stream-cache
 * Cache of stream connections by route key.
 * Used by request service to manage/clean up streams per route
 * @public
 */

import { run } from '@ember/runloop';

/*
 * Keeps track of stream by route for future cleaning up on route change
 * unless in case where stream should continue
 */
const _streams = {
  ANON: []
};

/**
 * For a given route name, clean up any other streams that should no longer be open.
 *
 * @param {string} newRouteName name of the route that was navigated to
 * @returns {undefined}
 * @public
 */
function cleanUpRouteStreams(newRouteName) {

  // Nuke Anon streams, those registered with no route name
  if (_streams.ANON.length > 0) {
    _streams.ANON.forEach(({ stream }) => {
      run.next(() => stream.stop());
    });
    _streams.ANON = [];
  }

  Object.keys(_streams).forEach((routeName) => {
    // 1) if the new route isn't also the route we are looking at from the cache and
    // 2) the new route isn't a child of a child of a route with streams,
    // then the streams need to be cleaned up
    if (newRouteName !== routeName) {
      const isParentRoute = newRouteName.indexOf(routeName) === 0;
      _streams[routeName].forEach(({ stream, streamOptions }) => {
        if (!isParentRoute || !streamOptions.keepAliveOnTransitionToChildRoute) {
          run.next(() => {
            stream.stop();
            delete _streams[routeName];
          });
        }
      });
    }
  });
}

/**
 * If a stream exists already for the model/method,
 * stop it.
 *
 * @param {String} method the method name for the stream
 * @param {String} modelName the model name for the stream
 * @returns {undefined}
 * @public
 */
function _cleanUpDuplicateStream(_method, _modelName) {
  // create array of all streams
  const allStreams = [];
  Object.keys(_streams).forEach((routeName) => allStreams.push(..._streams[routeName]));

  allStreams.forEach(({ method, modelName, stream }) => {
    if (method === _method && modelName === _modelName) {
      // stream exists that matches modelName/method for the stream
      // that is being created, stop the previous one so that any
      // data coming back from it doesn't cause async problems
      // with new request going out. Not always guaranteed
      // responses come back in order.
      stream.stop();
    }
  });
}

/**
 * Keeps track of stream by route for future cleaning up on route change
 * unless in case where stream should continue
 *
 * @param {Stream} stream the stream being registered
 * @param {String} method the method name for the stream
 * @param {String} modelName the model name for the stream
 * @param {Object} streamOptions options for the stream, including whether the stream should be
 *   kept alive after the route change
 * @param {String} routeName the route for the stream being registered
 * @returns {undefined}
 * @public
 */
function registerStream(stream, method, modelName, routeName, streamOptions) {
  // need to check and see if is identical
  // streaming existing and cancel it
  if (streamOptions.cancelPreviouslyExecuting === true) {
    _cleanUpDuplicateStream(method, modelName);
  }

  if (!streamOptions.keepAliveOnRouteChange) {
    if (routeName) {
      if (!_streams[routeName]) {
        _streams[routeName] = [];
      }
      _streams[routeName].push({ stream, streamOptions, method, modelName });
    } else {
      // lacking route name, so add to anon routes that
      // get cleaned up on any route change
      _streams.ANON.push({ stream, streamOptions, method, modelName });
    }
  }
}

export default {
  _streams, // exposed for testing purposes
  cleanUpRouteStreams,
  registerStream
};
