import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'recon/actions/types';

const headerInitialState = {
  headerError: null,
  headerItems: null,
  headerLoading: null
};

const headerReducer = handleActions({
  [ACTION_TYPES.INITIALIZE]: (/* state */) => ({
    ...headerInitialState
  }),

  [ACTION_TYPES.SUMMARY_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (/* s */) => ({ ...headerInitialState, headerLoading: true }),
      finish: (s) => ({ ...s, headerLoading: false }),
      failure: (s) => ({ ...s, headerError: true }),
      success: (s) => ({ ...s, headerItems: action.payload.headerItems })
    });
  }

}, headerInitialState);

export default headerReducer;

