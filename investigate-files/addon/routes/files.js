import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { userLeftFilesPage } from 'investigate-files/actions/data-creators';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';

export default Route.extend({

  contextualHelp: service(),

  redux: service(),

  // On deactivating the route send the user left page action to cleanup the state if any
  deactivate() {
    const redux = this.get('redux');
    this.set('contextualHelp.topic', null);
    redux.dispatch({ type: SHARED_ACTION_TYPES.RESET_FILTER, meta: { belongsTo: 'FILE' } });
    redux.dispatch(userLeftFilesPage());
  },

  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.invFiles'));
  }
});
