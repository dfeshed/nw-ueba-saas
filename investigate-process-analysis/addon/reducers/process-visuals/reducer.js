import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import Immutable from 'seamless-immutable';

const dataInitialState = Immutable.from({
  detailsTabSelected: 'Events', // Possible values Properties or Events at this point.
  isEventPanelExpanded: true,
  isProcessDetailsVisible: true
});

const processVisualsReducer = handleActions({

  [ACTION_TYPES.SET_DETAILS_TAB]: (state, action) => state.merge({ detailsTabSelected: action.payload }),

  [ACTION_TYPES.TOGGLE_EVENT_PANEL_EXPANDED]: (state) => state.set('isEventPanelExpanded', !state.isEventPanelExpanded),

  [ACTION_TYPES.TOGGLE_PROCESS_DETAILS_VISIBILITY]: (state) => state.set('isProcessDetailsVisible', !state.isProcessDetailsVisible)

}, dataInitialState);

export default processVisualsReducer;
