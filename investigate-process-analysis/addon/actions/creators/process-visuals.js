import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';

export const setDetailsTab = (input) => ({
  type: ACTION_TYPES.SET_DETAILS_TAB,
  payload: input
});

export const toggleEventPanelExpanded = () => ({ type: ACTION_TYPES.TOGGLE_EVENT_PANEL_EXPANDED });

export const toggleProcessDetailsVisibility = (payload) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.TOGGLE_PROCESS_DETAILS_VISIBILITY, payload });
    if (!payload) {
      dispatch(setDetailsTab({}));
    }
  };
};


