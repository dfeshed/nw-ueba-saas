// *******
// BEGIN - Should be moved with Download Manager
// *******
import { test, module } from 'qunit';
import reducer from 'investigate-events/reducers/investigate/files/reducer';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import Immutable from 'seamless-immutable';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';

module('Unit | Reducers | Files | Investigate');

const initialState = Immutable.from({
  fileExtractStatus: null,
  fileExtractJobId: null,
  fileExtractLink: null,
  isAutoDownloadFile: true
});

test('test INITIALIZE changes fileExtractStatus to queue when wait', function(assert) {
  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
    payload: {
      fileExtractStatus: 'wait'
    }
  };
  const currentState = initialState.merge({
    fileExtractStatus: 'wait'
  });
  const result = reducer(currentState, action);
  assert.equal(result.fileExtractStatus, 'queued');
});

test('test SET_PREFERENCES', function(assert) {
  const action = {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: {
      eventAnalysisPreferences: {
        autoDownloadExtractedFiles: false
      }
    }
  };

  const result = reducer(initialState, action);
  assert.equal(result.isAutoDownloadFile, false);
});

test('test SET_PREFERENCES when auto download preference is not persisted yet', function(assert) {
  const action = {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: {}
  };
  const result = reducer(initialState, action);
  assert.equal(result.isAutoDownloadFile, true);
});

test('test REHYDRATE', function(assert) {
  const action = {
    type: ACTION_TYPES.REHYDRATE,
    payload: {
      investigate: {
        files: {
          isAutoDownloadFile: false
        }
      }
    }
  };
  const result = reducer(initialState, action);
  assert.equal(result.isAutoDownloadFile, false);
});

test('FILE_EXTRACT_JOB_ID_RETRIEVE updates state with init when initiated', function(assert) {

  const startAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE
  });
  const result = reducer(initialState, startAction);

  assert.equal(result.fileExtractStatus, 'init', 'extraction in progress');
});

test('FILE_EXTRACT_JOB_ID_RETRIEVE updates state with a jobId on success', function(assert) {

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE,
    payload: {
      data: {
        jobId: 2
      }
    }
  });
  const result = reducer(initialState, successAction);

  assert.equal(result.fileExtractStatus, 'wait', 'extraction status changed to wait');
  assert.equal(result.fileExtractJobId, 2, 'jobid saved to state');
});

test('FILE_EXTRACT_JOB_ID_RETRIEVE updates state with error object on failure', function(assert) {
  const failureAction = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE,
    payload: {
      error: {}
    }
  });
  const result = reducer(initialState, failureAction);

  assert.equal(result.fileExtractStatus, 'error', 'extraction status changed to error on failure');
});

test('test NOTIFICATION_TEARDOWN_SUCCESS changes fileExtractStatus to queue when wait', function(assert) {
  const action = {
    type: ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS
  };

  const currentState = initialState.merge({
    fileExtractStatus: 'wait'
  });
  const result = reducer(currentState, action);
  assert.equal(result.fileExtractStatus, 'queued');
});

test('test FILE_EXTRACT_NOTIFIED changes fileExtractStatus to notified when queued', function(assert) {
  const action = {
    type: ACTION_TYPES.FILE_EXTRACT_NOTIFIED
  };

  const currentState = initialState.merge({
    fileExtractStatus: 'queued'
  });
  const result = reducer(currentState, action);
  assert.equal(result.fileExtractStatus, 'notified');
});

test('test FILE_EXTRACT_FAILURE', function(assert) {
  const action = {
    type: ACTION_TYPES.FILE_EXTRACT_FAILURE
  };

  const currentState = initialState.merge({
    fileExtractStatus: 'wait'
  });
  const result = reducer(currentState, action);
  assert.equal(result.fileExtractStatus, 'error');
});

// *******
// END - Should be moved with Download Manager
// *******
