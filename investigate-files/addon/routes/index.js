import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { ping } from 'streaming-data/services/data-access/requests';

export default Route.extend({
  accessControl: service(),

  contextualHelp: service(),

  beforeModel() {
    if (!this.get('accessControl.hasInvestigateHostsAccess')) {
      this.transitionToExternal('protected.permission-denied'); // Directing to permission denied page
    }
  },

  model() {
    return ping('endpoint-server-ping')
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
