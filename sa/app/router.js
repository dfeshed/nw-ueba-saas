import Ember from 'ember';
import config from './config/environment';

const {
  Router: EmberRouter,
  inject: {
    service
  }
} = Ember;

const Router = EmberRouter.extend({

  headData: service(),

  location: config.locationType,
  rootURL: config.rootURL,

  setTitle(title) {
    this.get('headData').set('title', title);
  }

});

Router.map(function() {
  this.route('login');

  this.route('protected', { path: '/' }, function() {
    this.route('monitor');

    if (config.featureFlags['show-respond-route']) {
      this.route('responded', function() {
        this.route('incident', { path: '/incident/:incident_id' }, function() {
          this.route('details', { path: '/details/:detail_id' }, function() {
          });
        });
      });
    }

    if (config.featureFlags['show-respond-route']) {
      this.mount('respond');
    }

    if (config.featureFlags['show-investigate-route']) {
      this.mount('investigate');
    }
  });

  this.route('not-found', { path: '/*path' });
});

export default Router;
