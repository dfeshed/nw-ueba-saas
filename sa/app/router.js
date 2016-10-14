import Ember from 'ember';
import config from './config/environment';

const { Router: EmberRouter } = Ember;

const Router = EmberRouter.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.route('login');
  this.route('protected', { path: '/do' }, function() {
    this.route('monitor');
    if (config.featureFlags['show-respond-route']) {
      this.route('respond', function() {
        this.route('incident', { path: '/incident/:incident_id' }, function() {
          this.route('details', { path: '/details/:detail_id' }, function() {});
        });
      });
    }
    if (config.featureFlags['show-investigate-route']) {
      this.route('investigate', function() {
        this.route('query', { path: '/query/*filter' });
        this.route('not-found', { path: '*invalidinvestigatepath' });
      });
    }
    this.route('not-found', { path: '*invalidprotectedpath' });
  });
  this.route('not-found');
});

export default Router;
