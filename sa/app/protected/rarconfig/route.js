import Route from '@ember/routing/route';
import { lookup } from 'ember-dependency-lookup';

export default Route.extend({
  queryParams: {
    /**
     * selected serviceId for multi-server endpoint server
     * @type {string}
     * @public
     */
    sid: {
      refreshModel: false,
      replace: true
    }
  },

  model(params) {
    const request = lookup('service:request');
    const { sid } = params;
    request.registerPersistentStreamOptions({ 'socketUrlPostfix': sid, 'requiredSocketUrl': 'endpoint/socket' });
    return request.ping('endpoint-server-ping')
      .then(() => {
        return { endpointServerOffline: false, serverId: sid };
      })
      .catch(function() {
        return { endpointServerOffline: true };
      });
  },

  deactivate() {
    const request = lookup('service:request');
    request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
  }
});