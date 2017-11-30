import Route from '@ember/routing/route';
import { ping } from 'streaming-data/services/data-access/requests';

export default Route.extend({
  model() {
    return ping('endpoint-server-ping')
      .catch(function() {
        return { endpointServerOffline: true };
      });
  }
});
