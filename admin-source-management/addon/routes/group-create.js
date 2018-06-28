import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializeGroup } from 'admin-source-management/actions/data-creators/group-creators';

export default Route.extend({
  redux: service(),

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
