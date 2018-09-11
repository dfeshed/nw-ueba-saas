import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/files/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';
import { filesData } from '../../../state/state';

module('Unit | Reducers | files');

const initialState = Immutable.from({
  files: [],
  selectedFileId: null,
  pageNumber: -1,
  totalItems: 0,
  sortField: 'fileName',
  isDescOrder: false,
  filesLoadingStatus: 'wait',
  filesLoadMoreStatus: 'stopped',
  selectedFileList: [],
  fileStatusData: {}
});

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});

test('The RESET_HOST_DETAILS will reset the state', function(assert) {
  const previous = Immutable.from({
    files: [{ fileName: 'test' }],
    pageNumber: 0,
    totalItems: 1
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_HOST_DETAILS });

  assert.deepEqual(result, initialState);
});

test('The SET_HOST_FILES_SORT_BY action will sets the sort by field', function(assert) {
  const previous = Immutable.from({
    sortField: 'fileName',
    filesLoadingStatus: 'wait',
    isDescOrder: false
  });
  const result = reducer(
    previous,
    { type: ACTION_TYPES.SET_HOST_FILES_SORT_BY, payload: { sortOption: { isDescOrder: true, sortField: 'size' } } }
  );
  assert.deepEqual(result, { sortField: 'size', filesLoadingStatus: 'sorting', isDescOrder: true });
});

test('The INCREMENT_PAGE_NUMBER will increment page number sets the state', function(assert) {
  const previous = Immutable.from({
    pageNumber: 0
  });
  const result = reducer(previous, { type: ACTION_TYPES.INCREMENT_PAGE_NUMBER });
  assert.equal(result.pageNumber, 1);
});

test('The RESET_HOST_FILES will reset the state', function(assert) {
  const previous = Immutable.from({
    files: [ { name: 'test' }],
    pageNumber: 1
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_HOST_FILES });
  assert.deepEqual(result, { files: [], pageNumber: -1 });
});
test('The SET_SELECTED_FILE will sets the selected file id the state', function(assert) {
  const previous = Immutable.from({
    selectedFileId: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_FILE, payload: { id: 'aaazza234aa2123' } });
  assert.equal(result.selectedFileId, 'aaazza234aa2123');
});

test('The GET_HOST_FILES sets normalized server response to state', function(assert) {
  const previous = Immutable.from({
    files: [],
    selectedFileId: null,
    pageNumber: -1,
    totalItems: 0,
    filesLoadMoreStatus: null
  });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_HOST_FILES });
  const startEndState = reducer(previous, startAction);
  assert.deepEqual(startEndState.filesLoadMoreStatus, 'streaming');

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_HOST_FILES,
    payload: { data: filesData }
  });
  const endState = reducer(previous, action);

  assert.equal(endState.files.length, 3);
  assert.equal(endState.pageNumber, 10);

  const data = { ...filesData, hasNext: false };

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_HOST_FILES,
    payload: { data }
  });

  const newEndState = reducer(previous, newAction);
  assert.equal(newEndState.filesLoadMoreStatus, 'completed');

  const failureAction = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.GET_HOST_FILES });
  const failureState = reducer(previous, failureAction);
  assert.deepEqual(failureState.filesLoadMoreStatus, 'error');
});

test('The TOGGLE_SELECTED_FILE  the state', function(assert) {
  const previous = Immutable.from({
    files: [ { id: 'test' }],
    pageNumber: 1,
    selectedFileList: []
  });

  const expectedSelectedList = [{ id: 'aaazza234aa2123', checksumSha256: undefined, signature: undefined, size: undefined }];
  let result = reducer(previous, { type: ACTION_TYPES.TOGGLE_SELECTED_FILE, payload: { id: 'aaazza234aa2123' } });
  assert.deepEqual(result, { files: [{ id: 'test' }], pageNumber: 1, selectedFileList: expectedSelectedList, fileStatusData: {} });

  const newExpected = [{ id: 'test', checksumSha256: undefined, signature: undefined, size: undefined }];
  result = reducer(previous, { type: ACTION_TYPES.TOGGLE_SELECTED_FILE, payload: { id: 'test' } });
  assert.deepEqual(result, { files: [{ id: 'test' }], pageNumber: 1, selectedFileList: newExpected, fileStatusData: {} });
});

test('The SELECT_ALL_FILES in the state', function(assert) {
  const previous = Immutable.from({
    files: [ { id: 'test1' }, { id: 'test2' }],
    pageNumber: 1,
    selectedFileList: []
  });

  const expectedSelectedList = [
    { id: 'test1', checksumSha256: undefined },
    { id: 'test2', checksumSha256: undefined }
  ];
  const result = reducer(previous, { type: ACTION_TYPES.SELECT_ALL_FILES });
  assert.deepEqual(result, { files: [{ id: 'test1' }, { id: 'test2' }], pageNumber: 1, selectedFileList: expectedSelectedList });
});
test('The DESELECT_ALL_FILES in the state', function(assert) {
  const previous = Immutable.from({
    files: [ { id: 'test1' }, { id: 'test2' }],
    pageNumber: 1,
    selectedFileList: [{ id: 'test1' }, { id: 'test2' }]
  });
  const result = reducer(previous, { type: ACTION_TYPES.DESELECT_ALL_FILES });
  assert.deepEqual(result, { files: [{ id: 'test1' }, { id: 'test2' }], pageNumber: 1, selectedFileList: [] });
});

test('The GET_FILE_STATUS set server response to state', function(assert) {
  const previous = Immutable.from({
    fileStatusData: {}
  });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_FILE_STATUS });
  const startEndState = reducer(previous, startAction);

  assert.deepEqual(startEndState.fileStatusData, {});

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_FILE_STATUS,
    payload: { data: [ { resultList: [ { data: 'Whitelist' } ] } ] }
  });
  const endState = reducer(previous, action);
  assert.equal(endState.fileStatusData, 'Whitelist');
});