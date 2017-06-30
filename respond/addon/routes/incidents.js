import Route from 'ember-route';
import service from 'ember-service/inject';

export default Route.extend({
  accessControl: service(),
  contextualHelp: service(),
  i18n: service(),

  titleToken() {
    return this.get('i18n').t('respond.entities.incidents');
  },

  beforeModel() {
    // TODO: we should use more complex redirects here, but we're just going to send back to / for now
    if (!this.get('accessControl.hasRespondIncidentsAccess')) {
      this.transitionTo('index');
    }
  },

  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.respIncListVw'));
  },

  deactivate() {
    this.set('contextualHelp.topic', null);
  }
});
