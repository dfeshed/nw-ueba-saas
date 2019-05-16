import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'recon/actions/types';
import _ from 'lodash';

// State of server jobs for downloading file(s)
const fileExtractInitialState = {
  fileExtractStatus: null, // either 'init' (creating job), 'wait' (job executing), 'success' or 'error'
  fileExtractJobId: null, // job id for tracking notifications (NOT CURRENTLY USED)
  fileExtractLink: null // url for downloading successful job's results (NOT CURRENTLY USED)
};

const filesInitialState = Immutable.from({
  files: null,
  selectedFileIds: [],
  ...fileExtractInitialState,

  // Linked files are not extracted like normal files.
  // Rather, they are essentially shortcuts to another event query.
  // When the user clicks on a linked file, recon invokes a configurable callback
  // that is responsible for handling it (e.g., launching a new query).
  linkToFileAction: null,
  isAutoDownloadFile: true
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
const _fileExtractEnqueueState = (fileExtractStatus) => fileExtractStatus === 'wait' || fileExtractStatus === 'queued' ? 'queued' : null;

const _fileExtractQueueUpdateState = (fileExtractStatus) => fileExtractStatus === 'queued' ? 'notified' : fileExtractStatus;

const filesReducer = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload: { linkToFileAction } }) => {
    return filesInitialState.merge({
      ..._fileExtractEnqueueState(state.fileExtractStatus),
      linkToFileAction,
      // maintain the state of auto-download preference since it'd have been rehydrated before initialize
      isAutoDownloadFile: state.isAutoDownloadFile
    });
  },

  [ACTION_TYPES.SET_PREFERENCES]: (state, { payload: { eventAnalysisPreferences } }) => {
    const isAutoDownloadFile = _.get(eventAnalysisPreferences, 'autoDownloadExtractedFiles', state.isAutoDownloadFile);
    return state.merge({ isAutoDownloadFile });
  },

  [ACTION_TYPES.RESET_PREFERENCES]: (state, { payload: { eventAnalysisPreferences } }) => {
    const isAutoDownloadFile = _.get(eventAnalysisPreferences, 'autoDownloadExtractedFiles', state.isAutoDownloadFile);
    return state.merge({ isAutoDownloadFile });
  },

  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    const isAutoDownloadFile = _.get(payload, 'recon.files.isAutoDownloadFile', state.isAutoDownloadFile);
    let fileExtractStatus = _.get(payload, 'recon.files.fileExtractStatus', state.fileExtractStatus);
    fileExtractStatus = _fileExtractEnqueueState(fileExtractStatus);
    return state.merge({ isAutoDownloadFile, fileExtractStatus });
  },

  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state) => {
    return state.merge({ 'fileExtractStatus': _fileExtractEnqueueState(state.fileExtractStatus) });
  },

  [ACTION_TYPES.FILES_RETRIEVE_SUCCESS]: (state, { payload }) => {
    const filteredPayload = payload.map((file) => {
      if (file.query) {
        // This regex removes any `\` before equals `=` operator
        // ex: action\=foo -> action=foo
        file.query = file.query.replace(/\\=/g, '=');
      }
      return file;
    });
    return state.set('files', filteredPayload);
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
      failure: (s) => s.merge({ fileExtractStatus: 'error' }),
      success: (s) => s.merge({ fileExtractStatus: 'wait', fileExtractJobId: action.payload.data.jobId })
    });
  },

  [ACTION_TYPES.FILE_EXTRACT_JOB_SUCCESS]: (state, { payload }) => {
    return state.merge({ fileExtractStatus: 'success', fileExtractLink: payload.link });
  },

  [ACTION_TYPES.FILE_EXTRACT_FAILURE]: (state) => {
    return state.set('fileExtractStatus', 'error');
  },

  [ACTION_TYPES.FILE_EXTRACT_JOB_DOWNLOADED]: (state) => {
    return state.merge(fileExtractInitialState);
  },

  [ACTION_TYPES.FILE_EXTRACT_NOTIFIED]: (state) => {
    return state.merge({ 'fileExtractStatus': _fileExtractQueueUpdateState(state.fileExtractStatus) });
  },

  [ACTION_TYPES.CLOSE_RECON]: (state) => {
    return state.merge({ 'fileExtractStatus': _fileExtractEnqueueState(state.fileExtractStatus) });
  },
  // Files-based notifcation handling
  [ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS]: (state) => {
    return state.merge({ 'fileExtractStatus': _fileExtractEnqueueState(state.fileExtractStatus) });
  }
}, filesInitialState);

export default filesReducer;
