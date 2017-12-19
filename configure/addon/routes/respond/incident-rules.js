import Route from '@ember/routing/route';
import { inject } from '@ember/service';

export default Route.extend({
  accessControl: inject(),
  contextualHelp: inject(),
  i18n: inject(),

  titleToken() {
    return this.get('i18n').t('configure.incidentRulesTitle');
  },

  beforeModel() {
    if (!this.get('accessControl.respondCanManageAlertRules')) {
      this.transitionToExternal('protected');
    }
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
