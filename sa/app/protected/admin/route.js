import config from 'sa/config/environment';
import Ember from 'ember';

const { Route } = Ember;

export default Route.extend({
  setupController(controller) {
    controller.set('name', config.APP.name);
    controller.set('version', config.APP.version);
  }
});
