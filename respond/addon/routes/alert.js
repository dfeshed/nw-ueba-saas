import Route from 'ember-route';

export default Route.extend({
  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('respond.title') });
  },
  model({ alert_id }) {
    return {
      alertId: alert_id
    };
  }
});
