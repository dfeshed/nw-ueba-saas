import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { userLeftFilesPage } from 'investigate-files/actions/data-creators';
import { lookup } from 'ember-dependency-lookup';
import * as ACTION_TYPES from '../actions/types';

export default Route.extend({

  contextualHelp: service(),

  redux: service(),

  // On deactivating the route send the user left page action to cleanup the state if any
  deactivate() {
    const redux = this.get('redux');
    const request = lookup('service:request');
    request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
    this.set('contextualHelp.topic', null);
    redux.dispatch({ type: ACTION_TYPES.RESET_FILES });
    redux.dispatch(userLeftFilesPage());
  },

  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.invFiles'));
  }
});
