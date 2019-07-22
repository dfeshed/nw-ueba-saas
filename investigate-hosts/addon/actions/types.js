// Data Retrieval Related Action Types
export const FETCH_ALL_FILTERS = 'INVESTIGATE_HOSTS::FETCH_ALL_FILTERS';
export const FETCH_ALL_MACHINES = 'INVESTIGATE_HOSTS::FETCH_ALL_MACHINES';
export const FETCH_NEXT_MACHINES = 'INVESTIGATE_HOSTS::FETCH_NEXT_MACHINES';
export const FETCH_ALL_SCHEMAS = 'INVESTIGATE_HOSTS::FETCH_ALL_SCHEMAS';
export const FETCH_DOWNLOAD_JOB_ID = 'INVESTIGATE_HOSTS::FETCH_DOWNLOAD_JOB_ID';
export const DELETE_HOSTS = 'INVESTIGATE_HOSTS::DELETE_HOSTS';

export const FETCH_AGENT_STATUS = 'INVESTIGATE_HOSTS::FETCH_AGENT_STATUS';

export const FETCH_ALL_SNAP_SHOTS = 'INVESTIGATE_HOSTS::FETCH_ALL_SNAP_SHOTS';
export const FETCH_HOST_DETAILS = 'INVESTIGATE_HOSTS::FETCH_HOST_DETAILS';
export const FETCH_POLICY_DETAILS = 'INVESTIGATE_HOSTS::FETCH_POLICY_DETAILS';
export const FETCH_DOWNLOAD_FILECONTEXT_JOB_ID = 'INVESTIGATE_HOSTS::FETCH_DOWNLOAD_FILECONTEXT_JOB_ID';
export const FETCH_HOST_OVERVIEW = 'INVESTIGATE_HOSTS::FETCH_HOST_OVERVIEW';

// UI Action Types
export const TOGGLE_MACHINE_SELECTED = 'INVESTIGATE_HOSTS::TOGGLE_MACHINE_SELECTED';
export const EXECUTE_QUERY = 'INVESTIGATE_HOSTS::EXECUTE_QUERY';
export const SET_SELECTED_HOST = 'INVESTIGATE_HOSTS::SET_SELECTED_HOST';
export const SET_FOCUSED_HOST = 'INVESTIGATE_HOSTS::SET_FOCUSED_HOST';
export const SET_FOCUSED_HOST_INDEX = 'INVESTIGATE_HOSTS::SET_FOCUSED_HOST_INDEX';
export const CHANGE_HOST_LIST_PROPERTY_TAB = 'INVESTIGATE_HOSTS::CHANGE_HOST_LIST_PROPERTY_TAB';
export const TOGGLE_ICON_VISIBILITY = 'INVESTIGATE_HOSTS::TOGGLE_ICON_VISIBILITY';

export const CHANGE_DETAIL_TAB = 'INVESTIGATE_HOSTS::CHANGE_DETAIL_TAB';
export const CHANGE_AUTORUNS_TAB = 'INVESTIGATE_HOSTS::CHANGE_AUTORUNS_TAB';
export const CHANGE_PROPERTY_TAB = 'INVESTIGATE_HOSTS::CHANGE_PROPERTY_TAB';
export const CHANGE_ANOMALIES_TAB = 'INVESTIGATE_HOSTS::CHANGE_ANOMALIES_TAB';
export const CHANGE_DATASOURCE_TAB = 'INVESTIGATE_HOSTS::CHANGE_DATASOURCE_TAB';
export const RESET_HOST_DETAILS = 'INVESTIGATE_HOSTS::RESET_HOST_DETAILS';
export const RESET_INPUT_DATA = 'INVESTIGATE_HOSTS::RESET_INPUT_DATA';
export const RESET_HOST_DOWNLOAD_LINK = 'INVESTIGATE_HOSTS::RESET_HOST_DOWNLOAD_LINK';
export const ARRANGE_SECURITY_CONFIGURATIONS = 'INVESTIGATE_HOSTS::ARRANGE_SECURITY_CONFIGURATIONS';
export const SET_SYSTEM_INFORMATION_TAB = 'INVESTIGATE_HOSTS::SET_SYSTEM_INFORMATION_TAB';
export const SET_PROPERTY_PANEL_TAB = 'INVESTIGATE_HOSTS::SET_PROPERTY_PANEL_TAB';
export const CHANGE_PROPERTY_PANEL_TAB = 'INVESTIGATE_HOSTS::CHANGE_PROPERTY_PANEL_TAB';
export const TOGGLE_DETAIL_RIGHT_PANEL = 'INVESTIGATE_HOSTS::TOGGLE_DETAIL_RIGHT_PANEL';

