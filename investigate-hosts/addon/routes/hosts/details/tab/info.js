import Route from '@ember/routing/route';
import { getProcessDetails } from 'investigate-hosts/actions/data-creators/process';
import { inject as service } from '@ember/service';

export default Route.extend({

  redux: service(),

  model(params) {
    const redux = this.get('redux');
    const parentParam = this.modelFor('hosts.details.tab');
    const { rowId } = params;
    if (rowId) {
      redux.dispatch(getProcessDetails(rowId));
    }
    return { ...params, ...parentParam };
  }
});
