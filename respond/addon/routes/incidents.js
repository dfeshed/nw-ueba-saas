import Ember from 'ember';

const { Route } = Ember;

export default Route.extend({
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
