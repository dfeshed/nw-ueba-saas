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
  selectedFileHash: null,
  pageNumber: -1,
  totalItems: 0,
  sortField: 'fileName',
  isDescOrder: false,
  filesLoadingStatus: 'wait',
  filesLoadMoreStatus: 'stopped'
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
test('The SET_SELECTED_FILE will sets the selected file hash the state', function(assert) {
  const previous = Immutable.from({
    selectedFileHash: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_FILE, payload: { checksumSha256: 'aaazza234aa2123' } });
  assert.equal(result.selectedFileHash, 'aaazza234aa2123');
});

test('The GET_HOST_FILES sets normalized server response to state', function(assert) {
  const previous = Immutable.from({
    files: [],
    selectedFileHash: null,
    pageNumber: -1,
    totalItems: 0
  });
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_HOST_FILES,
    payload: { data: filesData }
  });
  const endState = reducer(previous, action);

  assert.equal(endState.files.length, 3);
  assert.equal(endState.pageNumber, 10);
});
