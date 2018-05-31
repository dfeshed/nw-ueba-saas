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

  this.route('protected', { path: '/' }, function() {
    this.route('monitor');
    this.route('packager');
    this.mount('respond');
    this.mount('configure');
    this.mount('investigate');
  });

  this.route('not-found', { path: '/*path' });
  this.mount('admin', { path: 'manage' });
});

export default Router;
