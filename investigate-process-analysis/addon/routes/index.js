import { lookup } from 'ember-dependency-lookup';
import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initProcessAnalysis } from 'investigate-process-analysis/actions/creators/init-creators';
import { getServices } from 'investigate-process-analysis/actions/creators/services-creators';
import { run } from '@ember/runloop';

export default Route.extend({

  redux: service(),

  queryParams: {
    pn: { refreshModel: true }, // process name
    aid: { refreshModel: true }, // agent id
    checksum: { refreshModel: true }, // checksum
    st: { refreshModel: true }, // start time
    et: { refreshModel: true }, // end time
    sid: { refreshModel: true }, // service id
    vid: { replace: true }, // process identification
    hn: { refreshModel: true }, // host name
    serverId: { refreshModel: true } // serverId
  },

  model(params) {
    const redux = this.get('redux');
    const request = lookup('service:request');
    const { selectedServerId } = redux.getState().processAnalysis.processTree;
    const { serverId } = params;
    if (serverId) {
      request.registerPersistentStreamOptions({ socketUrlPostfix: selectedServerId ? selectedServerId : serverId, requiredSocketUrl: 'endpoint/socket' });
    }
    run.next(() => {
      redux.dispatch(initProcessAnalysis(params));
      redux.dispatch(getServices());
    });
    document.title = '';
  },
  activate() {
    // To hide the navigation header in process analysis window.
    document.getElementsByTagName('body')[0].classList.add('process-analysis');
  },
  deactivate() {
    document.getElementsByTagName('body')[0].classList.remove('process-analysis');
    const request = lookup('service:request');
    request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
  },

  actions: {
    executeQuery() {
      const redux = this.get('redux');
      const { startTime, endTime, serviceId } = redux.getState().processAnalysis.query;
      this.transitionTo({ queryParams: { sid: serviceId, st: startTime, et: endTime } });
    }
  }
});
