import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'packager/actions/types';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  defaultPackagerConfig: { packageConfig: {} },
  error: null,
  loading: true,
  downloadLink: null,
  updating: false,
  devices: {},
  initialState: {
    packageConfig: {}
  },
  selectedServerIP: null
});

const packagerReducer = handleActions({
  [ACTION_TYPES.GET_INFO]: (state, action) => {
    return handle(state, action, {
      start: () => state.merge({ loading: true, error: false }),
      finish: (s) => s.set('loading', false),
      failure: (s) => s.set('error', true),
      success: (s) => s.merge({
        defaultPackagerConfig: {
          ...action.payload.data,
          packageConfig: { ...action.payload.data.packageConfig, server: s.selectedServerIP }
        },
        initialState: {
          ...action.payload.data,
          packageConfig: { ...action.payload.data.packageConfig, server: s.selectedServerIP }
        }
      })
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

  [ACTION_TYPES.RESET_FORM]: (state) => state.set('defaultPackagerConfig', { ...state.initialState }),

  [ACTION_TYPES.SET_SELECTED_SERVER_IP]: (state, { payload }) => state.set('selectedServerIP', payload)
}, initialState);

export default packagerReducer;
