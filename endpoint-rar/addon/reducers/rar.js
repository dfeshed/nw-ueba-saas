import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'endpoint-rar/actions/types';
import Immutable from 'seamless-immutable';

const _processRARConfig = ({ data }) => {
  const servers = data.servers ? data.servers[0] : [{}];
  const enabled = !!data.enabled;
  const rarConfig = { esh: '', address: '', enabled, ...data, ...servers };
  const { httpsBeaconIntervalInSeconds = 900, httpsPort = 443 } = rarConfig;
  rarConfig.httpsPort = httpsPort;
  rarConfig.httpsBeaconIntervalInSeconds = httpsBeaconIntervalInSeconds / 60;
  return rarConfig;
};

const initialState = Immutable.from({
  defaultRARConfig: { rarConfig: {} },
  initialRARConfig: { rarConfig: {} },
  loading: false,
  testConfigLoader: false,
  downloadId: null,
  serverId: null,
  isEnabled: false
});

const rarReducer = handleActions({
  [ACTION_TYPES.GET_RAR_INSTALLER_ID]: (state, action) => {
    return handle(state, action, {
      start: () => state.merge({ loading: true }),
      finish: (s) => s.set('loading', false),
      failure: (s) => s.merge({ downloadId: null }),
      success: (s) => s.merge({ downloadId: action.payload.data.id })
    });
  },

  [ACTION_TYPES.SET_SERVER_ID]: (state, { payload }) => {
    return state.set('serverId', payload);
  },

  [ACTION_TYPES.GET_AND_SAVE_RAR_CONFIG]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const rarConfig = _processRARConfig(action.payload);

        return s.merge({
          defaultRARConfig: {
            rarConfig: { ...rarConfig }
          },
          initialRARConfig: {
            rarConfig: { ...rarConfig }
          }
        });
      }
    });
  },

  [ACTION_TYPES.GET_AND_SAVE_ENABLE_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        return s.set('isEnabled', action.payload.data.enabled);
      }
    });
  },


  [ACTION_TYPES.UPDATE_UI_STATE]: (state, { payload }) => state.set('defaultRARConfig', payload),

  [ACTION_TYPES.RESET_RAR_CONFIG]: (state) => state.set('defaultRARConfig', { ...state.initialRARConfig }),

  [ACTION_TYPES.TEST_RAR_CONFIG]: (state, action) => {
    return handle(state, action, {
      start: () => state.set('testConfigLoader', true),
      finish: () => state.set('testConfigLoader', false)
    });
  }
}, initialState);

export default rarReducer;
