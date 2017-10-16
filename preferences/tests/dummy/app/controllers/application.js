import Ember from 'ember';
import service from 'ember-service/inject';

const { Controller } = Ember;

export default Controller.extend({
  queryParams: ['source'],
  source: 'events',
  eventBus: service(),

  actions: {
    togglePreferencesPanel(launchFor) {
      this.get('eventBus').trigger('toggle-rsa-preferences-panel', launchFor);
    }
  }

});
