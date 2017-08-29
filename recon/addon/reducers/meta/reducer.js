import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'recon/actions/types';

const metaInitialState = Immutable.from({
  meta: null,
  metaError: null,
  metaLoading: false
});

const metaReducer = handleActions({

  [ACTION_TYPES.INITIALIZE]: (state, { payload: { meta } }) => {
    return metaInitialState.merge({ meta });
  },

  [ACTION_TYPES.META_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (/* s */) => metaInitialState.set('metaLoading', true),
      finish: (s) => s.set('metaLoading', false),
      failure: (s) => s.set('metaError', true),
      success: (s) => s.set('meta', action.payload)
    });
  }

}, metaInitialState);

export default metaReducer;
