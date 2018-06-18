import ERouter from '@ember/routing/router';
import config from './config/environment';

const Router = ERouter.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.mount('configure');
  this.route('protected');
});

export default Router;
