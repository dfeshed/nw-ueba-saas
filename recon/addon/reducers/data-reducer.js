import Ember from 'ember';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import { RECON_VIEW_TYPES_BY_NAME } from '../utils/reconstruction-types';
import * as ACTION_TYPES from '../actions/types';

const { set } = Ember;

// State of server jobs for downloading file(s)
const fileExtractInitialState = {
  fileExtractStatus: null,  // either 'init' (creating job), 'wait' (job executing), 'success' or 'error'
  fileExtractError: null,   // error object
  fileExtractJobId: null,   // job id for tracking notifications
  fileExtractLink: null     // url for downloading successful job's results
};

const dataInitialState = {
  // view defaults to packet
  currentReconView: RECON_VIEW_TYPES_BY_NAME.PACKET,

  // Recon inputs
  endpointId: null,
  eventId: null,
  total: null,
  index: null,

  // Recon inputs or fetched if not provided
  aliases: null,
  language: null,

  // Fetched data
  headerItems: null,
  headerLoading: null,
  files: null,

  ...fileExtractInitialState,

  // Linked files are not extracted like normal files.
  // Rather, they are essentially shortcuts to another event query.
  // When the user clicks on a linked file, recon invokes a configurable callback
  // that is responsible for handling it (e.g., launching a new query).
  linkToFileAction: null,

  // callback for stopping notifications
  // (obtained at run-time as a result from notifications initialization)
  stopNotifications: null,

  // Error state
  headerError: null,
  contentError: null,

  // loading states
  contentLoading: false
};

const allFilesSelection = (setTo) => {
  return (state) => ({
    ...state,
    files: state.files.map((f) => ({
      ...f,
      // linked files cannot be selected for extraction
      selected: (f.type === 'link') ? false : setTo
    }))
  });
};

const data = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload }) => ({
    ...dataInitialState,
    currentReconView: state.currentReconView,
    stopNotifications: state.stopNotifications,
    ...payload
  }),

  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state, { payload: { newView } }) => ({
    ...state,
    currentReconView: newView
  }),

  // Summary reducing
  [ACTION_TYPES.SUMMARY_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, headerItems: null, headerError: null, headerLoading: true }),
      finish: (s) => ({ ...s, headerLoading: false }),
      failure: (s) => ({ ...s, headerError: true }),
      success: (s) => ({ ...s, headerItems: action.payload.headerItems })
    });
  },

  // Content reducing
  [ACTION_TYPES.CONTENT_RETRIEVE_STARTED]: (state) => ({
    ...state,
    contentError: null,
    contentLoading: true
  }),
  [ACTION_TYPES.FILES_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    files: payload.map((f) => {
      set(f, 'selected', false);
      return f;
    }),
    contentLoading: false
  }),
  [ACTION_TYPES.CONTENT_RETRIEVE_FAILURE]: (state, { payload }) => ({
    ...state,
    contentError: payload,
    contentLoading: false
  }),

  // TODO: future DRY possibility here
  [ACTION_TYPES.TEXT_DECODE_PAGE]: (state) => ({
    ...state,
    contentLoading: false
  }),
  [ACTION_TYPES.PACKETS_RETRIEVE_PAGE]: (state) => ({
    ...state,
    contentLoading: false
  }),

  // Download reducing
  [ACTION_TYPES.FILES_FILE_TOGGLED]: (state, { payload: fileId }) => {
    const newFiles = state.files.map((f) => {
      if (f.id === fileId) {
        return {
          ...f,
          selected: !f.selected
        };
      } else {
        return f;
      }
    });

    return {
      ...state,
      files: newFiles
    };
  },
  [ACTION_TYPES.FILES_DESELECT_ALL]: allFilesSelection(false),
  [ACTION_TYPES.FILES_SELECT_ALL]: allFilesSelection(true),
  [ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, ...fileExtractInitialState, fileExtractStatus: 'init' }),
      failure: (s) => ({ ...s, fileExtractStatus: 'error', fileExtractError: action.payload }),
      success: (s) => ({ ...s, fileExtractStatus: 'wait', fileExtractJobId: action.payload.data.jobId })
    });
  },
  [ACTION_TYPES.FILE_EXTRACT_JOB_SUCCESS]: (state, { payload }) => ({
    ...state,
    fileExtractStatus: 'success',
    fileExtractLink: payload.link
  }),
  [ACTION_TYPES.FILE_EXTRACT_JOB_DOWNLOADED]: (state) => ({
    ...state,
    ...fileExtractInitialState
  }),

  // Notifications
  [ACTION_TYPES.NOTIFICATION_INIT_SUCCESS]: (state, { payload }) => ({
    ...state,
    stopNotifications: payload.cancelFn
  }),
  [ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS]: (state) => ({
    // clear the callback that tears down notifications, and
    // clear any pending/completed file extraction state
    ...state,
    ...fileExtractInitialState,
    stopNotifications: null
  })
}, dataInitialState);

export default data;
