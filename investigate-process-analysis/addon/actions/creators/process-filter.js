import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import { selectedProcessEvents } from 'investigate-process-analysis/actions/creators/events-creators';

export const updateFilterValue = (input) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.UPDATE_FILTER_ITEMS, payload: input });
    dispatch(selectedProcessEvents(input.selectedProcess.processId));
  };
};

export const resetFilterValue = (selectedProcess) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.RESET_FILTER_ITEMS });
    dispatch(selectedProcessEvents(selectedProcess));
  };
};
