import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializeFileDetails, getAllServices, resetInputData } from 'investigate-files/actions/data-creators';
import { next } from '@ember/runloop';
import { lookup } from 'ember-dependency-lookup';
import { setSelectedEndpointServer } from 'investigate-shared/actions/data-creators/endpoint-server-creators';
export default Route.extend({
  redux: service(),

  queryParams: {
    /**
     * selected sid for multi-server endpoint server
     * @type {string}
     * @public
     */
    sid: {
      refreshModel: true
    },
    /**
     * checksumSha256 for selected file
     * @type {string}
     * @public
     */
    checksum: {
      refreshModel: true
    }
  },

  model(params) {
    const redux = this.get('redux');
    const { checksum, sid } = params;
    const request = lookup('service:request');
    next(() => {
      if (sid) {
        redux.dispatch(resetInputData());
        request.registerPersistentStreamOptions({ socketUrlPostfix: sid, requiredSocketUrl: 'endpoint/socket' });
        redux.dispatch(getAllServices());
        redux.dispatch(setSelectedEndpointServer(sid));
        redux.dispatch(initializeFileDetails(checksum));
      }
    });
  }
});
