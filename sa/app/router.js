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
  this.route('internal-error');

  this.route('protected', { path: '/' }, function() {
    this.route('monitor');
    this.mount('respond');
    this.route('investigate', function() {
      this.mount('investigate-events', { path: 'events' });
    });
  });

  this.route('not-found', { path: '/*path' });
});

export default Router;
