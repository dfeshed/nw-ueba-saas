// Incidents Action Types
export const FETCH_INCIDENTS_STARTED = 'RESPOND::FETCH_INCIDENTS_STARTED';
export const FETCH_INCIDENTS_STREAM_INITIALIZED = 'RESPOND::FETCH_INCIDENTS_STREAM_INITIALIZED';
export const FETCH_INCIDENTS_COMPLETED = 'RESPOND::FETCH_INCIDENTS_STREAM_COMPLETED';
export const FETCH_INCIDENTS_ERROR = 'RESPOND::FETCH_INCIDENTS_ERROR';
export const FETCH_INCIDENTS_RETRIEVE_BATCH = 'RESPOND::FETCH_INCIDENTS_RETRIEVE_BATCH';
export const FETCH_INCIDENTS_TOTAL_COUNT = 'RESPOND::FETCH_INCIDENTS_TOTAL_COUNT';
export const FETCH_INCIDENT_DETAILS = 'RESPOND::FETCH_INCIDENT_DETAILS';
export const FETCH_INCIDENT_STORYLINE_STARTED = 'RESPOND::FETCH_INCIDENT_STORYLINE_STARTED';
export const FETCH_INCIDENT_STORYLINE_STREAM_INITIALIZED = 'RESPOND::FETCH_INCIDENT_STORYLINE_STREAM_INITIALIZED';
export const FETCH_INCIDENT_STORYLINE_COMPLETED = 'RESPOND::FETCH_INCIDENT_STORYLINE_COMPLETED';
export const FETCH_INCIDENT_STORYLINE_ERROR = 'RESPOND::FETCH_INCIDENT_STORYLINE_ERROR';
export const FETCH_INCIDENT_STORYLINE_RETRIEVE_BATCH = 'RESPOND::FETCH_INCIDENT_STORYLINE_RETRIEVE_BATCH';
export const FETCH_INCIDENT_STORYLINE_EVENTS_STREAM_INITIALIZED = 'RESPOND::FETCH_INCIDENT_STORYLINE_EVENTS_STREAM_INITIALIZED';
export const FETCH_INCIDENT_STORYLINE_EVENTS_REQUEST_BATCH = 'RESPOND::FETCH_INCIDENT_STORYLINE_EVENTS_REQUEST_BATCH';
export const FETCH_INCIDENT_STORYLINE_EVENTS_RETRIEVE_BATCH = 'RESPOND::FETCH_INCIDENT_STORYLINE_EVENTS_RETRIEVE_BATCH';
export const FETCH_INCIDENT_STORYLINE_EVENTS_COMPLETED = 'RESPOND::FETCH_INCIDENT_STORYLINE_EVENTS_COMPLETED';
export const FETCH_INCIDENT_STORYLINE_EVENTS_ERROR = 'RESPOND::FETCH_INCIDENT_STORYLINE_EVENTS_ERROR';
export const EXPAND_STORYLINE_EVENT = 'RESPOND::EXPAND_STORYLINE_EVENT';
export const FETCH_INCIDENTS_SETTINGS = 'RESPOND::FETCH_INCIDENTS_SETTINGS';
export const UPDATE_INCIDENT_FILTERS = 'RESPOND::UPDATE_INCIDENT_FILTERS';
export const RESET_INCIDENT_FILTERS = 'RESPOND::RESET_INCIDENT_FILTERS';
export const TOGGLE_SELECT_ALL_INCIDENTS = 'RESPOND::TOGGLE_SELECT_ALL_INCIDENTS';
export const CLEAR_FOCUS_INCIDENTS = 'RESPOND::CLEAR_FOCUS_INCIDENTS';
export const UPDATE_INCIDENT = 'RESPOND::UPDATE_INCIDENT';
export const DELETE_INCIDENT = 'RESPOND::DELETE_INCIDENT';
export const SEND_INCIDENT_TO_ARCHER = 'RESPOND::SEND_INCIDENT_TO_ARCHER';
export const FETCH_REMEDIATION_TASKS_FOR_INCIDENT = 'RESPOND::FETCH_REMEDIATION_TASKS_FOR_INCIDENT';
export const SET_TASKS_JOURNAL_MODE = 'RESPOND::SET_TASKS_JOURNAL_MODE';
export const TOGGLE_TASKS_JOURNAL = 'RESPOND::TOGGLE_TASKS_JOURNAL';
export const CREATE_INCIDENT_SAGA = 'RESPOND::CREATE_INCIDENT::SAGA';
export const CREATE_INCIDENT = 'RESPOND::CREATE_INCIDENT';
export const START_TRANSACTION = 'RESPOND::START_TRANSACTION';
export const FINISH_TRANSACTION = 'RESPOND::FINISH_TRANSACTION';

