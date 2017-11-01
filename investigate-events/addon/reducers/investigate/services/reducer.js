import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  data: undefined,
  isLoading: undefined,
  isError: undefined,
  summaryData: undefined,
  summaryError: false
});

export default handleActions({
  [ACTION_TYPES.INITIALIZE_TESTS]: (state, { payload }) => {
    return _initialState.merge(payload.services);
  },

  [ACTION_TYPES.SERVICES_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('isLoading', true),
      failure: (s) => s.merge({ isError: true, isLoading: false }),
      success: (s) => s.merge({ data: action.payload.data, isLoading: false })
    });
  },

  // Handles the results from a Promise call to fetch summary attributes for a given
  // service.
  [ACTION_TYPES.SUMMARY_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ summaryData: undefined, summaryError: false, summaryErrorMessage: undefined }),
      failure: (s) => s.merge({ summaryError: true, summaryErrorMessage: action.payload.meta.message }),
      success: (s) => s.merge({
        summaryData: action.payload.data
      })
    });
  }
}, _initialState);
