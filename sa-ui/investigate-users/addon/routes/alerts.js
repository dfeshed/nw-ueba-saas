import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initTabs } from 'investigate-users/actions/initialization-creators';
import { getFilter } from 'investigate-users/reducers/alerts/selectors';

export default Route.extend({
  redux: service(),

  contextualHelp: service(),

  deactivate() {
    this.set('contextualHelp.topic', null);
  },
  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.invEntitiesAlerts'));
  },

  model() {
    const redux = this.get('redux');
    const filter = getFilter(redux.getState());
    redux.dispatch(initTabs('alerts', filter));
  }
});
