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

// Meta action types
export const META_RETRIEVE = 'RECON::META_RETRIEVE';

// Packet action types
export const TOGGLE_BYTE_STYLING = 'RECON::TOGGLE_BYTE_STYLING';
export const TOGGLE_PACKET_PAYLOAD_ONLY = 'RECON::TOGGLE_PACKET_PAYLOAD_ONLY';
export const SHOW_PACKET_TOOLTIP = 'RECON::SHOW_PACKET_TOOLTIP';
export const HIDE_PACKET_TOOLTIP = 'RECON::HIDE_PACKET_TOOLTIP';
export const PACKETS_RETRIEVE_PAGE = 'RECON::PACKETS_RETRIEVE_PAGE';

// Text action types
export const TEXT_DECODE_PAGE = 'RECON::TEXT_DECODE_PAGE';
export const TEXT_HIGHLIGHT_META = 'RECON::TEXT_HIGHLIGHT_META';
export const TOGGLE_TEXT_DECODE = 'RECON::TOGGLE_TEXT_DECODE';

// Dictionary action types
export const LANGUAGE_RETRIEVE = 'RECON::LANGUAGE_RETRIEVE';
export const ALIASES_RETRIEVE = 'RECON::ALIASES_RETRIEVE';

// Data action types
export const SUMMARY_RETRIEVE = 'RECON::SUMMARY_RETRIEVE';
export const CONTENT_RETRIEVE_STARTED = 'RECON::CONTENT_RETRIEVE_STARTED';
export const FILES_RETRIEVE_SUCCESS = 'RECON::FILES_RETRIEVE_SUCCESS';
export const CONTENT_RETRIEVE_FAILURE = 'RECON::CONTENT_RETRIEVE_FAILURE';

// User interaction
export const CHANGE_RECON_VIEW = 'RECON::CHANGE_RECON_VIEW';
export const FILES_SELECT_ALL = 'RECON::FILES_SELECT_ALL';
export const FILES_DESELECT_ALL = 'RECON::FILES_DESELECT_ALL';
export const FILES_FILE_TOGGLED = 'RECON::FILES_FILE_TOGGLED';
export const FILE_EXTRACT_JOB_ID_RETRIEVE = 'RECON::FILE_EXTRACT_JOB_ID_RETRIEVE';
export const FILE_EXTRACT_JOB_SUCCESS = 'RECON::FILE_EXTRACT_JOB_SUCCESS';
export const FILE_EXTRACT_JOB_DOWNLOADED = 'RECON::FILE_EXTRACT_JOB_DOWNLOADED';
export const NOTIFICATION_INIT_SUCCESS = 'RECON::NOTIFICATION_INIT_SUCCESS';
export const NOTIFICATION_TEARDOWN_SUCCESS = 'RECON::NOTIFICATION_TEARDOWN_SUCCESS';
