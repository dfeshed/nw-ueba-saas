import Ember from 'ember';
import config from './config/environment';

const { Router: EmberRouter } = Ember;

const Router = EmberRouter.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.route('login');

  this.route('protected', { path: '/' }, function() {
    this.route('monitor');

    if (config.featureFlags['show-respond-route']) {
      this.route('respond', function() {
        this.route('incident', { path: '/incident/:incident_id' }, function() {
          this.route('details', { path: '/details/:detail_id' }, function() {
          });
        });
      });
    }

    if (config.featureFlags['show-investigate-route']) {
      this.mount('investigate');
    }

    if (config.featureFlags['show-live-content-route']) {
      this.route('configure', function() {
        this.route('live-content', function() {
          this.route('search');
          this.route('jobs');
          this.route('deployed');
          this.route('updates');
          this.route('feeds');
          this.route('custom');
        });
      });
    }
  });

  this.route('not-found', { path: '/*path' });
});

export default Router;
