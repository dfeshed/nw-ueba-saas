// Sring values of actions must be prefixed with RECON::

export const REHYDRATE = 'persist/REHYDRATE';

export const INITIALIZE = 'RECON::INITIALIZE';

export const SET_INDEX_AND_TOTAL = 'RECON::SET_INDEX_AND_TOTAL';

// Visual action types
export const CHANGE_RECON_VIEW = 'RECON::CHANGE_RECON_VIEW';
export const STORE_RECON_VIEW = 'RECON::STORE_RECON_VIEW';
export const TOGGLE_HEADER = 'RECON::TOGGLE_HEADER';
export const TOGGLE_REQUEST = 'RECON::TOGGLE_REQUEST';
export const TOGGLE_RESPONSE = 'RECON::TOGGLE_RESPONSE';
export const TOGGLE_META = 'RECON::TOGGLE_META';
export const TOGGLE_EXPANDED = 'RECON::TOGGLE_EXPANDED';
export const CLOSE_RECON = 'RECON::CLOSE_RECON';
export const OPEN_RECON = 'RECON::OPEN_RECON';
export const SET_PREFERENCES = 'RECON::SET_PREFERENCES';
export const CHANGE_PAGE_NUMBER = 'RECON::CHANGE_PAGE_NUMBER';
export const TEXT_CHANGE_PAGE_NUMBER = 'RECON::TEXT_CHANGE_PAGE_NUMBER';
export const CHANGE_PACKETS_PER_PAGE = 'RECON::CHANGE_PACKETS_PER_PAGE';
export const RESET_PREFERENCES = 'RECON::RESET_PREFERENCES';

// Meta action types
export const META_RETRIEVE = 'RECON::META_RETRIEVE';

// Packet action types
export const TOGGLE_BYTE_STYLING = 'RECON::TOGGLE_BYTE_STYLING';
export const TOGGLE_KNOWN_SIGNATURES = 'RECON::TOGGLE_KNOWN_SIGNATURES';
export const TOGGLE_PACKET_PAYLOAD_ONLY = 'RECON::TOGGLE_PACKET_PAYLOAD_ONLY';
export const SHOW_PACKET_TOOLTIP = 'RECON::SHOW_PACKET_TOOLTIP';
export const HIDE_PACKET_TOOLTIP = 'RECON::HIDE_PACKET_TOOLTIP';
export const PACKETS_RECEIVE_PAGE = 'RECON::PACKETS_RECEIVE_PAGE';
export const PACKETS_RENDER_NEXT = 'RECON::PACKETS_RENDER_NEXT';
export const MAIL_RENDER_NEXT = 'RECON::MAIL_RENDER_NEXT';

// Text action types
export const TEXT_RECEIVE_PAGE = 'RECON::TEXT_RECEIVE_PAGE';
export const TEXT_RENDER_NEXT = 'RECON::TEXT_RENDER_NEXT';
export const TEXT_UPDATE_CURSOR = 'RECON::TEXT_UPDATE_CURSOR';
export const TEXT_HIGHLIGHT_META = 'RECON::TEXT_HIGHLIGHT_META';
export const TOGGLE_TEXT_DECODE = 'RECON::TOGGLE_TEXT_DECODE';

// Mail action types
export const EMAIL_RECEIVE_PAGE = 'RECON::EMAIL_RECEIVE_PAGE';
export const EMAIL_RENDER_NEXT = 'RECON::EMAIL_RENDER_NEXT';

// Dictionary action types
export const LANGUAGE_AND_ALIASES_RETRIEVE = 'RECON::LANGUAGE_AND_ALIASES_RETRIEVE';

// Summary/header
export const SUMMARY_RETRIEVE = 'RECON::SUMMARY_RETRIEVE';

// Files
export const FILES_SELECT_ALL = 'RECON::FILES_SELECT_ALL';
export const FILES_DESELECT_ALL = 'RECON::FILES_DESELECT_ALL';
export const FILES_FILE_TOGGLED = 'RECON::FILES_FILE_TOGGLED';
export const FILES_FILE_SELECTED = 'RECON::FILES_FILE_SELECTED';
export const FILES_FILE_DESELECTED = 'RECON::FILES_FILE_DESELECTED';
export const FILE_EXTRACT_JOB_ID_RETRIEVE = 'RECON::FILE_EXTRACT_JOB_ID_RETRIEVE';
export const FILE_EXTRACT_JOB_SUCCESS = 'RECON::FILE_EXTRACT_JOB_SUCCESS';
export const FILE_EXTRACT_FAILURE = 'RECON::FILE_EXTRACT_FAILURE';
export const FILE_EXTRACT_JOB_DOWNLOADED = 'RECON::FILE_EXTRACT_JOB_DOWNLOADED';
export const FILES_RETRIEVE_SUCCESS = 'RECON::FILES_RETRIEVE_SUCCESS';

// Generic content action types
export const CONTENT_RETRIEVE_STARTED = 'RECON::CONTENT_RETRIEVE_STARTED';
export const CONTENT_RETRIEVE_FAILURE = 'RECON::CONTENT_RETRIEVE_FAILURE';
export const SET_FATAL_API_ERROR_FLAG = 'RECON::SET_FATAL_API_ERROR_FLAG';

// Notifications
export const NOTIFICATION_INIT_SUCCESS = 'RECON::NOTIFICATION_INIT_SUCCESS';
export const NOTIFICATION_TEARDOWN_SUCCESS = 'RECON::NOTIFICATION_TEARDOWN_SUCCESS';
