import * as ACTION_TYPES from './types';

const toggleFilterPanel = () => ({ type: ACTION_TYPES.TOGGLE_FILTER_PANEL });
const toggleIsInSelectMode = () => ({ type: ACTION_TYPES.TOGGLE_SELECT_MODE });
const toggleTheme = () => ({ type: ACTION_TYPES.TOGGLE_THEME });
const toggleIncidentSelected = (incident) => ({ type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTED, payload: incident });
const toggleJournalPanel = () => ({ type: ACTION_TYPES.TOGGLE_JOURNAL_PANEL });
const setViewMode = (viewMode) => ({ type: ACTION_TYPES.SET_VIEW_MODE, payload: viewMode });
const singleSelectStoryPoint = (id) => ({ type: ACTION_TYPES.SET_INCIDENT_SELECTION, payload: { type: 'storyPoint', id } });
const toggleSelectStoryPoint = (id) => ({ type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTION, payload: { type: 'storyPoint', id } });
const singleSelectEvent = (id) => ({ type: ACTION_TYPES.SET_INCIDENT_SELECTION, payload: { type: 'event', id } });
const toggleSelectEvent = (id) => ({ type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTION, payload: { type: 'event', id } });
const singleSelectNode = (id) => ({ type: ACTION_TYPES.SET_INCIDENT_SELECTION, payload: { type: 'node', id } });
const toggleSelectNode = (id) => ({ type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTION, payload: { type: 'node', id } });
const singleSelectLink = (id) => ({ type: ACTION_TYPES.SET_INCIDENT_SELECTION, payload: { type: 'link', id } });
const toggleSelectLink = (id) => ({ type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTION, payload: { type: 'link', id } });

export {
  toggleFilterPanel,
  toggleIsInSelectMode,
  toggleTheme,
  toggleIncidentSelected,
  toggleJournalPanel,
  setViewMode,
  singleSelectStoryPoint,
  toggleSelectStoryPoint,
  singleSelectEvent,
  toggleSelectEvent,
  singleSelectLink,
  toggleSelectLink,
  singleSelectNode,
  toggleSelectNode
};