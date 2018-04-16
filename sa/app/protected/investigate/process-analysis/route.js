import Route from '@ember/routing/route';
import { setProcessAnalysisInput } from 'investigate-shared/actions/creators/endpoint/initialization-creators';
import { getEvents } from 'investigate-shared/actions/creators/endpoint/events-creators';

import { run } from '@ember/runloop';
import { inject as service } from '@ember/service';

export default Route.extend({

  redux: service(),

  queryParams: {
    serviceId: {
      refreshModel: false
    },
    agentId: {
      refreshModel: false
    },
    processName: {
      refreshModel: false
    },
    checksum: {
      refreshModel: false
    },
    startTime: {
      refreshModel: false
    },
    endTime: {
      refreshModel: false
    }
  },
  model(params) {
    const redux = this.get('redux');
    run.next(() => {
      redux.dispatch(setProcessAnalysisInput(params));
      redux.dispatch(getEvents());
    });
    return {
      processName: params.processName
    };
  }
});
