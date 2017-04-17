import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'recon/actions/types';

const metaInitialState = {
  meta: null,
  metaError: null,
  metaLoading: false
};

const metaReducer = handleActions({

  [ACTION_TYPES.INITIALIZE]: (state, { payload }) => ({
    ...metaInitialState,
    meta: payload.meta
  }),

  [ACTION_TYPES.META_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (/* s */) => ({ ...metaInitialState, metaLoading: true }),
      finish: (s) => ({ ...s, metaLoading: false }),
      failure: (s) => ({ ...s, metaError: true }),
      success: (s) => ({ ...s, meta: action.payload })
    });
  }

}, metaInitialState);

export default metaReducer;
