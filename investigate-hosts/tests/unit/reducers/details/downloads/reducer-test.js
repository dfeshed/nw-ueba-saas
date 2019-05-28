import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/downloads/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';

const initialState = {
  files: {},
  loadMoreStatus: 'completed',
  selectedIndex: -1,
  totalItems: 4,
  sortField: 'downloadedTime',
  isSortDescending: true,
  selectedFileList: [],
  selectedFile: {},
  pageNumber: -1
};

module('Unit | Reducers | downloads', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });


  test('The SET_SELECTED_DOWNLOADED_FILE_INDEX will reset the index', function(assert) {
    const previous = Immutable.from({
      selectedIndex: 1
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_DOWNLOADED_FILE_INDEX, payload: 3 });

    assert.equal(result.selectedIndex, 3);
  });

  test('The RESET_DOWNLOADED_FILES will reset the state', function(assert) {
    const previous = Immutable.from({
      selectedIndex: 3
    });
    const result = reducer(previous, { type: ACTION_TYPES.RESET_DOWNLOADED_FILES });

    assert.equal(result.selectedIndex, -1);
  });

  test('The DESELECT_ALL_DOWNLOADED_FILES will remove all selected files', function(assert) {
    const previous = Immutable.from({
      selectedFileList: [{ id: 1 }, { id: 2 }, { id: 3 }]
    });
    const result = reducer(previous, { type: ACTION_TYPES.DESELECT_ALL_DOWNLOADED_FILES });

    assert.deepEqual(result.selectedFileList, []);
  });

  test('The SELECT_ALL_DOWNLOADED_FILES will selecte all files', function(assert) {
    const previous = Immutable.from({
      files: { 1: { id: 1, checksumSha256: 1, filename: 'test', serviceId: 'wefew', size: 1234, status: 'Downloaded', fileType: 'file' } },
      selectedFileList: []
    });
    const result = reducer(previous, { type: ACTION_TYPES.SELECT_ALL_DOWNLOADED_FILES });

    assert.deepEqual(result.selectedFileList, [{ id: 1, checksumSha256: 1, filename: 'test', serviceId: 'wefew', size: 1234, status: 'Downloaded', fileType: 'file' }]);
  });

  test('The TOGGLE_SELECTED_DOWNLOADED_FILE will unselect all selected files', function(assert) {
    const previous = Immutable.from({
      selectedFileList: [{ id: 1, checksumSha256: 1 }, { id: 2, checksumSha256: 2 }, { id: 3, checksumSha256: 3 }]
    });
    const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_SELECTED_DOWNLOADED_FILE, payload: { id: 1, checksumSha256: 1, filename: 'test', serviceId: 'wefew', size: 1234, status: 'Downloaded', type: 'file' } });

    assert.deepEqual(result.selectedFileList, [{ id: 2, checksumSha256: 2 }, { id: 3, checksumSha256: 3 }]);
  });

  test('The INCREMENT_DOWNLOADED_FILES_PAGE_NUMBER will incriment the page count', function(assert) {
    const previous = Immutable.from({
      pageNumber: 2
    });
    const result = reducer(previous, { type: ACTION_TYPES.INCREMENT_DOWNLOADED_FILES_PAGE_NUMBER });

    assert.equal(result.pageNumber, 3);
  });

  test('The SET_DOWNLOADED_FILES_SORT_BY will set sort criteria', function(assert) {
    const previous = Immutable.from({
      sortField: 'downloadedTime',
      isSortDescending: true
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_DOWNLOADED_FILES_SORT_BY, payload: { sortField: 'downloadedTime', isSortDescending: false } });

    assert.equal(result.isSortDescending, false);
  });

  test('The FETCH_NEXT_DOWNLOADED_FILES will fetch the next set of downloaded files', function(assert) {
    const previous = Immutable.from({
      files: {},
      loadMoreStatus: 'completed',
      selectedIndex: -1,
      totalItems: 4,
      sortField: 'downloadedTime',
      isSortDescending: true,
      selectedFileList: [],
      selectedFile: {},
      pageNumber: -1
    });


    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_NEXT_DOWNLOADED_FILES });
    const startEndState = reducer(previous, startAction);

    assert.equal(startEndState.loadMoreStatus, 'streaming');

    const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_NEXT_DOWNLOADED_FILES, payload: { data: { items: [{ id: 1 }, { id: 2 }, { id: 3 }] } } });
    const endState = reducer(startEndState, action);

    assert.equal(endState.loadMoreStatus, 'completed');
    assert.deepEqual(endState.files, { 1: { id: 1 }, 2: { id: 2 }, 3: { id: 3 } });

    const errorAction = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_NEXT_DOWNLOADED_FILES });
    const errorEndState = reducer(previous, errorAction);

    assert.equal(errorEndState.loadMoreStatus, 'error');
  });

  test('The FETCH_ALL_DOWNLOADED_FILES will fetch the download files', function(assert) {
    const previous = Immutable.from({
      files: {},
      loadMoreStatus: 'completed',
      selectedIndex: -1,
      totalItems: 4,
      sortField: 'downloadedTime',
      isSortDescending: true,
      selectedFileList: [],
      selectedFile: {},
      pageNumber: -1
    });


    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ALL_DOWNLOADED_FILES });
    const startEndState = reducer(previous, startAction);

    assert.equal(startEndState.areFilesLoading, 'wait');

    const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_ALL_DOWNLOADED_FILES, payload: { data: { items: [{ id: 1 }, { id: 2 }, { id: 3 }] } } });
    const endState = reducer(startEndState, action);

    assert.equal(endState.loadMoreStatus, 'completed');
    assert.deepEqual(endState.files, { 1: { id: 1 }, 2: { id: 2 }, 3: { id: 3 } });

    const errorAction = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_ALL_DOWNLOADED_FILES });
    const errorEndState = reducer(previous, errorAction);

    assert.equal(errorEndState.loadMoreStatus, 'error');
  });
});
