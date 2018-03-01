import ERouter from '@ember/routing/router';
import config from './config/environment';

const Router = ERouter.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.mount('investigate-hosts');
  this.route('events');
  this.route('files');
  this.route('hosts');
});

export default Router;
