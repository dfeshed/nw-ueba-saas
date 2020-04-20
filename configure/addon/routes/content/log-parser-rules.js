import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import { initializeLogParserRules } from 'configure/actions/creators/content/log-parser-rule-creators';

export default Route.extend({
  redux: inject(),
  accessControl: inject(),
  contextualHelp: inject(),
  i18n: inject(),

  titleToken() {
    return this.get('i18n').t('configure.logsParser.pageTitle');
  },

  beforeModel() {
    if (!this.get('accessControl.hasLogParsersAccess')) {
      this.transitionToExternal('protected');
    }
  },

  model() {
    const redux = this.get('redux');
    redux.dispatch(initializeLogParserRules());
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.contentModule'));
    this.set('contextualHelp.topic', this.get('contextualHelp.contentOverview'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
    this.set('contextualHelp.topic', null);
  }
});
