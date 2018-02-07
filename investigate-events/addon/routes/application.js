import Route from 'ember-route';
import service from 'ember-service/inject';

export default Route.extend({
  accessControl: service(),
  i18n: service(),

  title() {
    return this.get('i18n').t('pageTitle', {
      section: this.get('i18n').t('investigate.title')
    });
  },

  beforeModel() {
    if (!this.get('accessControl.hasInvestigateEventsAccess')) {
      this.transitionTo('permission-denied');
    }
  }

});
