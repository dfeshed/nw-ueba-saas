import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import Immutable from 'seamless-immutable';

const dataInitialState = Immutable.from({
  detailsTabSelected: '', // Possible values Properties or Events at this point.
  isEventPanelExpanded: false,
  isProcessDetailsVisible: false,
  isProcessInfoVisible: false
});

const processVisualsReducer = handleActions({

  [ACTION_TYPES.SET_DETAILS_TAB]: (state, action) => state.merge({ detailsTabSelected: action.payload }),

  [ACTION_TYPES.TOGGLE_EVENT_PANEL_EXPANDED]: (state) => state.set('isEventPanelExpanded', !state.isEventPanelExpanded),

  [ACTION_TYPES.TOGGLE_PROCESS_DETAILS_VISIBILITY]: (state, action) => {
    const visibility = action.payload !== 'undefined' ? action.payload : !state.isProcessDetailsVisible;
    return state.set('isProcessDetailsVisible', visibility);
  },

  [ACTION_TYPES.TOGGLE_PROCESS_INFO_VISIBILITY]: (state, action) => {
    const visibility = action.payload !== 'undefined' ? action.payload : !state.isProcessDetailsVisible;
    return state.set('isProcessInfoVisible', visibility);
  }
}, dataInitialState);

export default processVisualsReducer;
