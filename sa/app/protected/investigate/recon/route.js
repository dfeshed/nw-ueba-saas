import Route from 'ember-route';
import service from 'ember-service/inject';

export default Route.extend({
  accessControl: service(),

  queryParams: {
    eventId: {
      refreshModel: false
    },
    endpointId: {
      refreshModel: false
    }
  },

  beforeModel() {
    if (!this.get('accessControl.hasInvestigateEventsAccess')) {
      this.transitionTo('protected.investigate.investigate-events.permission-denied');
    }
  }

});
