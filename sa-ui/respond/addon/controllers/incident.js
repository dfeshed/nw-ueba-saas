import Controller from '@ember/controller';

export default Controller.extend({
  actions: {
    transitionToIncidentRoute() {
      const { incidentId } = this.get('model');
      this.transitionToRoute('incident', incidentId);
    }
  }
});
