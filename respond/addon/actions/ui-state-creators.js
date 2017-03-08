import * as ACTION_TYPES from './types';

const toggleFilterPanel = () => ({ type: ACTION_TYPES.TOGGLE_FILTER_PANEL });
const toggleIsInSelectMode = () => ({ type: ACTION_TYPES.TOGGLE_SELECT_MODE });
const toggleTheme = () => ({ type: ACTION_TYPES.TOGGLE_THEME });
const toggleIncidentSelected = (incident) => ({ type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTED, payload: incident });
const toggleJournalPanel = () => ({ type: ACTION_TYPES.TOGGLE_JOURNAL_PANEL });
const setViewMode = (viewMode) => ({ type: ACTION_TYPES.SET_VIEW_MODE, payload: viewMode });

export {
  toggleFilterPanel,
  toggleIsInSelectMode,
  toggleTheme,
  toggleIncidentSelected,
  toggleJournalPanel,
  setViewMode
};