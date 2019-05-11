import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';

const setActiveFilterTab = (tabName) => ({ type: ACTION_TYPES.SET_ACTIVE_EVENT_FILTER_TAB, payload: { tabName } });

export {
  setActiveFilterTab
};