// Filter
export const TOGGLE_CUSTOM_FILTER = 'INVESTIGATE_HOSTS::TOGGLE_CUSTOM_FILTER';
export const REMOVE_HOST_FILTER = 'INVESTIGATE_HOSTS::REMOVE_HOST_FILTER';
export const SET_ACTIVE_FILTER = 'INVESTIGATE_HOSTS::SET_ACTIVE_FILTER';
export const ADD_SYSTEM_FILTER = 'INVESTIGATE_HOSTS::ADD_SYSTEM_FILTER';
export const ADD_HOST_FILTER = 'INVESTIGATE_HOSTS::ADD_HOST_FILTER';
export const RESET_HOST_FILTERS = 'INVESTIGATE_HOSTS::RESET_HOST_FILTERS';
export const UPDATE_HOST_FILTER = 'INVESTIGATE_HOSTS::UPDATE_HOST_FILTER';
export const UPDATE_HOST_DATETIME_FILTER = 'INVESTIGATE_HOSTS::UPDATE_HOST_DATETIME_FILTER';
export const UPDATE_FILTER_LIST = 'INVESTIGATE_HOSTS::UPDATE_FILTER_LIST';

// Process
export const GET_PROCESS_LIST = 'INVESTIGATE_HOSTS::GET_PROCESS_LIST';
export const GET_PROCESS_TREE = 'INVESTIGATE_HOSTS::GET_PROCESS_TREE';
export const TOGGLE_PROCESS_VIEW = 'INVESTIGATE_HOSTS::TOGGLE_PROCESS_VIEW';
export const SET_SORT_BY = 'INVESTIGATE_HOSTS::SET_SORT_BY';
export const RESET_PROCESS_LIST = 'INVESTIGATE_HOSTS::RESET_PROCESS_LIST';
export const GET_PROCESS = 'INVESTIGATE_HOSTS::GET_PROCESS';
export const GET_PROCESS_FILE_CONTEXT = 'INVESTIGATE_HOSTS::GET_PROCESS_FILE_CONTEXT';

export const GET_LIBRARY_PROCESS_INFO = 'INVESTIGATE_HOSTS::GET_LIBRARY_PROCESS_INFO';
export const SET_SELECTED_PROCESS_ID = 'INVESTIGATE_HOSTS::SET_SELECTED_PROCESS_ID';
export const SET_SELECTED_PROCESS = 'INVESTIGATE_HOSTS::SET_SELECTED_PROCESS';
export const SELECT_ALL_PROCESS = 'INVESTIGATE_HOSTS::SELECT_ALL_PROCESS';
export const DESELECT_ALL_PROCESS = 'INVESTIGATE_HOSTS::DESELECT_ALL_PROCESS';
export const TOGGLE_PROCESS_DETAILS_VIEW = 'INVESTIGATE_HOSTS::TOGGLE_PROCESS_DETAILS_VIEW';
export const TOGGLE_PROCESS_DETAILS_ROW = 'INVESTIGATE_HOSTS::TOGGLE_PROCESS_DETAILS_ROW';
export const CLOSE_PROCESS_DETAILS = 'INVESTIGATE_HOSTS::CLOSE_PROCESS_DETAILS';
export const SET_PROCESS_DLL_ROW_ID = 'INVESTIGATE_HOSTS::SET_PROCESS_DLL_ROW_ID';
export const OPEN_PROCESS_DETAILS = 'INVESTIGATE_HOSTS::OPEN_PROCESS_DETAILS';

