import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { ping } from 'streaming-data/services/data-access/requests';

export default Route.extend({
  accessControl: service(),

  beforeModel() {
    if (!this.get('accessControl.hasInvestigateHostsAccess')) {
      window.location = '/investigate'; // Redirecting to parent route
    }
  },

  model() {
    return ping('endpoint-server-ping')
      .catch(function() {
        return { endpointServerOffline: true };
      });
  }
});
