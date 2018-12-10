import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import { getRules } from 'configure/actions/creators/respond/incident-rule-creators';
import { getRiskScoringSettings } from 'configure/actions/creators/respond/risk-scoring-creators';

export default Route.extend({
  accessControl: inject(),
  contextualHelp: inject(),
  i18n: inject(),
  redux: inject(),

  titleToken() {
    return this.get('i18n').t('configure.incidentRulesTitle');
  },

  beforeModel() {
    if (!this.get('accessControl.hasRespondAlertRulesAccess')) {
      this.transitionToExternal('protected');
    }
  },

  model() {
    const redux = this.get('redux');
    redux.dispatch(getRules());
    redux.dispatch(getRiskScoringSettings());
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.respondModule'));
    this.set('contextualHelp.topic', this.get('contextualHelp.incRulesListVw'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
    this.set('contextualHelp.topic', null);
  },

  actions: {
    transitionToRule(ruleId) {
      this.transitionTo('respond.incident-rule', ruleId);
    }
  }
});