// DATA
export const INITIALIZE_DATA = 'INVESTIGATE_HOSTS::INITIALIZE_DATA';

export const SET_SCAN_TIME = 'INVESTIGATE_HOSTS::SET_SCAN_TIME';
export const SET_ANIMATION = 'INVESTIGATE_HOSTS::SET_ANIMATION';

// Files
export const SET_SELECTED_FILE = 'INVESTIGATE_HOSTS::SET_SELECTED_FILE';
export const TOGGLE_SELECTED_FILE = 'INVESTIGATE_HOSTS::TOGGLE_SELECTED_FILE';
export const SELECT_ALL_FILES = 'INVESTIGATE_HOSTS::SELECT_ALL_FILES';
export const DESELECT_ALL_FILES = 'INVESTIGATE_HOSTS::DESELECT_ALL_FILES';
export const GET_FILE_STATUS = 'INVESTIGATE_HOSTS::GET_FILE_STATUS';
export const SAVE_FILE_STATUS = 'INVESTIGATE_HOSTS::SAVE_FILE_STATUS';

// Explore
export const START_FILE_SEARCH = 'INVESTIGATE_HOSTS::START_FILE_SEARCH';
export const SELECTED_TAB_DATA = 'INVESTIGATE_HOSTS::SELECTED_TAB_DATA';
export const FILE_SEARCH_PAGE = 'INVESTIGATE_HOSTS::FILE_SEARCH_PAGE';
export const FILE_SEARCH_END = 'INVESTIGATE_HOSTS::FILE_SEARCH_END';


// FileContext
export const FETCH_FILE_CONTEXT_AUTORUNS = 'INVESTIGATE_HOSTS::FETCH_FILE_CONTEXT_AUTORUNS';
export const FETCH_FILE_CONTEXT_SERVICES = 'INVESTIGATE_HOSTS::FETCH_FILE_CONTEXT_SERVICES';
export const FETCH_FILE_CONTEXT_TASKS = 'INVESTIGATE_HOSTS::FETCH_FILE_CONTEXT_TASKS';
export const SET_AUTORUN_SELECTED_ROW = 'INVESTIGATE_HOSTS::SET_AUTORUN_SELECTED_ROW';
export const SET_HOST_DETAIL_PROPERTY_TAB = 'INVESTIGATE_HOSTS::SET_HOST_DETAIL_PROPERTY_TAB';

// Autoruns
export const TOGGLE_SELECTED_AUTORUN = 'INVESTIGATE_HOSTS::TOGGLE_SELECTED_AUTORUN';
export const TOGGLE_ALL_AUTORUN_SELECTION = 'INVESTIGATE_HOSTS::TOGGLE_ALL_AUTORUN_SELECTION';
export const SAVE_AUTORUN_STATUS = 'INVESTIGATE_HOSTS::SAVE_AUTORUN_STATUS';
export const GET_AUTORUN_STATUS = 'INVESTIGATE_HOSTS::GET_AUTORUN_STATUS';

// Autoruns Services
export const TOGGLE_SELECTED_SERVICE = 'INVESTIGATE_HOSTS::TOGGLE_SELECTED_SERVICE';
export const TOGGLE_ALL_SERVICE_SELECTION = 'INVESTIGATE_HOSTS::TOGGLE_ALL_SERVICE_SELECTION';
export const SAVE_SERVICE_STATUS = 'INVESTIGATE_HOSTS::SAVE_SERVICE_STATUS';
export const GET_SERVICE_STATUS = 'INVESTIGATE_HOSTS::GET_SERVICE_STATUS';

// Autoruns Tasls
export const TOGGLE_SELECTED_TASK = 'INVESTIGATE_HOSTS::TOGGLE_SELECTED_TASK';
export const TOGGLE_ALL_TASK_SELECTION = 'INVESTIGATE_HOSTS::TOGGLE_ALL_TASK_SELECTION';
export const SAVE_TASK_STATUS = 'INVESTIGATE_HOSTS::SAVE_TASK_STATUS';
export const GET_TASK_STATUS = 'INVESTIGATE_HOSTS::GET_TASK_STATUS';

