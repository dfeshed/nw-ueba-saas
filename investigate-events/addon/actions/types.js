export const SORT_IN_CLIENT_COMPLETE = 'INVESTIGATE_EVENTS::SORT_IN_CLIENT_COMPLETE';
export const SORT_IN_CLIENT_BEGIN = 'INVESTIGATE_EVENTS::SORT_IN_CLIENT_BEGIN';
export const UPDATE_SORT = 'INVESTIGATE_EVENTS::UPDATE_SORT';
export const UPDATE_GLOBAL_PREFERENCES = 'INVESTIGATE_EVENTS::UPDATE_GLOBAL_PREFERENCES';
export const SET_VISIBLE_COLUMNS = 'INVESTIGATE_EVENTS::SET_VISIBLE_COLUMNS';

export const SET_SEARCH_SCROLL = 'INVESTIGATE_EVENTS::SET_SEARCH_SCROLL';
export const SET_SEARCH_TERM = 'INVESTIGATE_EVENTS::SET_SEARCH_TERM';
export const TOGGLE_QUERY_CONSOLE = 'INVESTIGATE_EVENTS::TOGGLE_QUERY_CONSOLE';
export const QUERY_STATS = 'INVESTIGATE_EVENTS::QUERY_STATS';

export const REHYDRATE = 'persist/REHYDRATE';

export const INITIALIZE_INVESTIGATE = 'INVESTIGATE_EVENTS::INITIALIZE_INVESTIGATE';
export const SET_PREFERENCES = 'INVESTIGATE_EVENTS::SET_PREFERENCES';
export const COLUMNS_RETRIEVE = 'INVESTIGATE_EVENTS::COLUMNS_RETRIEVE';
export const COLUMNS_CREATE = 'INVESTIGATE_EVENTS::COLUMNS_CREATE';
export const COLUMNS_DELETE = 'INVESTIGATE_EVENTS::COLUMNS_DELETE';
export const COLUMNS_UPDATE = 'INVESTIGATE_EVENTS::COLUMNS_UPDATE';

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
export const SUMMARY_UPDATE = 'INVESTIGATE_EVENTS::SUMMARY_UPDATE';

// URL query param action types
export const SET_META_PANEL_SIZE = 'INVESTIGATE_EVENTS::SET_META_PANEL_SIZE';
export const SET_RECON_PANEL_SIZE = 'INVESTIGATE_EVENTS::SET_RECON_PANEL_SIZE';

// Query action types
export const RESET_QUERYNODE = 'INVESTIGATE_EVENTS::RESET_QUERYNODE';
export const SET_QUERY_TIME_RANGE = 'INVESTIGATE_EVENTS::SET_QUERY_TIME_RANGE';
export const SET_TIME_RANGE_ERROR = 'INVESTIGATE_EVENTS::SET_TIME_RANGE_ERROR';
export const SET_QUERY_VIEW = 'INVESTIGATE_EVENTS::SET_QUERY_VIEW';
export const SET_FREE_FORM_TEXT = 'INVESTIGATE_EVENTS::SET_FREE_FORM_TEXT';
export const QUERY_IS_RUNNING = 'INVESTIGATE_EVENTS::QUERY_IS_RUNNING';
export const SET_QUERY_EXECUTED_BY_COLUMN_GROUP_FLAG = 'INVESTIGATE_EVENTS::SET_QUERY_EXECUTED_BY_COLUMN_GROUP_FLAG';

// View state action types
export const SET_RECON_VIEWABLE = 'INVESTIGATE_EVENTS::SET_RECON_VIEWABLE';

// Event action types
export const SELECT_EVENTS = 'INVESTIGATE_EVENTS::SELECT_EVENTS';
export const DESELECT_EVENT = 'INVESTIGATE_EVENTS::DESELECT_EVENT';

export const INIT_EVENTS_STREAMING = 'INVESTIGATE_EVENTS::INIT_EVENTS_STREAMING';
export const EVENT_COUNT_RESULTS = 'INVESTIGATE_EVENTS::EVENT_COUNT_RESULTS';
export const START_GET_EVENT_COUNT = 'INVESTIGATE_EVENTS::START_GET_EVENT_COUNT';
export const FAILED_GET_EVENT_COUNT = 'INVESTIGATE_EVENTS::FAILED_GET_EVENT_COUNT';
export const GET_EVENT_TIMELINE = 'INVESTIGATE_EVENTS::GET_EVENT_TIMELINE';
export const SET_EVENTS_PAGE = 'INVESTIGATE_EVENTS::SET_EVENTS_PAGE';
export const SET_EVENTS_PAGE_ERROR = 'INVESTIGATE_EVENTS::SET_EVENTS_PAGE_ERROR';
export const SET_EVENTS_PAGE_STATUS = 'INVESTIGATE_EVENTS::SET_EVENTS_PAGE_STATUS';
export const GET_LOG = 'INVESTIGATE::GET_LOG';
export const SET_LOG = 'INVESTIGATE::SET_LOG';
export const SET_LOG_STATUS = 'INVESTIGATE::SET_LOG_STATUS';
export const SET_SELECTED_COLUMN_GROUP = 'INVESTIGATE::SET_SELECTED_COLUMN_GROUP';
export const SET_MAX_EVENT_LIMIT = 'INVESTIGATE::SET_MAX_EVENT_LIMIT';

