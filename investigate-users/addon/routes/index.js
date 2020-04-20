import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initTabs } from 'investigate-users/actions/initialization-creators';

export default Route.extend({

  contextualHelp: service(),
  redux: service(),

  // On deactivating the route send the user left page action to cleanup the state if any
  deactivate() {
    this.set('contextualHelp.topic', null);
  },
  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.invEntities'));
  },
  model() {
    const redux = this.get('redux');
    redux.dispatch(initTabs('overview'));
  }
});