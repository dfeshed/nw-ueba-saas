import Ember from 'ember';
import { StreamCache, promiseRequest, streamRequest } from './data-access';

const {
  Service,
  inject: {
    service
  }
} = Ember;

export default Service.extend({

  router: service('-routing'),

  init() {
    this._super();
    // register observer on route changes to allow for stream cleanup
    this.get('router').addObserver('currentRouteName', this, '_routeCleanup');
  },

  /*
   * Called when route changes to see if that route has any streams to stop
   * @private
   */
  _routeCleanup() {
    const newRouteName = this.get('router').get('currentRouteName');
    StreamCache.cleanUpRouteStreams(newRouteName);
  },

  promiseRequest(opts) {
    const routeName = this.get('router').get('currentRouteName');
    return promiseRequest(opts, routeName);
  },

  streamRequest(opts) {
    const routeName = this.get('router').get('currentRouteName');
    streamRequest(opts, routeName);
  }

});