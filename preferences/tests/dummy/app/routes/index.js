import Route from 'ember-route';
import CONFIG from '../config';

export default Route.extend({
  setupController() {
    this.controllerFor('application').set('eventsPreferenceConfig', CONFIG);
  }
});
