import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';
import Immutable from 'seamless-immutable';

const dataInitialState = Immutable.from({
  // Host inputs
  agentId: null,
  scanTime: null,
  animation: 'default',

  snapShots: null,
  isDetailRightPanelVisible: true,
  isSnapshotsLoading: false,
  activeHostDetailPropertyTab: 'FILE_DETAILS',
  downloadLink: null
});

const data = handleActions({

  [ACTION_TYPES.RESET_INPUT_DATA]: (state) => state.merge(dataInitialState),

  [ACTION_TYPES.INITIALIZE_DATA]: (state, { payload }) => state.merge({
    agentId: payload.agentId,
    scanTime: payload.scanTime
  }),

  [ACTION_TYPES.FETCH_ALL_SNAP_SHOTS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('isSnapshotsLoading', true),
      success: (s) => s.merge({ snapShots: action.payload.data, isSnapshotsLoading: false })
    });
  },

  [ACTION_TYPES.SET_SCAN_TIME]: (state, { payload }) => state.set('scanTime', payload),

  [ACTION_TYPES.SET_ANIMATION]: (state, { payload }) => state.set('animation', payload),

  [SHARED_ACTION_TYPES.SET_DOWNLOAD_FILE_LINK]: (state, { payload }) => state.set('downloadLink', payload),

  [ACTION_TYPES.TOGGLE_DETAIL_RIGHT_PANEL]: (state) => {
    return state.set('isDetailRightPanelVisible', !state.isDetailRightPanelVisible);
  },
  [ACTION_TYPES.SET_HOST_DETAIL_PROPERTY_TAB]: (state, { payload: { tabName } }) => state.set('activeHostDetailPropertyTab', tabName)

}, dataInitialState);

export default data;
