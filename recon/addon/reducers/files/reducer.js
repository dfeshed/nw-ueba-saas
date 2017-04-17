import Ember from 'ember';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'recon/actions/types';

const { set } = Ember;

// State of server jobs for downloading file(s)
const fileExtractInitialState = {
  fileExtractStatus: null,  // either 'init' (creating job), 'wait' (job executing), 'success' or 'error'
  fileExtractError: null,   // error object
  fileExtractJobId: null,   // job id for tracking notifications (NOT CURRENTLY USED)
  fileExtractLink: null     // url for downloading successful job's results (NOT CURRENTLY USED)
};

const filesInitialState = {
  files: null,
  ...fileExtractInitialState,

  // Linked files are not extracted like normal files.
  // Rather, they are essentially shortcuts to another event query.
  // When the user clicks on a linked file, recon invokes a configurable callback
  // that is responsible for handling it (e.g., launching a new query).
  linkToFileAction: null
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

const filesReducer = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload }) => ({
    ...filesInitialState,
    linkToFileAction: payload.linkToFileAction
  }),

  [ACTION_TYPES.FILES_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    files: payload.map((f) => {
      set(f, 'selected', false);
      return f;
    })
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

  // Files-based notifcation handling,
  // clear any pending/completed file extraction state
  [ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS]: (state) => ({
    ...state,
    ...fileExtractInitialState
  })
}, filesInitialState);

export default filesReducer;
