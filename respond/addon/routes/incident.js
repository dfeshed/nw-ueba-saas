import Ember from 'ember';

const {
  Route,
  inject: {
    service
  }
} = Ember;

export default Route.extend({
  i18n: service(),

  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('respond.title') });
  },

  model({ incident_id }) {
    return {
      incidentId: incident_id
    };
  }
});
