import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import policiesCreators from 'admin-source-management/actions/creators/policies-creators';

export default Route.extend({
  redux: inject(),
  model() {
    const redux = this.get('redux');
    redux.dispatch(policiesCreators.fetchPolicyList());
  }
});
