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
  showNonEmptyProperty: false

});

const data = handleActions({

  [ACTION_TYPES.RESET_INPUT_DATA]: (state) => state.merge(dataInitialState),

  [ACTION_TYPES.INITIALIZE_DATA]: (state, { payload }) => state.merge({
    agentId: payload.agentId,
    scanTime: payload.scanTime
  }),

  [ACTION_TYPES.FETCH_ALL_SNAP_SHOTS]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('snapShots', action.payload.data)
    });
  },

  [ACTION_TYPES.SET_SCAN_TIME]: (state, { payload }) => state.set('scanTime', payload),

  [ACTION_TYPES.SET_ANIMATION]: (state, { payload }) => state.set('animation', payload),

  [ACTION_TYPES.TOGGLE_OVERVIEW_PANEL]: (state) => {
    return state.set('isOverviewPanelVisible', !state.isOverviewPanelVisible);
  },

  [ACTION_TYPES.TOGGLE_SHOW_PROPERTY_WITH_VALUES]: (state) => {
    return state.set('showNonEmptyProperty', !state.showNonEmptyProperty);
  }

}, dataInitialState);

export default data;
