import Route from 'ember-route';
import service from 'ember-service/inject';

import { initializeServices } from 'investigate-events/actions/data-creators';

export default Route.extend({
  accessControl: service(),
  redux: service(),

  beforeModel() {
    // Re-route back to the parent's protected route if we don't have permission
    if (!this.get('accessControl.hasInvestigateAccess')) {
      this.transitionToExternal('protected');
    } else {
      // Get services
      this.get('redux').dispatch(initializeServices());
    }
  }
});
