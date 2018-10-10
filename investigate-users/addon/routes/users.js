import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initTabs } from 'investigate-users/actions/initialization-creators';
import { getUserFilter } from 'investigate-users/reducers/users/selectors';

export default Route.extend({
  redux: service(),

  model() {
    const redux = this.get('redux');
    const filter = getUserFilter(redux.getState());
    redux.dispatch(initTabs('users', filter));
  }
});