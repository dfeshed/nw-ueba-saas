import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import groupsCreators from 'admin-source-management/actions/creators/groups-creators';

export default Route.extend({
  redux: inject(),
  model() {
    const redux = this.get('redux');
    redux.dispatch(groupsCreators.fetchGroups());
  }
});
