import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'endpoint-rar/actions/types';
import Immutable from 'seamless-immutable';

const _processRARConfig = ({ data }) => {
  const servers = data.servers ? data.servers[0] : [{}];
  const rarConfig = { esh: '', address: '', ...data, ...servers };
  const { httpsBeaconIntervalInSeconds = 60 } = rarConfig;

  rarConfig.httpsBeaconIntervalInSeconds = httpsBeaconIntervalInSeconds / 60;
  return rarConfig;
};

const initialState = Immutable.from({
  defaultRARConfig: { rarConfig: {} },
  initialRARConfig: { rarConfig: {} },
  loading: false,
  downloadId: null,
  serverId: null
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

  [ACTION_TYPES.UPDATE_UI_STATE]: (state, { payload }) => state.set('defaultRARConfig', payload),

  [ACTION_TYPES.RESET_RAR_CONFIG]: (state) => state.set('defaultRARConfig', { ...state.initialRARConfig })
}, initialState);

export default rarReducer;