export const FETCH_FILE_CONTEXT_IMAGE_HOOKS = 'INVESTIGATE_HOSTS::FETCH_FILE_CONTEXT_IMAGE_HOOKS';
export const FETCH_FILE_CONTEXT_THREADS = 'INVESTIGATE_HOSTS::FETCH_FILE_CONTEXT_THREADS';
export const FETCH_FILE_CONTEXT_KERNEL_HOOKS = 'INVESTIGATE_HOSTS::FETCH_FILE_CONTEXT_KERNEL_HOOKS';
export const SET_ANOMALIES_SELECTED_ROW = 'INVESTIGATE_HOSTS::SET_ANOMALIES_SELECTED_ROW';

// Drivers
export const FETCH_FILE_CONTEXT_DRIVERS = 'INVESTIGATE_HOSTS::FETCH_FILE_CONTEXT_DRIVERS';
export const SET_DRIVERS_SELECTED_ROW = 'INVESTIGATE_HOSTS::SET_DRIVERS_SELECTED_ROW';
export const TOGGLE_SELECTED_DRIVER = 'INVESTIGATE_HOSTS::TOGGLE_SELECTED_DRIVER';
export const TOGGLE_ALL_DRIVER_SELECTION = 'INVESTIGATE_HOSTS::TOGGLE_ALL_DRIVER_SELECTION';
export const SAVE_DRIVER_STATUS = 'INVESTIGATE_HOSTS::SAVE_DRIVER_STATUS';
export const GET_DRIVER_STATUS = 'INVESTIGATE_HOSTS::GET_DRIVER_STATUS';

// libraries
export const TOGGLE_SELECTED_LIBRARY = 'INVESTIGATE_HOSTS::TOGGLE_SELECTED_LIBRARY';
export const TOGGLE_ALL_LIBRARY_SELECTION = 'INVESTIGATE_HOSTS::TOGGLE_ALL_LIBRARY_SELECTION';
export const SAVE_LIBRARY_STATUS = 'INVESTIGATE_HOSTS::SAVE_LIBRARY_STATUS';
export const GET_LIBRARY_STATUS = 'INVESTIGATE_HOSTS::GET_LIBRARY_STATUS';

