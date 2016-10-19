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
export const META_RETRIEVE_SUCCESS = 'RECON::META_RETRIEVE_SUCCESS';
export const META_RETRIEVE_FAILURE = 'RECON::META_RETRIEVE_FAILURE';

// Summary action
export const SUMMARY_RETRIEVE_STARTED = 'RECON::SUMMARY_RETRIEVE_STARTED';
export const SUMMARY_RETRIEVE_SUCCESS = 'RECON::SUMMARY_RETRIEVE_SUCCESS';
export const SUMMARY_RETRIEVE_FAILURE = 'RECON::SUMMARY_RETRIEVE_FAILURE';

// Content action types
export const FILES_RETRIEVE_SUCCESS = 'RECON::FILES_RETRIEVE_SUCCESS';
export const PACKETS_RETRIEVE_PAGE = 'RECON::PACKETS_RETRIEVE_PAGE';
export const CONTENT_RETRIEVE_FAILURE = 'RECON::CONTENT_RETRIEVE_FAILURE';

// In progress action types
export const CONTENT_RETRIEVE_STARTED = 'RECON::CONTENT_RETRIEVE_STARTED';
export const META_RETRIEVE_STARTED = 'RECON::META_RETRIEVE_STARTED';

// User interaction
export const FILES_FILE_TOGGLED = 'RECON::FILES_FILE_TOGGLED';
export const FILE_DOWNLOAD_SUCCESS = 'RECON::FILE_DOWNLOAD_SUCCESS';
