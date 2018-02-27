import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/file-list/reducer';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Reducers | investigate-files');

const FILE_LIST = [
  {
    'firstFileName': 'xt_conntrack.ko',
    'format': 'ELF'
  },
  {
    'firstFileName': 'svchost.dll',
    'format': 'PE'
  },
  {
    'firstFileName': 'explorer.dll',
    'format': 'PE'
  }
];

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    files: [],
    areFilesLoading: 'wait',
    loadMoreStatus: 'stopped',
    pageNumber: -1,
    totalItems: 0,
    hasNext: false,
    sortField: 'firstSeenTime',
    isSortDescending: true,
    downloadStatus: 'completed',
    downloadId: null,
    listOfServices: null
  });
});

test('The RESET_DOWNLOAD_ID action reset the export link', function(assert) {
  const previous = Immutable.from({
    downloadId: '123'
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_DOWNLOAD_ID });
  assert.equal(result.downloadId, null);
});

test('RESET_FILES action reset files and page number', function(assert) {
  const previous = Immutable.from({
    files: [{ firstFileName: 'test.dll' }],
    pageNumber: 0
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_FILES });
  assert.equal(result.files.length, 0);
  assert.equal(result.pageNumber, -1);
});

test('INCREMENT_PAGE_NUMBER action should increment page number', function(assert) {
  const previous = Immutable.from({
    pageNumber: 0
  });
  const result = reducer(previous, { type: ACTION_TYPES.INCREMENT_PAGE_NUMBER });
  assert.equal(result.pageNumber, 1);
});

test('The SET_SORT_BY will set the selected sort to state', function(assert) {
  const previous = Immutable.from({
    sortField: 'name',
    areFilesLoading: 'sorting',
    isSortDescending: false
  });
  const result = reducer(previous, {
    type: ACTION_TYPES.SET_SORT_BY,
    payload: { sortField: 'id', isSortDescending: false }
  });
  assert.equal(result.sortField, 'id');
});

test('The DOWNLOAD_FILE_AS_CSV action will set the download id to state', function(assert) {
  const previous = Immutable.from({
    downloadId: null,
    downloadStatus: 'completed'
  });
  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.DOWNLOAD_FILE_AS_CSV });
  const endState = reducer(previous, startAction);

  assert.equal(endState.downloadStatus, 'streaming');

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.DOWNLOAD_FILE_AS_CSV,
    payload: { data: { id: 111 } }
  });
  const newEndState = reducer(previous, successAction);
  assert.equal(newEndState.downloadStatus, 'completed');
  assert.equal(newEndState.downloadId, 111);
});

test('FETCH_NEXT_FILES failure will set ', function(assert) {
  const previous = Immutable.from({
    files: FILE_LIST,
    loadMoreStatus: null
  });
  const errorAction = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.FETCH_NEXT_FILES
  });
  const newEndState = reducer(previous, errorAction);
  assert.equal(newEndState.loadMoreStatus, 'error');
});

test('The FETCH_NEXT_FILES will append the paged response to state', function(assert) {
  const previous = Immutable.from({
    files: FILE_LIST,
    loadMoreStatus: null
  });
  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_NEXT_FILES });
  const endState = reducer(previous, startAction);

  assert.equal(endState.loadMoreStatus, 'streaming');

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_NEXT_FILES,
    payload: { data: { hasNext: false, items: [{ id: 11 }] } }
  });
  const newEndState = reducer(previous, successAction);

  assert.equal(newEndState.loadMoreStatus, 'completed');
  assert.equal(newEndState.files.length, 4);
});

test('The FETCH_NEXT_FILES sets load more state properly', function(assert) {
  const previous = Immutable.from({
    files: FILE_LIST,
    loadMoreStatus: null
  });
  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_NEXT_FILES });
  const endState = reducer(previous, startAction);

  assert.equal(endState.loadMoreStatus, 'streaming');

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_NEXT_FILES,
    payload: { data: { hasNext: true, items: [{ id: 11 }] } }
  });
  const newEndState = reducer(previous, successAction);

  assert.equal(newEndState.loadMoreStatus, 'stopped', 'load more status is stopped');
  assert.equal(newEndState.files.length, 4);

  const successAction1 = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_NEXT_FILES,
    payload: { data: { hasNext: false, items: [{ id: 11 }] } }
  });
  const newEndState1 = reducer(previous, successAction1);

  assert.equal(newEndState1.loadMoreStatus, 'completed', 'load more status is completed');
});

test('The FETCH_NEXT_FILES sets load more state properly when totalItems > 1000', function(assert) {
  const previous = Immutable.from({
    files: FILE_LIST,
    loadMoreStatus: null
  });
  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_NEXT_FILES });
  const endState = reducer(previous, startAction);

  assert.equal(endState.loadMoreStatus, 'streaming');

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_NEXT_FILES,
    payload: { data: { hasNext: true, items: [{ id: 11 }], totalItems: 1500 } }
  });
  const newEndState = reducer(previous, successAction);

  assert.equal(newEndState.loadMoreStatus, 'stopped', 'load more status is stopped');
  assert.equal(newEndState.files.length, 4);

  const successAction1 = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_NEXT_FILES,
    payload: { data: { hasNext: false, items: [{ id: 11 }], totalItems: 1500 } }
  });
  const newEndState1 = reducer(previous, successAction1);

  assert.equal(newEndState1.loadMoreStatus, 'stopped', 'load more status is stopped');
});

test('The GET_LIST_OF_SERVICES will set listOfServices', function(assert) {
  // Initial state
  const initialResult = reducer(undefined, {});
  assert.equal(initialResult.listOfServices, null, 'original listOfServices value');

  const response = [{ name: 'broker' }, { name: 'concentrator' }, { name: 'decoder' }];

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_LIST_OF_SERVICES,
    payload: { data: response }
  });

  const result = reducer(initialResult, newAction);

  assert.deepEqual(result.listOfServices, [{ name: 'broker' }, { name: 'concentrator' }, { name: 'decoder' }], 'listOfServices value is set');
});
