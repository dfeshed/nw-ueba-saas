import Ember from 'ember';
import service from 'ember-service/inject';

const { Controller } = Ember;

export default Controller.extend({
  queryParams: ['source'],
  source: 'investigate',
  eventBus: service(),

  actions: {
    togglePreferencesPanel(source) {
      this.get('eventBus').trigger(`rsa-preferences-panel-${source}-toggled`);
    }
  }

});