// Downloads
export const RESET_DOWNLOADED_FILES = 'INVESTIGATE_HOSTS::RESET_DOWNLOADED_FILES';
export const FETCH_NEXT_DOWNLOADED_FILES = 'INVESTIGATE_HOSTS::FETCH_NEXT_DOWNLOADED_FILES';
export const FETCH_ALL_DOWNLOADED_FILES = 'INVESTIGATE_HOSTS::FETCH_ALL_DOWNLOADED_FILES';
export const INCREMENT_DOWNLOADED_FILES_PAGE_NUMBER = 'INVESTIGATE_HOSTS::INCREMENT_DOWNLOADED_FILES_PAGE_NUMBER';
export const SET_DOWNLOADED_FILES_SORT_BY = 'INVESTIGATE_HOSTS::SET_DOWNLOADED_FILES_SORT_BY';
export const TOGGLE_SELECTED_DOWNLOADED_FILE = 'INVESTIGATE_HOSTS::TOGGLE_SELECTED_DOWNLOADED_FILE';
export const SELECT_ALL_DOWNLOADED_FILES = 'INVESTIGATE_HOSTS::SELECT_ALL_DOWNLOADED_FILES';
export const DESELECT_ALL_DOWNLOADED_FILES = 'INVESTIGATE_HOSTS::DESELECT_ALL_DOWNLOADED_FILES';
export const SET_SELECTED_DOWNLOADED_FILE_INDEX = 'INVESTIGATE_HOSTS::SET_SELECTED_DOWNLOADED_FILE_INDEX';
export const TOGGLE_MFT_VIEW = 'INVESTIGATE_HOSTS::TOGGLE_MFT_VIEW';
export const FETCH_MFT_SUBDIRECTORIES = 'INVESTIGATE_HOSTS::FETCH_MFT_SUBDIRECTORIES';
export const FETCH_MFT_SUBDIRECTORIES_AND_FILES = 'INVESTIGATE_HOSTS::FETCH_MFT_SUBDIRECTORIES_AND_FILES';
export const SET_SELECTED_MFT_PARENT_DIRECTORY = 'INVESTIGATE_HOSTS::SET_SELECTED_MFT_PARENT_DIRECTORY';
export const SET_SELECTED_MFT_DIRECTORY_FOR_DETAILS = 'INVESTIGATE_HOSTS::SET_SELECTED_MFT_DIRECTORY_FOR_DETAILS';
export const RESET_MFT_FILE_DATA = 'INVESTIGATE_HOSTS::RESET_MFT_FILE_DATA';
export const TOGGLE_SELECTED_MFT_FILE = 'INVESTIGATE_HOSTS::TOGGLE_SELECTED_MFT_FILE';
export const SET_MFT_FILES_SORT_BY = 'INVESTIGATE_HOSTS::SET_MFT_FILES_SORT_BY';
export const SET_SELECTED_DOWNLOADED_MFT_FILE_INDEX = 'INVESTIGATE_HOSTS::SET_SELECTED_DOWNLOADED_MFT_FILE_INDEX';
export const DESELECT_ALL_DOWNLOADED_MFT_FILES = 'INVESTIGATE_HOSTS::DESELECT_ALL_DOWNLOADED_MFT_FILES';
export const SELECT_ALL_DOWNLOADED_MFT_FILES = 'INVESTIGATE_HOSTS::SELECT_ALL_DOWNLOADED_MFT_FILES';
export const INCREMENT_DOWNLOADED_MFT_FILES_PAGE_NUMBER = 'INVESTIGATE_HOSTS::INCREMENT_DOWNLOADED_MFT_FILES_PAGE_NUMBER';
export const FETCH_NEXT_MFT_SUBDIRECTORIES_AND_FILES = 'INVESTIGATE_HOSTS::FETCH_NEXT_MFT_SUBDIRECTORIES_AND_FILES';
export const TOGGLE_MFT_FILTER_PANEL = 'INVESTIGATE_HOSTS::TOGGLE_MFT_FILTER_PANEL';


export const FETCH_FILE_CONTEXT_DLLS = 'INVESTIGATE_HOSTS::FETCH_FILE_CONTEXT_DLLS';
export const SET_DLLS_SELECTED_ROW = 'INVESTIGATE_HOSTS::SET_DLLS_SELECTED_ROW';

export const SET_HOST_COLUMN_SORT = 'INVESTIGATE_HOSTS::SET_HOST_COLUMN_SORT';
export const USER_LEFT_HOST_LIST_PAGE = 'INVESTIGATE_HOSTS::USER_LEFT_HOST_LIST_PAGE';
export const SET_APPLIED_HOST_FILTER = 'INVESTIGATE_HOSTS::SET_APPLIED_HOST_FILTER';
export const TOGGLE_EXPLORE_SEARCH_RESULTS = 'INVESTIGATE_HOSTS::TOGGLE_EXPLORE_SEARCH_RESULTS';
export const RESET_EXPLORED_RESULTS = 'INVESTIGATE_HOSTS::RESET_EXPLORED_RESULTS';

export const SELECT_ALL_HOSTS = 'INVESTIGATE_HOSTS::SELECT_ALL_HOSTS';
export const DESELECT_ALL_HOSTS = 'INVESTIGATE_HOSTS::DESELECT_ALL_HOSTS';
export const TOGGLE_DELETE_HOSTS_MODAL = 'INVESTIGATE_HOSTS::TOGGLE_DELETE_HOSTS_MODAL';

export const DELETE_SAVED_SEARCH = 'INVESTIGATE_HOSTS::DELETE_SAVED_SEARCH';

export const SET_PREFERENCES = 'INVESTIGATE_HOSTS::SET_PREFERENCES';
export const GET_LIST_OF_SERVICES = 'INVESTIGATE_HOSTS::GET_LIST_OF_SERVICES';

