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
      start: () => metaInitialState.set('metaLoading', true),
      failure: (s) => s.set('metaLoading', false).set('metaError', action.payload),
      success: (s) => s.set('metaLoading', false).set('meta', action.payload)
    });
  }

}, metaInitialState);

export default metaReducer;
