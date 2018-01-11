import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  serviceData: undefined,
  isServicesLoading: undefined,
  isServicesRetrieveError: undefined,
  summaryData: undefined,
  isSummaryRetrieveError: false,
  summaryErrorMessage: undefined,
  isSummaryLoading: false
});

export default handleActions({
  [ACTION_TYPES.INITIALIZE_TESTS]: (state, { payload }) => {
    return _initialState.merge(payload.services);
  },

  [ACTION_TYPES.SERVICES_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('isServicesLoading', true),
      failure: (s) => s.merge({ isServicesRetrieveError: true, isServicesLoading: false }),
      success: (s) => s.merge({ serviceData: action.payload.data, isServicesLoading: false, isServicesRetrieveError: false })
    });
  },

  // Handles the results from a Promise call to fetch summary attributes for a given
  // service.
  [ACTION_TYPES.SUMMARY_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ isSummaryLoading: true, summaryData: undefined, isSummaryRetrieveError: false, summaryErrorMessage: undefined }),
      failure: (s) => s.merge({ isSummaryLoading: false, isSummaryRetrieveError: true, summaryErrorMessage: action.payload.meta.message }),
      success: (s) => s.merge({
        isSummaryLoading: false,
        summaryData: action.payload.data
      })
    });
  }
}, _initialState);
