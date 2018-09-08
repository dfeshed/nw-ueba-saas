import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializePolicy } from 'admin-source-management/actions/creators/policy-creators';

export default Route.extend({
  redux: service(),

  model() {
    const redux = this.get('redux');
    redux.dispatch(initializePolicy());
  },

  actions: {
    transitionToPolicies() {
      this.transitionTo('policies');
    }
  }
});