export const HOST_DETAILS_DATATABLE_SORT_CONFIG = 'INVESTIGATE_HOSTS::HOST_DETAILS_DATATABLE_SORT_CONFIG';

export const RESET_HOSTS = 'INVESTIGATE_HOSTS::RESET_HOSTS';

export const SET_CONTEXT_DATA = 'INVESTIGATE_HOSTS::SET_CONTEXT_DATA';
export const CLEAR_PREVIOUS_CONTEXT = 'INVESTIGATE_HOSTS::CLEAR_PREVIOUS_CONTEXT';
export const CONTEXT_ERROR = 'INVESTIGATE_HOSTS::CONTEXT_ERROR';

// Endpoint server
export const ENDPOINT_SERVER_SELECTED = 'INVESTIGATE_HOSTS::ENDPOINT_SERVER_SELECTED';
export const LIST_OF_ENDPOINT_SERVERS = 'INVESTIGATE_HOSTS::LIST_OF_ENDPOINT_SERVERS';
export const ENDPOINT_SERVER_STATUS = 'INVESTIGATE_HOSTS::ENDPOINT_SERVER_STATUS';
export const CHANGE_ALERT_TAB = 'INVESTIGATE_HOSTS::CHANGE_ALERT_TAB';

// File Context
export const SET_FILE_CONTEXT_ROW_SELECTION = 'INVESTIGATE_HOSTS::SET_FILE_CONTEXT_ROW_SELECTION';
export const SET_FILE_CONTEXT_COLUMN_SORT = 'INVESTIGATE_HOSTS::SET_FILE_CONTEXT_COLUMN_SORT';
export const FETCH_FILE_CONTEXT = 'INVESTIGATE_HOSTS::FETCH_FILE_CONTEXT';
export const TOGGLE_FILE_CONTEXT_ROW_SELECTION = 'INVESTIGATE_HOSTS::TOGGLE_FILE_CONTEXT_ROW_SELECTION';
export const TOGGLE_FILE_CONTEXT_ALL_SELECTION = 'INVESTIGATE_HOSTS::TOGGLE_FILE_CONTEXT_ALL_SELECTION';
export const DESELECT_FILE_CONTEXT_ALL_SELECTION = 'INVESTIGATE_HOSTS::DESELECT_FILE_CONTEXT_ALL_SELECTION';
export const SAVE_FILE_CONTEXT_FILE_STATUS = 'INVESTIGATE_HOSTS::SAVE_FILE_CONTEXT_FILE_STATUS';
export const GET_FILE_CONTEXT_FILE_STATUS = 'INVESTIGATE_HOSTS::GET_FILE_CONTEXT_FILE_STATUS';
export const RESET_CONTEXT_DATA = 'INVESTIGATE_HOSTS::RESET_CONTEXT_DATA';
export const FETCH_REMEDIATION_STATUS = 'INVESTIGATE_HOSTS::FETCH_REMEDIATION_STATUS';
export const FILE_CONTEXT_RESET_SELECTION = 'INVESTIGATE_HOSTS::FILE_CONTEXT_RESET_SELECTION';
export const SET_FILE_CONTEXT_ROW_INDEX = 'INVESTIGATE_HOSTS::SET_FILE_CONTEXT_ROW_INDEX';

export const SET_ROW_INDEX = 'INVESTIGATE_HOSTS::SET_ROW_INDEX';
export const SAVE_COLUMN_CONFIG = 'INVESTIGATE_HOSTS::SAVE_COLUMN_CONFIG';

// agent count saga
export const SET_AGENT_COUNT = 'INVESTIGATE_HOSTS::SET_AGENT_COUNT';
export const AGENT_COUNT_INIT = 'INVESTIGATE_HOSTS::AGENT_COUNT_INIT';
export const SET_AGENT_COUNT_FAILED = 'INVESTIGATE_HOSTS::SET_AGENT_COUNT_FAILED';
export const GET_AGENTS_COUNT_SAGA = 'INVESTIGATE_HOSTS::GET_AGENTS_COUNT_SAGA';
