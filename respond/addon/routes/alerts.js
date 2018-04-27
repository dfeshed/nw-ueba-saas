import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializeAlerts } from 'respond/actions/creators/alert-creators';

export default Route.extend({
  accessControl: service(),
  contextualHelp: service(),
  i18n: service(),
  redux: service(),

  titleToken() {
    return this.get('i18n').t('respond.entities.alerts');
  },

  beforeModel() {
    if (!this.get('accessControl.hasRespondAlertsAccess')) {
      this.transitionTo('index');
    }
  },

  model() {
    const redux = this.get('redux');
    redux.dispatch(initializeAlerts());
  },


  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.respAlrtListVw'));
  },

  deactivate() {
    this.set('contextualHelp.topic', null);
  }
});
