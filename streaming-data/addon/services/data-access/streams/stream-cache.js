/**
 * @file stream-cache
 * Cache of stream connections by route key.
 * Used by request service to manage/clean up streams per route
 * @public
 */

import Ember from 'ember';

const { run } = Ember;

/*
 * Keeps track of stream by route for future cleaning up on route change
 * unless in case where stream should continue
 */
let _streams = {
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
    if (newRouteName != routeName) {
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
 * Keeps track of stream by route for future cleaning up on route change
 * unless in case where stream should continue
 *
 * @param {Stream} stream the stream being registered
 * @param {Object} streamOptions options for the stream, including whether the stream should be
 *   kept alive after the route change
 * @param {String} routeName the route for the stream being registered
 * @returns {undefined}
 * @public
 */
function registerStream(stream, routeName, streamOptions) {
  if (!streamOptions.keepAliveOnRouteChange) {
    if (routeName) {
      if (!_streams[routeName]) {
        _streams[routeName] = [];
      }
      _streams[routeName].push({ stream, streamOptions });
    } else {
      // lacking route name, so add to anon routes that
      // get cleaned up on any route change
      _streams.ANON.push({ stream, streamOptions });
    }
  }
}

export default {
  cleanUpRouteStreams,
  registerStream
};