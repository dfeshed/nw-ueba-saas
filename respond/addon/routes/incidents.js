import Route from 'ember-route';

export default Route.extend({
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
