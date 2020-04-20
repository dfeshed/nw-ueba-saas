import Route from '@ember/routing/route';
import { getMFTDetails } from 'investigate-hosts/actions/data-creators/host-details';
import { getFilter } from 'investigate-shared/actions/data-creators/filter-creators';
import { inject as service } from '@ember/service';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';

export default Route.extend({

  redux: service(),

  queryParams: {
    mftSid: {
      refreshModel: true
    }
  },

  model(params) {
    const redux = this.get('redux');
    const parentParam = this.modelFor('hosts.details.tab');
    const { mftName, mftFile, mftSid } = params;
    redux.dispatch({ type: SHARED_ACTION_TYPES.SET_DOWNLOAD_FILE_LINK, payload: null });

    if (mftName) {
      redux.dispatch({ type: ACTION_TYPES.SET_MFT_EPS, payload: mftSid });
      redux.dispatch(getFilter(() => {}, 'MFTDIRECTORY'));
      redux.dispatch(getMFTDetails(mftName, mftFile));
    }
    return { ...params, ...parentParam };
  }
});