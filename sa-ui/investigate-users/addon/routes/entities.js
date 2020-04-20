import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initTabs } from 'investigate-users/actions/initialization-creators';
import { getUserFilter } from 'investigate-users/reducers/users/selectors';

export default Route.extend({
  redux: service(),
  contextualHelp: service(),

  deactivate() {
    this.set('contextualHelp.topic', null);
  },
  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.invEntitiesList'));
  },

  model() {
    const redux = this.get('redux');
    const filter = getUserFilter(redux.getState());
    redux.dispatch(initTabs('users', filter));
  }
});