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

  actions: {
    /**
     * Transitions to the incident details page
     * @public
     * @param incidentId
     */
    viewIncidentDetails(incidentId) {
      this.transitionTo('incident', incidentId);
    }
  }
});