export const FETCH_STATUS_TYPES = 'RESPOND::FETCH_STATUS_TYPES';
export const FETCH_PRIORITY_TYPES = 'RESPOND::FETCH_PRIORITY_TYPES';
export const FETCH_CATEGORY_TAGS = 'RESPOND::FETCH_CATEGORY_TAGS';

export const SORT_BY = 'RESPOND::SORT_BY';
export const INITIALIZE_INCIDENT = 'RESPOND::INITIALIZE_INCIDENT';

/* Remediation Tasks */
export const FETCH_REMEDIATION_TASKS = 'RESPOND::FETCH_REMEDIATION_TASKS';
export const UPDATE_REMEDIATION_TASK = 'RESPOND::UPDATE_REMEDIATION_TASK';
export const UPDATE_REMEDIATION_TASK_FILTERS = 'RESPOND::UPDATE_REMEDIATION_TASK_FILTERS';
export const FETCH_REMEDIATION_TASKS_TOTAL_COUNT = 'RESPOND::FETCH_REMEDIATION_TASKS_TOTAL_COUNT';
export const CREATE_REMEDIATION_TASK = 'RESPOND::CREATE_REMEDIATION_TASK';
export const TOGGLE_FILTER_PANEL_REMEDIATION_TASKS = 'RESPOND::TOGGLE_FILTER_PANEL_REMEDIATION_TASKS';
export const RESET_REMEDIATION_TASK_FILTERS = 'RESPOND::RESET_REMEDIATION_TASK_FILTERS';
export const TOGGLE_REMEDIATION_TASKS_CUSTOM_DATE_RESTRICTION = 'RESPOND::TOGGLE_REMEDIATION_TASKS_CUSTOM_DATE_RESTRICTION';
export const TOGGLE_REMEDIATION_TASK_SELECTED = 'RESPOND::TOGGLE_REMEDIATION_TASK_SELECTED';
export const TOGGLE_FOCUS_REMEDIATION_TASK = 'RESPOND::TOGGLE_FOCUS_REMEDIATION_TASK';
export const CLEAR_FOCUS_REMEDIATION_TASK = 'RESPOND::CLEAR_FOCUS_REMEDIATION_TASK';
export const TOGGLE_SELECT_ALL_REMEDIATION_TASKS = 'RESPOND::TOGGLE_SELECT_ALL_REMEDIATION_TASK';
export const REMEDIATION_TASK_SORT_BY = 'RESPOND::REMEDIATION_TASK_SORT_BY';
export const FETCH_REMEDIATION_STATUS_TYPES = 'RESPOND::FETCH_REMEDIATION_STATUS_TYPES';
export const FETCH_REMEDIATION_TYPES = 'RESPOND::FETCH_REMEDIATION_TYPES';
export const DELETE_REMEDIATION_TASK = 'RESPOND::DELETE_REMEDIATION_TASK';

