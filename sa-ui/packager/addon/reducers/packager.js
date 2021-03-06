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
  selectedServerIP: null,
  endpointServerList: []
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
          packageConfig: { ...action.payload.data }
        },
        initialState: {
          ...action.payload.data,
          packageConfig: { ...action.payload.data }
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

  [ACTION_TYPES.UPDATE_FIELDS]: (state, { payload }) => state.set('defaultPackagerConfig', payload),

  [ACTION_TYPES.DOWNLOAD_PACKAGE]: (state, { payload }) => {
    return state.set('downloadLink', payload);
  },

  [ACTION_TYPES.RETRIEVE_FAILURE]: (state) => state.merge({ error: true, loading: false }),

  [ACTION_TYPES.RESET_FORM]: (state) => state.set('defaultPackagerConfig', { ...state.initialState }),

  [ACTION_TYPES.SET_SELECTED_SERVER_IP]: (state, { payload }) => state.set('selectedServerIP', payload),

  [ACTION_TYPES.GET_ENDPOINT_SERVERS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const listOfServers = action.payload.data;
        listOfServers.forEach((item) => {
          item.hostIpClone = item.host;
        });
        return s.set('endpointServerList', listOfServers);
      }
    });
  }
}, initialState);

export default packagerReducer;
