import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import Immutable from 'seamless-immutable';

const dataInitialState = Immutable.from({
  // Host inputs
  agentId: null,
  scanTime: null,
  animation: 'default',

  snapShots: null,
  isOverviewPanelVisible: true,
  isDetailRightPanelVisible: true,
  isSnapshotsLoading: false
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

  [ACTION_TYPES.TOGGLE_OVERVIEW_PANEL]: (state) => {
    return state.set('isOverviewPanelVisible', !state.isOverviewPanelVisible);
  },

  [ACTION_TYPES.TOGGLE_DETAIL_RIGHT_PANEL]: (state) => {
    return state.set('isDetailRightPanelVisible', !state.isDetailRightPanelVisible);
  }

}, dataInitialState);

export default data;
