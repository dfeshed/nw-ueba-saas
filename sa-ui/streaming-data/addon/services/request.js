import Service, { inject as service } from '@ember/service';
import {
  StreamCache,
  promiseRequest,
  streamRequest,
  pagedStreamRequest,
  Socket,
  ping
} from './data-access';

export default Service.extend({

  router: service('-routing'),

  init() {
    this._super();
    // register observer on route changes to allow for stream cleanup
    this.get('router').addObserver('currentRouteName', this, '_routeCleanup');

    /**
     * @public
     * stores the socketurl prefix
    */
    this.persistentStreamOptions = this.persistentStreamOptions || {};
  },

  /**
   * @public
   * setter to sets the persistentStreamOptions
  */
  registerPersistentStreamOptions(options) {
    const persistentStreamOptions = this.get('persistentStreamOptions');
    this.set('persistentStreamOptions', { ...persistentStreamOptions, ...options });
  },

  /**
   * @public
   * clear properties from the persisted streamOptions
   * @param {Array} [options] persistent options
  */
  clearPersistentStreamOptions(options) {
    const persistentStreamOptions = this.get('persistentStreamOptions') || {};
    const newStreamOptions = { ...persistentStreamOptions };
    if (Array.isArray(options)) {
      options.forEach((option) => {
        if (Object.keys(persistentStreamOptions).some((opt) => option === opt)) {
          delete newStreamOptions[option];
        }
      });
    }
    this.set('persistentStreamOptions', newStreamOptions);
  },

  /*
   * Retrieves the current route name from the routing service. If called while
   * in mid-transition (e.g., from the `model()` hook of a Route) returns the
   * name of the route we are transitioning to. Otherwise, if it were to return
   * the name of the route we are transitioning from, this service might
   * mistakenly think any requests from the `model()` hook belong to the
   * route we are leaving, and thus stop them.
   * @note In theory we could implement this as a computed property (maybe even
   * a `computed.or`) but that yielded a bunch of Ember errors, not sure why.
   * @warning When we do Ember upgrades, it's possible that this function may
   * need to change. Strictly speaking, the routing service and its child objects that
   * we are referencing here are part of a private Ember API (as of Ember 2.8).
   * However the Ember docs do seem to encourage its use and suggest it will be
   * public eventually. So we use them for now and keep an eye out in the future.
   * @private
   */
  _currentRouteName() {
    return this.get('router.router._routerMicrolib.activeTransition.targetName') ||
      this.get('router.currentRouteName') ||
      '';
  },

  /*
   * Called when route changes to see if that route has any streams to stop
   * @private
   */
  _routeCleanup() {
    const routeName = this._currentRouteName();
    StreamCache.cleanUpRouteStreams(routeName);
  },

  /*
   * updates the request opts
   * @private
   */
  _updateOpts(opts) {
    const persistentStreamOptions = this.get('persistentStreamOptions');
    if (!persistentStreamOptions) {
      return opts;
    }

    return {
      ...opts,
      streamOptions: {
        ...persistentStreamOptions,
        ...(opts.streamOptions || {})
      }
    };
  },

  promiseRequest(opts) {
    const routeName = this._currentRouteName();
    return promiseRequest(this._updateOpts(opts), routeName);
  },

  streamRequest(opts) {
    const routeName = this._currentRouteName();
    streamRequest(this._updateOpts(opts), routeName);
  },

  pagedStreamRequest(opts) {
    const routeName = this._currentRouteName();
    return pagedStreamRequest(this._updateOpts(opts), routeName);
  },

  ping(modelName) {
    return ping(modelName, this.get('persistentStreamOptions'));
  },

  /*
   * Disconnects all the STOMP clients currently connected by this service.
   * Typically the caller to this service does not need this method directly.
   * However, we provide it here for edge cases when the caller needs fine
   * control; for example: in ember tests, we need to tell the Socket to close
   * all of its connections at the end of each test.
   */
  disconnectAll() {
    Socket.disconnectAll();
  },

  /*
   * Disconnects a dedicated socket by that sockets name
   */
  disconnectNamed(name) {
    Socket.disconnectNamed(name);
  }
});