/* Alerts */
export const FETCH_ALERTS_STARTED = 'RESPOND::FETCH_ALERTS_STARTED';
export const FETCH_ALERTS_STREAM_INITIALIZED = 'RESPOND::FETCH_ALERTS_STREAM_INITIALIZED';
export const FETCH_ALERTS_COMPLETED = 'RESPOND::FETCH_ALERTS_COMPLETED';
export const FETCH_ALERTS_ERROR = 'RESPOND::FETCH_ALERTS_ERROR';
export const FETCH_ALERTS_RETRIEVE_BATCH = 'RESPOND::FETCH_ALERTS_RETRIEVE_BATCH';
export const FETCH_ALERTS_TOTAL_COUNT = 'RESPOND::FETCH_ALERTS_TOTAL_COUNT';
export const UPDATE_ALERT = 'RESPOND::UPDATE_ALERT';
export const DELETE_ALERT = 'RESPOND::DELETE_ALERT';
export const UPDATE_ALERT_FILTERS = 'RESPOND::UPDATE_ALERT_FILTERS';
export const TOGGLE_FILTER_PANEL_ALERTS = 'RESPOND::TOGGLE_FILTER_PANEL_ALERTS';
export const RESET_ALERT_FILTERS = 'RESPOND::RESET_ALERT_FILTERS';
export const TOGGLE_ALERTS_CUSTOM_DATE_RESTRICTION = 'RESPOND::TOGGLE_ALERT_CUSTOM_DATE_RESTRICTION';
export const TOGGLE_ALERT_SELECTED = 'RESPOND::TOGGLE_ALERT_SELECTED';
export const TOGGLE_FOCUS_ALERT = 'RESPOND::TOGGLE_FOCUS_ALERT';
export const CLEAR_FOCUS_ALERT = 'RESPOND::CLEAR_FOCUS_ALERT';
export const TOGGLE_SELECT_ALL_ALERTS = 'RESPOND::TOGGLE_SELECT_ALL_ALERTS';
export const ALERT_SORT_BY = 'RESPOND::ALERT_SORT_BY';
export const FETCH_ALERT_SOURCES = 'RESPOND::FETCH_ALERT_SOURCES';
export const FETCH_ALERT_TYPES = 'RESPOND::FETCH_ALERT_TYPES';
export const FETCH_ORIGINAL_ALERT = 'RESPOND::FETCH_ORIGINAL_ALERT';
export const FETCH_ALERT_NAMES = 'RESPOND::FETCH_ALERT_NAMES';

export const ALERTS_UPDATE_SEARCH_INCIDENTS_TEXT = 'RESPOND::ALERTS_UPDATE_SEARCH_INCIDENTS_TEXT';
export const ALERTS_UPDATE_SEARCH_INCIDENTS_SORTBY = 'RESPOND::ALERTS_UPDATE_SEARCH_INCIDENTS_SORTBY';
export const ALERTS_SEARCH_INCIDENTS_SELECT = 'RESPOND::ALERTS_SEARCH_INCIDENTS_SELECT';
export const ALERTS_SEARCH_INCIDENTS_STARTED = 'RESPOND::ALERTS_SEARCH_INCIDENTS_STARTED';
export const ALERTS_SEARCH_INCIDENTS_STREAM_INITIALIZED = 'RESPOND::ALERTS_SEARCH_INCIDENTS_STREAM_INITIALIZED';
export const ALERTS_SEARCH_INCIDENTS_COMPLETED = 'RESPOND::ALERTS_SEARCH_INCIDENTS_COMPLETED';
export const ALERTS_SEARCH_INCIDENTS_RETRIEVE_BATCH = 'RESPOND::ALERTS_SEARCH_INCIDENTS_RETRIEVE_BATCH';
export const ALERTS_SEARCH_INCIDENTS_ERROR = 'RESPOND::ALERTS_SEARCH_INCIDENTS_ERROR';
export const ALERTS_ADD_TO_INCIDENT = 'RESPOND::ALERTS_ADD_TO_INCIDENT';

export const CLEAR_SEARCH_INCIDENTS_RESULTS_FOR_ALERTS = 'RESPOND::CLEAR_SEARCH_INCIDENTS_RESULTS_FOR_ALERTS';

/* Alert Details */
export const INITIALIZE_ALERT = 'RESPOND::INITIALIZE_ALERT';
export const RESIZE_ALERT_INSPECTOR = 'RESPOND::RESIZE_ALERT_INSPECTOR';
export const FETCH_ALERT_DETAILS_STARTED = 'RESPOND::FETCH_ALERT_DETAILS_STARTED';
export const FETCH_ALERT_DETAILS_STREAM_INITIALIZED = 'RESPOND::FETCH_ALERT_DETAILS_STREAM_INITIALIZED';
export const FETCH_ALERT_DETAILS_RETRIEVE_BATCH = 'RESPOND::FETCH_ALERT_DETAILS_RETRIEVE_BATCH';
export const FETCH_ALERT_DETAILS_COMPLETED = 'RESPOND::FETCH_ALERT_DETAILS_COMPLETED';
export const FETCH_ALERT_DETAILS_ERROR = 'RESPOND::FETCH_ALERT_DETAILS_ERROR';
export const FETCH_ALERT_EVENTS = 'RESPOND::FETCH_ALERT_EVENTS';

/* Users */
export const FETCH_ALL_ENABLED_USERS = 'RESPOND::FETCH_ALL_ENABLED_USERS';
export const FETCH_ALL_USERS = 'RESPOND::FETCH_ALL_USERS';

