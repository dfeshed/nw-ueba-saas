import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import { initializeRule } from 'configure/actions/creators/respond/incident-rule-creators';

export default Route.extend({
  accessControl: inject(),
  contextualHelp: inject(),
  i18n: inject(),
  redux: inject(),

  titleToken() {
    return this.get('i18n').t('configure.incidentRulesTitle');
  },

  beforeModel() {
    if (!this.get('accessControl.respondCanManageAlertRules')) {
      this.transitionToExternal('protected');
    }
  },

  model() {
    const redux = this.get('redux');
    redux.dispatch(initializeRule());
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.respondModule'));
    this.set('contextualHelp.topic', this.get('contextualHelp.incRulesDetailVw'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
    this.set('contextualHelp.topic', null);
  },

  actions: {
    transitionToRules() {
      this.transitionTo('respond.incident-rules');
    }
  }
});
