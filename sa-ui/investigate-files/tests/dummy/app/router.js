import ERouter from '@ember/routing/router';
import config from './config/environment';

const Router = ERouter.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.mount('investigate-files');
  this.route('events');
  this.route('hosts');
  this.route('users');
});

export default Router;
