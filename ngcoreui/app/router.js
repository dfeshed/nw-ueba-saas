import EmberRouter from '@ember/routing/router';
import config from './config/environment';

const Router = EmberRouter.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.route('index', { path: '/' });
  this.route('logs');
  // Catch all unknown paths and put them through tree, handle unknown paths there
  this.route('tree', { path: '*path' });
});

export default Router;
