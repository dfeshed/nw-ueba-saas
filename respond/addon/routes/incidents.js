import Route from 'ember-route';
import service from 'ember-service/inject';

export default Route.extend({
  accessControl: service(),
  contextualHelp: service(),

  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('respond.title') });
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
