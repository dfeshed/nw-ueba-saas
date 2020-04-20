import ERouter from '@ember/routing/router';
import config from './config/environment';

export default class Router extends ERouter {
  location = config.locationType;
  rootURL = config.rootURL;
}

Router.map(function() {
  this.mount('springboard');
});