import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initProcessAnalysis } from 'investigate-process-analysis/actions/creators/init-creators';
import { run } from '@ember/runloop';

export default Route.extend({

  redux: service(),

  queryParams: {
    pn: { refreshModel: true }, // process name
    aid: { refreshModel: true },  // agent id
    checksum: { refreshModel: true },  // checksum
    st: { refreshModel: true },  // start time
    et: { replace: true },      // end time
    sid: { replace: true }        // service id
  },

  model(params) {
    const redux = this.get('redux');
    run.next(() => {
      redux.dispatch(initProcessAnalysis(params));
    });
  }
});
