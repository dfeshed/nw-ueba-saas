// Endpoint filters, which are same for hosts, files and certificates

export const GET_FILTER = 'ENDPOINT::GET_FILTER';
export const DELETE_FILTER = 'ENDPOINT::DELETE_FILTER';
export const APPLY_FILTER = 'ENDPOINT::APPLY_FILTER';
export const APPLY_SAVED_FILTER = 'ENDPOINT::APPLY_SAVED_FILTER';
export const SET_SAVED_FILTER = 'ENDPOINT::SET_SAVED_FILTER';
export const SAVE_FILTER = 'ENDPOINT::SAVE_FILTER';
export const RESET_FILTER = 'ENDPOINT::RESET_FILTER';


// Risk Score, which are same for Hosts and files.
export const RESET_RISK_SCORE = 'RISK_SCORE::ACTION_TYPES.RESET_RISK_SCORE';

export const RESET_RISK_CONTEXT = 'RISK_SCORE::RESET_RISK_CONTEXT';
export const GET_RISK_SCORE_CONTEXT = 'RISK_SCORE::GET_RISK_SCORE_CONTEXT';
export const GET_RESPOND_SERVER_STATUS = 'INVESTIGATE_FILES::GET_RESPOND_SERVER_STATUS';

export const SET_SELECTED_ALERT = 'RISK_SCORE::SET_SELECTED_ALERT';
export const ACTIVE_RISK_SEVERITY_TAB = 'RISK_SCORE::ACTIVE_RISK_SEVERITY_TAB';
export const GET_RESPOND_EVENTS = 'RISK_SCORE::GET_RESPOND_EVENTS';
export const GET_RESPOND_EVENTS_INITIALIZED = 'RISK_SCORE::GET_RESPOND_EVENTS_INITIALIZED';
export const GET_RESPOND_EVENTS_COMPLETED = 'RISK_SCORE::GET_RESPOND_EVENTS_COMPLETED';

export const GET_EVENTS = 'RISK_SCORE::GET_EVENTS';
export const GET_EVENTS_COMPLETED = 'RISK_SCORE::GET_EVENTS_COMPLETED';
export const GET_EVENTS_ERROR = 'RISK_SCORE::GET_EVENTS_ERROR';

export const SET_SELECTED_FILE = 'RISK_SCORE::SET_SELECTED_FILE';
export const EXPANDED_EVENT = 'RISK_SCORE::EXPANDED_EVENT';
export const CLEAR_EVENTS = 'RISK_SCORE::CLEAR_EVENTS';


// Investigate preference
export const SET_INVESTIGATE_PREFERENCE = 'ENDPOINT::SET_INVESTIGATE_PREFERENCE';
export const SET_RESTRICTED_FILE_LIST = 'ENDPOINT::SET_RESTRICTED_FILE_LIST';
