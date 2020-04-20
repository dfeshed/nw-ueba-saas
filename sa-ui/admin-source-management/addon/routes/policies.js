import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import policiesCreators from 'admin-source-management/actions/creators/policies-creators';

export default Route.extend({
  redux: inject(),
  contextualHelp: inject(),

  model() {
    const redux = this.get('redux');
    redux.dispatch(policiesCreators.initializePolicies());
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.usmModule'));
    this.set('contextualHelp.topic', this.get('contextualHelp.usmPolicies'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
    this.set('contextualHelp.topic', null);
  }
});
