// Schema action types
export const SCHEMA_RETRIEVE = 'INVESTIGATE_FILES::SCHEMA_RETRIEVE';
export const FETCH_NEXT_FILES = 'INVESTIGATE_FILES::FETCH_NEXT_FILES';

// Filters
export const GET_FILTER = 'INVESTIGATE_FILES::GET_FILTER';
export const DELETE_FILTER = 'INVESTIGATE_FILES::DELETE_FILTER';
export const APPLY_FILTER = 'INVESTIGATE_FILES::APPLY_FILTER';
export const APPLY_SAVED_FILTER = 'INVESTIGATE_FILES::APPLY_SAVED_FILTER';
export const SET_SAVED_FILTER = 'INVESTIGATE_FILES::SET_SAVED_FILTER';
export const SAVE_FILTER = 'INVESTIGATE_FILES::SAVE_FILTER';


// UI action types
export const SET_SORT_BY = 'INVESTIGATE_FILES::SET_SORT_BY';
export const RESET_FILES = 'INVESTIGATE_FILES::RESET_FILES';
export const INCREMENT_PAGE_NUMBER = 'INVESTIGATE_FILES::INCREMENT_PAGE_NUMBER';
export const DOWNLOAD_FILE_AS_CSV = 'INVESTIGATE_FILES::DOWNLOAD_FILE_AS_CSV';
export const UPDATE_COLUMN_VISIBILITY = 'INVESTIGATE_FILES::UPDATE_COLUMN_VISIBILITY';
export const RESET_DOWNLOAD_ID = 'INVESTIGATE_FILES::RESET_DOWNLOAD_ID';

export const SET_FILE_PREFERENCES = 'INVESTIGATE_FILES::SET_FILE_PREFERENCES';
export const GET_LIST_OF_SERVICES = 'INVESTIGATE_FILES::GET_LIST_OF_SERVICES';
export const CHANGE_DATASOURCE_TAB = 'INVESTIGATE_FILES::CHANGE_DATASOURCE_TAB';
export const SET_CONTEXT_DATA = 'INVESTIGATE_FILES::SET_CONTEXT_DATA';
export const CLEAR_PREVIOUS_CONTEXT = 'INVESTIGATE_FILES::CLEAR_PREVIOUS_CONTEXT';
export const CONTEXT_ERROR = 'INVESTIGATE_FILES::ACTION_TYPES.CONTEXT_ERROR';
export const SET_SELECTED_INDEX = 'INVESTIGATE_FILES::SET_SELECTED_INDEX';

export const TOGGLE_SELECTED_FILE = 'INVESTIGATE_FILES::TOGGLE_SELECTED_FILE';
export const SELECT_ALL_FILES = 'INVESTIGATE_FILES::SELECT_ALL_FILES';
export const DESELECT_ALL_FILES = 'INVESTIGATE_FILES::DESELECT_ALL_FILES';
export const SELECTED_FILE_ROW = 'INVESTIGATE_FILES::SELECTED_FILE_ROW';
export const CHANGE_FILE_DETAIL_TAB = 'INVESTIGATE_FILES::CHANGE_FILE_DETAIL_TAB';
export const USER_LEFT_FILES_PAGE = 'INVESTIGATE_FILES::USER_LEFT_FILES_PAGE';

// File status
export const SAVE_FILE_STATUS = 'INVESTIGATE_FILES::SAVE_FILE_STATUS';
export const GET_FILE_STATUS_HISTORY = 'INVESTIGATE_FILES::GET_FILE_STATUS_HISTORY';
export const GET_FILE_STATUS = 'INVESTIGATE_FILES::GET_FILE_STATUS';

export const SET_AGENT_COUNT = 'INVESTIGATE_FILES::SET_AGENT_COUNT';
export const SET_AGENT_COUNT_FAILED = 'INVESTIGATE_FILES::SET_AGENT_COUNT_FAILED';
export const GET_AGENTS_COUNT_SAGA = 'INVESTIGATE_FILES::GET_AGENTS_COUNT_SAGA';

// Endpoint server
export const ENDPOINT_SERVER_SELECTED = 'INVESTIGATE_FILES::ENDPOINT_SERVER_SELECTED';
export const LIST_OF_ENDPOINT_SERVERS = 'INVESTIGATE_FILES::LIST_OF_ENDPOINT_SERVERS';
export const ENDPOINT_SERVER_STATUS = 'INVESTIGATE_FILES::ENDPOINT_SERVER_STATUS';

export const SET_HOST_NAME_LIST = 'INVESTIGATE_FILES::SET_HOST_NAME_LIST';
export const FETCH_HOST_NAME_LIST_ERROR = 'INVESTIGATE_FILES::FETCH_HOST_NAME_LIST_ERROR';
export const INIT_FETCH_HOST_NAME_LIST = 'INVESTIGATE_FILES::INIT_FETCH_HOST_NAME_LIST';
export const META_VALUE_COMPLETE = 'INVESTIGATE_FILES::META_VALUE_COMPLETE';
export const FETCH_REMEDIATION_STATUS = 'INVESTIGATE_FILES::FETCH_REMEDIATION_STATUS';

// Risk Score Related Action Types

export const SET_SELECTED_FILE = 'INVESTIATE_FILES::SET_SELECTED_FILE';
export const INITIALIZE_FILE_DETAIL = 'INVESTIATE_FILES::INITIALIZE_FILE_DETAIL';

// Certificate view
export const TOGGLE_CERTIFICATE_VIEW = 'INVESTIGATE_FILES::TOGGLE_CERTIFICATE_VIEW';
export const GET_CERTIFICATES = 'INVESTIGATE_FILES::GET_CERTIFICATES';
export const SAVE_CERTIFICATE_STATUS = 'INVESTIGATE_FILES::SAVE_CERTIFICATE_STATUS';
export const GET_CERTIFICATE_STATUS = 'INVESTIGATE_FILES::GET_CERTIFICATE_STATUS';
export const INCREMENT_CERTIFICATE_PAGE_NUMBER = 'INVESTIGATE_FILES::INCREMENT_CERTIFICATE_PAGE_NUMBER';
export const TOGGLE_SELECTED_CERTIFICATE = 'INVESTIGATE_FILES::TOGGLE_SELECTED_CERTIFICATE';
export const RESET_CERTIFICATES = 'INVESTIGATE_FILES::RESET_CERTIFICATES';
export const UPDATE_CERTIFICATE_COLUMN_VISIBILITY = 'INVESTIGATE_FILES::UPDATE_CERTIFICATE_COLUMN_VISIBILITY';
export const CLOSE_CERTIFICATE_VIEW = 'INVESTIGATE_FILES::CLOSE_CERTIFICATE_VIEW';

export const FETCH_ALL_FILES = 'INVESTIGATE_FILES::FETCH_ALL_FILES';
export const AGENT_COUNT_INIT = 'INVESTIGATE_FILES::AGENT_COUNT_INIT';
