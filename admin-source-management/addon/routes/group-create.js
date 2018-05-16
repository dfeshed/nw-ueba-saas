import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import { initializeGroup } from 'admin-source-management/actions/creators/group-creators';

export default Route.extend({
  redux: inject(),

  model() {
    const redux = this.get('redux');
    redux.dispatch(initializeGroup());
  },

  actions: {
    transitionToGroups() {
      this.transitionTo('groups');
    }
  }
});
