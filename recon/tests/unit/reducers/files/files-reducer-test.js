import { test, module } from 'qunit';
import reducer from 'recon/reducers/files/reducer';
import * as ACTION_TYPES from 'recon/actions/types';
import Immutable from 'seamless-immutable';

module('Unit | Reducers | Files | Recon');

const initialState = Immutable.from({
  files: null,
  fileExtractStatus: null,
  fileExtractError: null,
  fileExtractJobId: null,
  fileExtractLink: null,
  selectedFileIds: [],
  linkToFileAction: null,
  isAutoDownloadFile: true
});

test('test INITIALIZE', function(assert) {
  const action = {
    type: ACTION_TYPES.INITIALIZE,
    payload: {
      fileExtractLink: 'random-link-1',
      fileExtractStatus: 'wait',
      linkToFileAction: 'link-to-file'
    }
  };
  const currentState = initialState.merge({
    isAutoDownloadFile: false,
    linkToFileAction: null,
    fileExtractLink: 'random-link-2'
  });
  const result = reducer(currentState, action);
  assert.equal(result.isAutoDownloadFile, false);
  assert.equal(result.fileExtractLink, null);
  assert.equal(result.linkToFileAction, 'link-to-file');
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

test('test RESET_PREFERENCES', function(assert) {
  const action = {
    type: ACTION_TYPES.RESET_PREFERENCES,
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

test('test RESET_PREFERENCES when auto download preference is not persisted yet', function(assert) {
  const action = {
    type: ACTION_TYPES.RESET_PREFERENCES,
    payload: {}
  };
  const result = reducer(initialState, action);
  assert.equal(result.isAutoDownloadFile, true);
});

test('test REHYDRATE', function(assert) {
  const action = {
    type: ACTION_TYPES.REHYDRATE,
    payload: {
      recon: {
        files: {
          isAutoDownloadFile: false
        }
      }
    }
  };
  const result = reducer(initialState, action);
  assert.equal(result.isAutoDownloadFile, false);
});

test('test CLOSE_RECON', function(assert) {
  const currentState = initialState.merge({
    fileExtractStatus: 'wait'
  });
  const action = {
    type: ACTION_TYPES.CLOSE_RECON
  };
  const result = reducer(currentState, action);
  assert.equal(result.fileExtractStatus, 'queued');
});

test('test FILES_RETRIEVE_SUCCESS removes backslash before operators for a given query filter', function(assert) {
  const action = {
    type: ACTION_TYPES.FILES_RETRIEVE_SUCCESS,
    payload: [
      { type: 'foo',
        fileName: 'bar',
        query: 'ip.src\=10.20.57.214&&tcp.dstport\=57337' // eslint-disable-line no-useless-escape
      }
    ]
  };

  const result = reducer(initialState, action);
  assert.equal(result.files[0].query, 'ip.src=10.20.57.214&&tcp.dstport=57337', 'Should only remove backslash if there is one before operator');
});
