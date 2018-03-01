import Route from '@ember/routing/route';
import CONFIG from '../config';

export default Route.extend({
  setupController() {
    this.controllerFor('application').set('eventsPreferenceConfig', CONFIG);
  }
});
