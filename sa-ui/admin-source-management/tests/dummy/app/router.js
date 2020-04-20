import EmberRouter from '@ember/routing/router';
import config from './config/environment';

const Router = EmberRouter.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.mount('admin-source-management', { path: '/usm' });
  this.route('protected');
  // this.route('engine2PrettyPath');
  // this.route('engine3PrettyPath');
});

export default Router;
