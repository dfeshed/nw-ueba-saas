import { lookup } from 'ember-dependency-lookup';
import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import { getEndpointServers } from 'configure/actions/creators/endpoint/server-creator';

export default Route.extend({
  redux: inject(),

  model() {
    const redux = this.get('redux');
    redux.dispatch(getEndpointServers());
  },
  deactivate() {
    const request = lookup('service:request');
    request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
  }
});
