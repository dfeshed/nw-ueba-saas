import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

export default Route.extend({
  accessControl: service(),
  contextualHelp: service(),
  i18n: service(),

  titleToken() {
    return this.get('i18n').t('respond.entities.alerts');
  },

  beforeModel() {
    // TODO: we should use more complex redirects here, but we're just going to send back to / for now
    if (!this.get('accessControl.hasRespondAlertsAccess')) {
      this.transitionTo('index');
    }
  },

  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.respAlrtListVw'));
  },

  deactivate() {
    this.set('contextualHelp.topic', null);
  }
});
