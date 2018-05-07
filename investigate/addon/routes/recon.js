/* Route for standalone Recon route */

import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

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
      this.transitionTo('investigate.investigate-events.permission-denied');
    }
  }

});
