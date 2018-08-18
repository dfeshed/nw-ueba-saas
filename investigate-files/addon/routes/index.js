import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { lookup } from 'ember-dependency-lookup';
import { getEndpointServers } from 'investigate-files/actions/endpoint-server-creators';
import { run } from '@ember/runloop';

export default Route.extend({

  contextualHelp: service(),

  redux: service(),

  model() {
    const redux = this.get('redux');
    run.next(() => {
      redux.dispatch(getEndpointServers());
    });
    const request = lookup('service:request');
    return request.ping('endpoint-server-ping')
      .catch(function() {
        return { endpointServerOffline: true };
      });
  },
  // On deactivating the route send the user left page action to cleanup the state if any
  deactivate() {
    this.set('contextualHelp.topic', null);
  },
  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.invFiles'));
  }
});
