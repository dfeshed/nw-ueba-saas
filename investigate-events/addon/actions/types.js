export const REHYDRATE = 'persist/REHYDRATE';

export const INITIALIZE_INVESTIGATE = 'INVESTIGATE_EVENTS::INITIALIZE_INVESTIGATE';
export const SET_PREFERENCES = 'INVESTIGATE_EVENTS::SET_PREFERENCES';
export const COLUMNS_RETRIEVE = 'INVESTIGATE_EVENTS::COLUMNS_RETRIEVE';

// Services action types
export const SERVICE_SELECTED = 'INVESTIGATE_EVENTS::SERVICE_SELECTED';
export const SERVICES_RETRIEVE = 'INVESTIGATE_EVENTS::SERVICES_RETRIEVE';

// Session action typess
export const SESSION_SELECTED = 'INVESTIGATE_EVENTS::SESSION_SELECTED';

// Dictionary action types
export const ALIASES_RETRIEVE = 'INVESTIGATE_EVENTS::ALIASES_RETRIEVE';
export const ALIASES_GET_FROM_CACHE = 'INVESTIGATE_EVENTS::ALIASES_GET_FROM_CACHE';
export const LANGUAGE_RETRIEVE = 'INVESTIGATE_EVENTS::LANGUAGE_RETRIEVE';
export const LANGUAGE_GET_FROM_CACHE = 'INVESTIGATE_EVENTS::LANGUAGE_GET_FROM_CACHE';
// Dictionary helper types for testing
export const SET_ALIASES = 'INVESTIGATE_EVENTS::SET_ALIASES';
export const SET_LANGUAGE = 'INVESTIGATE_EVENTS::SET_LANGUAGE';

// Summary action types
export const SUMMARY_RETRIEVE = 'INVESTIGATE_EVENTS::SUMMARY_RETRIEVE';

// URL query param action types
export const SET_META_PANEL_SIZE = 'INVESTIGATE_EVENTS::SET_META_PANEL_SIZE';
export const SET_RECON_PANEL_SIZE = 'INVESTIGATE_EVENTS::SET_RECON_PANEL_SIZE';

// Query action types
export const RESET_QUERYNODE = 'INVESTIGATE_EVENTS::RESET_QUERYNODE';
export const SET_QUERY_TIME_RANGE = 'INVESTIGATE_EVENTS::SET_QUERY_TIME_RANGE';
export const MARK_QUERY_DIRTY = 'INVESTIGATE_EVENTS::MARK_QUERY_DIRTY';
export const SET_QUERY_VIEW = 'INVESTIGATE_EVENTS::SET_QUERY_VIEW';
export const SET_FREE_FORM_TEXT = 'INVESTIGATE_EVENTS::SET_FREE_FORM_TEXT';

// View state action types
export const SET_RECON_VIEWABLE = 'INVESTIGATE_EVENTS::SET_RECON_VIEWABLE';

// Event action types
export const INIT_EVENTS_STREAMING = 'INVESTIGATE_EVENTS::INIT_EVENTS_STREAMING';
export const GET_EVENT_COUNT = 'INVESTIGATE_EVENTS::GET_EVENT_COUNT';
export const GET_EVENT_TIMELINE = 'INVESTIGATE_EVENTS::GET_EVENT_TIMELINE';
export const SET_EVENTS_PAGE = 'INVESTIGATE_EVENTS::SET_EVENTS_PAGE';
export const SET_EVENTS_PAGE_ERROR = 'INVESTIGATE_EVENTS::SET_EVENTS_PAGE_ERROR';
export const SET_EVENTS_PAGE_STATUS = 'INVESTIGATE_EVENTS::SET_EVENTS_PAGE_STATUS';
export const SET_ANCHOR = 'INVESTIGATE_EVENTS::SET_ANCHOR';
export const SET_GOAL = 'INVESTIGATE_EVENTS::SET_GOAL';
export const GET_LOG = 'INVESTIGATE::GET_LOG';
export const SET_LOG = 'INVESTIGATE::SET_LOG';
export const SET_LOG_STATUS = 'INVESTIGATE::SET_LOG_STATUS';
export const SET_SELECTED_COLUMN_GROUP = 'INVESTIGATE::SET_SELECTED_COLUMN_GROUP';

export const INITIALIZE_TESTS = 'INVESTIGATE_EVENTS::INITIALIZE_TESTS';
export const SET_QUERY_PARAMS_FOR_TESTS = 'INVESTIGATE_EVENTS::SET_QUERY_PARAMS_FOR_TESTS';
