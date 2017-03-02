import * as ACTION_TYPES from './types';

const toggleFilterPanel = () => ({ type: ACTION_TYPES.TOGGLE_FILTER_PANEL });
const toggleIsInSelectMode = () => ({ type: ACTION_TYPES.TOGGLE_SELECT_MODE });
const toggleTheme = () => ({ type: ACTION_TYPES.TOGGLE_THEME });
const toggleIncidentSelected = (incident) => ({ type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTED, payload: incident });
const toggleEntitiesPanel = () => ({ type: ACTION_TYPES.TOGGLE_ENTITIES_PANEL });
const toggleEventsPanel = () => ({ type: ACTION_TYPES.TOGGLE_EVENTS_PANEL });
const toggleJournalPanel = () => ({ type: ACTION_TYPES.TOGGLE_JOURNAL_PANEL });

export {
  toggleFilterPanel,
  toggleIsInSelectMode,
  toggleTheme,
  toggleIncidentSelected,
  toggleEntitiesPanel,
  toggleEventsPanel,
  toggleJournalPanel
};