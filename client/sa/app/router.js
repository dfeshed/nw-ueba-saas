import Ember from 'ember';
import config from './config/environment';

const Router = Ember.Router.extend({
  location: config.locationType
});

Router.map(function() {
  this.route('login');
  this.route('protected', { path: '/do' }, function() {
    this.route('monitor');
    if (config.featureFlags['show-respond-route']) {
      this.route('respond', function() {
        this.route('incident', { path: '/incident/:incidentId' });
      });
    }
    this.route('explore');
    this.route('admin');
    this.route('not-found', { path: '*invalidprotectedpath' });
  });
  this.route('not-found', { path: '*invalidrootpath' });
});

export default Router;
