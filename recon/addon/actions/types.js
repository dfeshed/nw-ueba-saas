// Sring values of actions must be prefixed with RECON::

// Visual action types
export const TOGGLE_HEADER = 'RECON::TOGGLE_HEADER';
export const TOGGLE_REQUEST = 'RECON::TOGGLE_REQUEST';
export const TOGGLE_RESPONSE = 'RECON::TOGGLE_RESPONSE';
export const TOGGLE_META = 'RECON::TOGGLE_META';
export const TOGGLE_EXPANDED = 'RECON::TOGGLE_EXPANDED';
export const CLOSE_RECON = 'RECON::CLOSE_RECON';
export const OPEN_RECON = 'RECON::OPEN_RECON';
export const INITIALIZE = 'RECON::INITIALIZE';

// Data action types
export const CHANGE_RECON_VIEW = 'RECON::CHANGE_RECON_VIEW';
export const LANGUAGE_RETRIEVE_SUCCESS = 'RECON::LANGUAGE_RETRIEVE_SUCCESS';
export const ALIASES_RETRIEVE_SUCCESS = 'RECON::ALIASES_RETRIEVE_SUCCESS';
export const META_RETRIEVE = 'RECON::META_RETRIEVE';
export const SUMMARY_RETRIEVE = 'RECON::SUMMARY_RETRIEVE';

// Content action types
export const CONTENT_RETRIEVE_STARTED = 'RECON::CONTENT_RETRIEVE_STARTED';
export const FILES_RETRIEVE_SUCCESS = 'RECON::FILES_RETRIEVE_SUCCESS';
export const PACKETS_RETRIEVE_PAGE = 'RECON::PACKETS_RETRIEVE_PAGE';
export const CONTENT_RETRIEVE_FAILURE = 'RECON::CONTENT_RETRIEVE_FAILURE';

// User interaction
export const FILES_SELECT_ALL = 'RECON::FILES_SELECT_ALL';
export const FILES_DESELECT_ALL = 'RECON::FILES_DESELECT_ALL';
export const FILES_FILE_TOGGLED = 'RECON::FILES_FILE_TOGGLED';
export const FILE_EXTRACT_JOB_ID_RETRIEVE = 'RECON::FILE_EXTRACT_JOB_ID_RETRIEVE';
export const FILE_EXTRACT_JOB_SUCCESS = 'RECON::FILE_EXTRACT_JOB_SUCCESS';
export const FILE_EXTRACT_JOB_DOWNLOADED = 'RECON::FILE_EXTRACT_JOB_DOWNLOADED';
export const NOTIFICATION_INIT_SUCCESS = 'RECON::NOTIFICATION_INIT_SUCCESS';
export const NOTIFICATION_TEARDOWN_SUCCESS = 'RECON::NOTIFICATION_TEARDOWN_SUCCESS';
export const SHOW_PACKET_TOOLTIP = 'RECON::SHOW_PACKET_TOOLTIP';
export const HIDE_PACKET_TOOLTIP = 'RECON::HIDE_PACKET_TOOLTIP';

// Set the event type i.e. LOG, NETWORK, etc
export const SET_EVENT_TYPE = 'RECON::SET_EVENT_TYPE';
