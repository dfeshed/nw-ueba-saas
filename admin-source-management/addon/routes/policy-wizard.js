import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { run } from '@ember/runloop';
import { initializePolicy } from 'admin-source-management/actions/creators/policy-wizard-creators';

export default Route.extend({
  redux: service(),
  contextualHelp: service(),

  model({ policyId }) {
    run.next(() => {
      this.get('redux').dispatch(initializePolicy(policyId));
    });
    return {
      policyId
    };
  },

  actions: {
    transitionToPolicies() {
      this.transitionTo('policies');
    }
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.usmModule'));
    this.set('contextualHelp.topic', this.get('contextualHelp.usmPoliciesWizard'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
    this.set('contextualHelp.topic', null);
  }
});