// *******
// BEGIN - Copy/pasted download code from Recon
// *******
import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'investigate-events/actions/types';

import _ from 'lodash';

// State of server jobs for downloading file(s)
const fileExtractInitialState = {
  fileExtractStatus: null, // either 'init' (creating job), 'wait' (job executing), 'success' or 'error'
  fileExtractJobId: null, // job id for tracking notifications
  fileExtractLink: null // url for downloading successful job's results
};

const filesInitialState = Immutable.from({
  ...fileExtractInitialState,
  isAutoDownloadFile: true
});


/**
 * Compute the status of file extraction (download). If we're in the middle of a
 * download and the user navigates away from Event Search (for example, goes to Hosts tab),
 * we want to change the extraction status to 'queued'. This will notify the UI that the
 * download will continue in the job queue.
 * @param {Object} state The current state of Investigate
 * @private
 */
const _fileExtractEnqueueState = (fileExtractStatus) => fileExtractStatus === 'wait' || fileExtractStatus === 'queued' ? 'queued' : null;

export default handleActions({

  [ACTION_TYPES.INITIALIZE_INVESTIGATE]: (state) => {
    return state.merge({ 'fileExtractStatus': _fileExtractEnqueueState(state.fileExtractStatus) });
  },

  [ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('fileExtractStatus', 'init'),
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

  [ACTION_TYPES.SET_PREFERENCES]: (state, { payload: { eventAnalysisPreferences } }) => {
    const isAutoDownloadFile = _.get(eventAnalysisPreferences, 'autoDownloadExtractedFiles', state.isAutoDownloadFile);
    return state.merge({ isAutoDownloadFile });
  },

  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    const isAutoDownloadFile = _.get(payload, 'investigate.files.isAutoDownloadFile', state.isAutoDownloadFile);
    let fileExtractStatus = _.get(payload, 'investigate.files.fileExtractStatus', state.fileExtractStatus);
    fileExtractStatus = _fileExtractEnqueueState(fileExtractStatus);
    return state.merge({ isAutoDownloadFile, fileExtractStatus });
  },

  // Files-based notifcation handling
  [ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS]: (state) => {
    return state.merge({ 'fileExtractStatus': _fileExtractEnqueueState(state.fileExtractStatus) });
  }
}, filesInitialState);
// *******
// END - Copy/pasted download code from Recon
// *******
