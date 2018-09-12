import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { run } from '@ember/runloop';
import { initializePolicy } from 'admin-source-management/actions/creators/policy-wizard-creators';

export default Route.extend({
  redux: service(),

  model({ policy_id }) {
    run.next(() => {
      this.get('redux').dispatch(initializePolicy(policy_id));
    });
    return {
      policyId: policy_id
    };
  },

  actions: {
    transitionToPolicies() {
      this.transitionTo('policies');
    }
  }
});