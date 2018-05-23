import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import policyCreators from 'admin-source-management/actions/data-creators/policy';

export default Route.extend({
  redux: inject(),
  model() {
    const redux = this.get('redux');
    redux.dispatch(policyCreators.fetchPolicyList());
  }
});
