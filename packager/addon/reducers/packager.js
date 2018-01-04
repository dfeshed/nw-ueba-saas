import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'packager/actions/types';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  defaultPackagerConfig: {},
  error: null,
  loading: false,
  downloadLink: null,
  updating: false,
  devices: {},
  initialState: {}
});

const packagerReducer = handleActions({
  [ACTION_TYPES.GET_INFO]: (state, action) => {
    return handle(state, action, {
      start: () => state.merge({ loading: true, error: false }),
      finish: (s) => s.set('loading', false),
      failure: (s) => s.set('error', true),
      success: (s) => s.set('defaultPackagerConfig', action.payload.data).set('initialState', action.payload.data)
    });
  },

  [ACTION_TYPES.SAVE_INFO]: (state, action) => {
    return handle(state, action, {
      start: () => state.merge({ updating: true, error: false, downloadLink: null }),
      finish: (s) => s.set('updating', false),
      failure: (s) => s
    });
  },

  [ACTION_TYPES.GET_DEVICES]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('devices', action.payload.data),
      failure: (s) => s.set('error', true)
    });
  },

  [ACTION_TYPES.UPDATE_FIELDS]: (state, { payload }) => state.set('defaultPackagerConfig', payload),

  [ACTION_TYPES.DOWNLOAD_PACKAGE]: (state, { payload }) => {
    return state.set('downloadLink', payload);
  },

  [ACTION_TYPES.RETRIEVE_FAILURE]: (state) => state.merge({ error: true, loading: false }),

  [ACTION_TYPES.RESET_FORM]: (state) => state.set('defaultPackagerConfig', { ...state.initialState })

}, initialState);

export default packagerReducer;
