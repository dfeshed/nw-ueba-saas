import Route from 'ember-route';
import service from 'ember-service/inject';

export default Route.extend({
  accessControl: service(),
  beforeModel() {
    // check permissions and route either to the first route to which the user has permission, or re-route back
    // up to the parent's protected route
    if (this.get('accessControl.hasRespondIncidentsAccess')) {
      this.transitionTo('incidents');
    } else if (this.get('accessControl.hasRespondAlertsAccess')) {
      this.transitionTo('alerts');
    } else if (this.get('accessControl.hasRespondRemediationAccess')) {
      this.transitionTo('remediation');
    } else {
      this.transitionToExternal('protected');
    }
  }
});
