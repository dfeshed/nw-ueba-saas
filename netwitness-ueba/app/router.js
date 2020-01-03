import EmberRouter from '@ember/routing/router';
import { inject as service } from '@ember/service';
import config from './config/environment';

const Router = EmberRouter.extend({

  headData: service(),

  location: config.locationType,
  rootURL: config.rootURL,

  setTitle(title) {
    this.get('headData').set('_title', title);
  }

});

Router.map(function() {
  this.route('login');
  this.route('internal-error');
  this.route('sso-oauth');
  this.route('sso-error');
  this.route('sso-logout');

  this.route('protected', { path: '/' }, function() {
    this.mount('investigate-users', { path: 'investigate/entities' });
  });

  this.route('not-found', { path: '/*path' });
});

export default Router;
