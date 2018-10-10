import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initialFilterState } from 'investigate-users/reducers/users/selectors';
import { updateFilter } from 'investigate-users/actions/user-tab-actions';

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
    applyUserFilter(filterFor) {
      const filter = initialFilterState.merge(filterFor);
      this.get('redux').dispatch(updateFilter(filter));
      this.transitionTo('users');
    }
  }
});
