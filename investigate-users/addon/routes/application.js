import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { updateFilter } from 'investigate-users/actions/user-tab-actions';
import { initiateUser } from 'investigate-users/actions/user-details';
import { updateFilter as updateAlertsFilter } from 'investigate-users/actions/alert-details';
import Immutable from 'seamless-immutable';

export default Route.extend({
  accessControl: service(),
  redux: service(),

  beforeModel() {
    if (!this.get('accessControl.hasInvestigateEventsAccess')) {
      this.transitionTo('permission-denied');
    }
  },
  actions: {
    navigateTo(routeName) {
      this.transitionTo(routeName);
    },
    showUserDetails(userId) {
      this.get('redux').dispatch(initiateUser({ userId }));
    },
    applyUserFilter(filterFor) {
      this.get('redux').dispatch(updateFilter(filterFor, true));
      this.transitionTo('users');
    },
    applyAlertsFilter(filterFor) {
      this.get('redux').dispatch(updateAlertsFilter(Immutable.from(filterFor), true));
      this.transitionTo('alerts');
    }
  }
});