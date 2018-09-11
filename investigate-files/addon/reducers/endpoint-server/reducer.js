import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-files/actions/types';

const initialState = Immutable.from({
  serviceData: undefined,
  isServicesLoading: undefined,
  isServicesRetrieveError: undefined,
  isSummaryRetrieveError: undefined
});

const endpointServer = handleActions({

  [ACTION_TYPES.LIST_OF_ENDPOINT_SERVERS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('isServicesLoading', true),
      failure: (s) => s.merge({ isServicesRetrieveError: true, isServicesLoading: false }),
      success: (s) => {
        const sortedServices = action.payload.data.sort((a, b) => {
          return a.displayName.toLowerCase() > b.displayName.toLowerCase() ? 1 : -1;
        });
        return s.merge({ serviceData: sortedServices, isServicesLoading: false, isServicesRetrieveError: false });
      }
    });
  },
  [ACTION_TYPES.ENDPOINT_SERVER_STATUS]: (state, { payload }) => {
    return state.set('isSummaryRetrieveError', payload);
  }
}, initialState);

export default endpointServer;