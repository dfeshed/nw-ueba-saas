import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'endpoint-rar/actions/types';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  error: null,
  loading: false,
  downloadId: null,
  serverId: null
});

const rarReducer = handleActions({
  [ACTION_TYPES.GET_RAR_INSTALLER_ID]: (state, action) => {
    return handle(state, action, {
      start: () => state.merge({ loading: true, error: false }),
      finish: (s) => s.set('loading', false),
      failure: (s) => s.merge({ downloadId: null, error: true }),
      success: (s) => s.merge({ downloadId: action.payload.data.id, error: false })
    });
  },

  [ACTION_TYPES.SET_SERVER_ID]: (state, { payload }) => {
    return state.set('serverId', payload);
  }
}, initialState);

export default rarReducer;
