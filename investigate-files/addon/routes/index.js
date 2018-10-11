import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { getEndpointServers } from 'investigate-files/actions/endpoint-server-creators';
import { userLeftFilesPage } from 'investigate-files/actions/data-creators';
import { run } from '@ember/runloop';
import { lookup } from 'ember-dependency-lookup';

export default Route.extend({

  contextualHelp: service(),

  redux: service(),

  model() {
    const redux = this.get('redux');
    run.next(() => {
      redux.dispatch(getEndpointServers());
    });
  },
  // On deactivating the route send the user left page action to cleanup the state if any
  deactivate() {
    const request = lookup('service:request');
    request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
    this.set('contextualHelp.topic', null);
    this.get('redux').dispatch(userLeftFilesPage());
  },
  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.invFiles'));
  }
});
