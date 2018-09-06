import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

export default Route.extend({
  accessControl: service(),

  beforeModel() {
    if (!this.get('accessControl.hasInvestigateEventsAccess')) {
      this.transitionTo('permission-denied');
    }
  }
});