// Next Gen Action types
export const INITIALIZE_QUERYING = 'INVESTIGATE_EVENTS::INITIALIZE_QUERYING';
export const ADD_PILL = 'INVESTIGATE_EVENTS::ADD_PILL';
export const ADD_PILL_FOCUS = 'INVESTIGATE_EVENTS::ADD_PILL_FOCUS';
export const BATCH_ADD_PILLS = 'INVESTIGATE_EVENTS::BATCH_ADD_PILLS';
export const DELETE_GUIDED_PILLS = 'INVESTIGATE_EVENTS::DELETE_GUIDED_PILLS';
export const DESELECT_GUIDED_PILLS = 'INVESTIGATE_EVENTS::DESELECT_GUIDED_PILLS';
export const EDIT_GUIDED_PILL = 'INVESTIGATE_EVENTS::EDIT_GUIDED_PILL';
export const OPEN_GUIDED_PILL_FOR_EDIT = 'INVESTIGATE_EVENTS::OPEN_GUIDED_PILL_FOR_EDIT';
export const REMOVE_FOCUS_GUIDED_PILL = 'INVESTIGATE_EVENTS::REMOVE_FOCUS_GUIDED_PILL';
export const REPLACE_ALL_GUIDED_PILLS = 'INVESTIGATE_EVENTS::REPLACE_ALL_GUIDED_PILLS';
export const RESET_GUIDED_PILL = 'INVESTIGATE_EVENTS::RESET_GUIDED_PILL';
export const SELECT_GUIDED_PILLS = 'INVESTIGATE_EVENTS::SELECT_GUIDED_PILLS';
export const UPDATE_FREE_FORM_TEXT = 'INVESTIGATE_EVENTS::UPDATE_FREE_FORM_TEXT';
export const VALIDATE_GUIDED_PILL = 'INVESTIGATE_EVENTS::VALIDATE_GUIDED_PILL';
export const INSERT_PARENS = 'INVESTIGATE_EVENTS::INSERT_PARENS';

export const RETRIEVE_QUERY_PARAMS_FOR_HASHES = 'INVESTIGATE_EVENTS::RETRIEVE_QUERY_PARAMS_FOR_HASHES';
export const RETRIEVE_HASH_FOR_QUERY_PARAMS = 'INVESTIGATE_EVENTS::RETRIEVE_HASH_FOR_QUERY_PARAMS';

export const SET_RECENT_QUERIES = 'INVESTIGATE_EVENTS::SET_RECENT_QUERIES';
export const SET_VALUE_SUGGESTIONS = 'INVESTIGATE_EVENTS::SET_VALUE_SUGGESTIONS';

// *******
// BEGIN - Should be moved with Download Manager
// *******
// File Extraction Action Types
export const FILE_EXTRACT_JOB_ID_RETRIEVE = 'INVESTIGATE_EVENTS::FILE_EXTRACT_JOB_ID_RETRIEVE';
export const FILE_EXTRACT_JOB_SUCCESS = 'INVESTIGATE_EVENTS::FILE_EXTRACT_JOB_SUCCESS';
export const FILE_EXTRACT_FAILURE = 'INVESTIGATE_EVENTS::FILE_EXTRACT_FAILURE';
export const FILE_EXTRACT_JOB_DOWNLOADED = 'INVESTIGATE_EVENTS::FILE_EXTRACT_JOB_DOWNLOADED';

export const NOTIFICATION_INIT_SUCCESS = 'INVESTIGATE_EVENTS::NOTIFICATION_INIT_SUCCESS';
export const NOTIFICATION_TEARDOWN_SUCCESS = 'INVESTIGATE_EVENTS::NOTIFICATION_TEARDOWN_SUCCESS';
// *******
// END - Should be moved with Download Manager
// *******

// Navigate meta panel
export const SET_META_RESPONSE = 'INVESTIGATE_EVENTS::SET_META_RESPONSE';
export const INIT_STREAM_FOR_META = 'INVESTIGATE_EVENTS::INIT_STREAM_FOR_META';
export const RESET_META_VALUES = 'INVESTIGATE_EVENTS::RESET_META_VALUES';
export const TOGGLE_META_FLAG = 'INVESTIGATE_EVENTS::TOGGLE_META_FLAG';