/* Journal */
export const FETCH_MILESTONE_TYPES = 'RESPOND::FETCH_MILESTONE_TYPES';
export const CREATE_JOURNAL_ENTRY = 'RESPOND::CREATE_JOURNAL_ENTRY';
export const DELETE_JOURNAL_ENTRY = 'RESPOND::DELETE_JOURNAL_ENTRY';
export const UPDATE_JOURNAL_ENTRY = 'RESPOND::UPDATE_JOURNAL_ENTRY';

/* Search for Related Indicators */
export const SET_DEFAULT_SEARCH_TIME_FRAME_NAME = 'RESPOND::SET_DEFAULT_SEARCH_TIME_FRAME_NAME';
export const SET_DEFAULT_SEARCH_ENTITY_TYPE = 'RESPOND::SET_DEFAULT_SEARCH_ENTITY_TYPE';
export const SEARCH_RELATED_INDICATORS_STARTED = 'RESPOND::SEARCH_RELATED_INDICATORS_STARTED';
export const SEARCH_RELATED_INDICATORS_STREAM_INITIALIZED = 'RESPOND::SEARCH_RELATED_INDICATORS_STREAM_INITIALIZED';
export const SEARCH_RELATED_INDICATORS_COMPLETED = 'RESPOND::SEARCH_RELATED_INDICATORS_COMPLETED';
export const SEARCH_RELATED_INDICATORS_ERROR = 'RESPOND::SEARCH_RELATED_INDICATORS_ERROR';
export const SEARCH_RELATED_INDICATORS_STOPPED = 'RESPOND::SEARCH_RELATED_INDICATORS_STOPPED';
export const SEARCH_RELATED_INDICATORS_RETRIEVE_BATCH = 'RESPOND::SEARCH_RELATED_INDICATORS_RETRIEVE_BATCH';
export const ADD_RELATED_INDICATORS = 'RESPOND::ADD_RELATED_INDICATORS';
export const CLEAR_ADD_RELATED_INDICATORS_STATUS = 'RESPOND:CLEAR_ADD_RELATED_INDICATORS_STATUS';

// UI State Action Types
export const TOGGLE_FILTER_PANEL = 'RESPOND::TOGGLE_FILTER_PANEL';
export const TOGGLE_INCIDENT_SELECTED = 'RESPOND::TOGGLE_INCIDENT_SELECTED';
export const TOGGLE_CUSTOM_DATE_RESTRICTION = 'RESPOND::TOGGLE_CUSTOM_DATE_RESTRICTION';
export const TOGGLE_ENTITY_VISIBILITY = 'RESPOND::TOGGLE_ENTITY_VISIBILITY';

export const SET_HIDE_VIZ = 'RESPOND::SET_HIDE_VIZ';
export const SET_VIEW_MODE = 'RESPOND::SET_VIEW_MODE';
export const RESIZE_INCIDENT_INSPECTOR = 'RESPOND::RESIZE_INCIDENT_INSPECTOR';

export const CLEAR_INCIDENT_SELECTION = 'RESPOND::CLEAR_INCIDENT_SELECTION';
export const SET_INCIDENT_SELECTION = 'RESPOND::SET_INCIDENT_SELECTION';
export const TOGGLE_INCIDENT_SELECTION = 'RESPOND::TOGGLE_INCIDENT_SELECTION';
export const TOGGLE_FOCUS_INCIDENT = 'RESPOND::TOGGLE_FOCUS_INCIDENT';

export const SERVICES_RETRIEVE = 'RESPOND::SERVICES_RETRIEVE';
export const ALIASES_AND_LANGUAGE_RETRIEVE_SAGA = 'RESPOND::ALIASES_AND_LANGUAGE::SAGA';
export const ALIASES_AND_LANGUAGE_RETRIEVE = 'RESPOND::ALIASES_AND_LANGUAGE_RETRIEVE';
export const ALIASES_AND_LANGUAGE_COMPLETE = 'RESPOND::ALIASES_AND_LANGUAGE_COMPLETE';
export const GET_FROM_LANGUAGE_AND_ALIASES_CACHE = 'RESPOND::GET_FROM_LANGUAGE_AND_ALIASES_CACHE';

// RIAC SETTINGS
export const GET_RIAC_SETTINGS = 'RESPOND::GET_RIAC_SETTINGS';
