import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'recon/actions/types';

// State of server jobs for downloading file(s)
const fileExtractInitialState = {
  fileExtractStatus: null,  // either 'init' (creating job), 'wait' (job executing), 'success' or 'error'
  fileExtractError: null,   // error object
  fileExtractJobId: null,   // job id for tracking notifications (NOT CURRENTLY USED)
  fileExtractLink: null     // url for downloading successful job's results (NOT CURRENTLY USED)
};

const filesInitialState = Immutable.from({
  files: null,
  selectedFileIds: [],
  ...fileExtractInitialState,

  // Linked files are not extracted like normal files.
  // Rather, they are essentially shortcuts to another event query.
  // When the user clicks on a linked file, recon invokes a configurable callback
  // that is responsible for handling it (e.g., launching a new query).
  linkToFileAction: null
});

/**
 * Compute the status of file extraction (download). If we're in the middle of a
 * download and something happens to change the state of Recon (view change,
 * recon panel closed or opened to a new event), we want to change the
 * extraction status to 'queued'. This will notify the UI that the download will
 * continue in the job queue.
 * @param {Object} state The current state of Recon
 * @private
 */
const _fileExtractState = (state) => ({
  fileExtractStatus: (state.fileExtractStatus === 'wait' ||
                      state.fileExtractStatus === 'queued') ? 'queued' : null
});

const filesReducer = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload: { linkToFileAction } }) => {
    return filesInitialState.merge({ ..._fileExtractState(state), linkToFileAction });
  },

  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state) => {
    return state.merge(_fileExtractState(state));
  },

  [ACTION_TYPES.FILES_RETRIEVE_SUCCESS]: (state, { payload }) => {
    return state.set('files', payload);
  },

  [ACTION_TYPES.FILES_FILE_TOGGLED]: (state, { payload: fileId }) => {
    let selectedFileIds = [];
    if (state.selectedFileIds.includes(fileId)) {
      selectedFileIds = state.selectedFileIds.filter((id) => id !== fileId);
    } else {
      selectedFileIds = [...state.selectedFileIds, fileId];
    }
    return state.merge({ selectedFileIds });
  },

  [ACTION_TYPES.FILES_DESELECT_ALL]: (state) => {
    return state.set('selectedFileIds', []);
  },

  [ACTION_TYPES.FILES_SELECT_ALL]: (state) => {
    return state.set('selectedFileIds', (state.files || []).map(({ id }) => id));
  },

  [ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ ...fileExtractInitialState, fileExtractStatus: 'init' }),
      failure: (s) => s.merge({ fileExtractStatus: 'error', fileExtractError: action.payload }),
      success: (s) => s.merge({ fileExtractStatus: 'wait', fileExtractJobId: action.payload.data.jobId })
    });
  },

  [ACTION_TYPES.FILE_EXTRACT_JOB_SUCCESS]: (state, { payload }) => {
    return state.merge({ fileExtractStatus: 'success', fileExtractLink: payload.link });
  },

  [ACTION_TYPES.FILE_EXTRACT_JOB_DOWNLOADED]: (state) => {
    return state.merge(fileExtractInitialState);
  },

  // Files-based notifcation handling
  [ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS]: (state) => {
    return state.merge(_fileExtractState(state));
  }
}, filesInitialState);

export default filesReducer;
