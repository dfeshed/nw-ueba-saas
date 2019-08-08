import Route from '@ember/routing/route';
import { getMFTDetails } from 'investigate-hosts/actions/data-creators/host-details';
import { getFilter } from 'investigate-shared/actions/data-creators/filter-creators';
import { inject as service } from '@ember/service';

export default Route.extend({

  redux: service(),

  model(params) {
    const redux = this.get('redux');
    const parentParam = this.modelFor('hosts.details.tab');
    const { mftName, mftFile } = params;
    if (mftName) {
      redux.dispatch(getFilter(() => {}, 'MFTDIRECTORY'));
      redux.dispatch(getMFTDetails(mftName, mftFile));
    }
    return { ...params, ...parentParam };
  }
});
