import Route from '@ember/routing/route';
import { inject } from '@ember/service';

export default Route.extend({
  accessControl: inject(),
  contextualHelp: inject(),
  i18n: inject(),

  titleToken() {
    return this.get('i18n').t('respond.entities.incidentRules');
  },

  beforeModel() {
    if (!this.get('accessControl.hasRespondConfigureAccess')) {
      this.transitionTo('index');
    }
  }
});
