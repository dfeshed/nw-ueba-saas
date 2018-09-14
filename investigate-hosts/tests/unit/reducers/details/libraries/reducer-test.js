import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/libraries/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';
import { libraries } from '../../../state/state';
import _ from 'lodash';

module('Unit | Reducers | libraries');

const initialState = Immutable.from({
  library: null,
  libraryLoadingStatus: null,
  selectedRowId: null,
  processList: null,
  selectedLibraryList: [],
  libraryStatusData: {}
});

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});


test('The RESET_HOST_DETAILS will reset the state', function(assert) {
  const previous = Immutable.from({
    library: { 1: { path: '/root', fileProperties: { fileName: 'test' } } }
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_HOST_DETAILS });

  assert.deepEqual(result, initialState);
});

test('The SET_DLLS_SELECTED_ROW will sets the selected row to state', function(assert) {
  const previous = Immutable.from({
    selectedRowId: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_DLLS_SELECTED_ROW, payload: { id: 123 } });

  assert.equal(result.selectedRowId, 123, 'Expected to match the selected id 123');
});

test('The FETCH_FILE_CONTEXT_DLLS sets normalized server response to state', function(assert) {
  const previous = Immutable.from({
    library: null,
    libraryLoadingStatus: null
  });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_FILE_CONTEXT_DLLS });
  const startEndState = reducer(previous, startAction);
  assert.deepEqual(startEndState.libraryLoadingStatus, 'wait');

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_FILE_CONTEXT_DLLS,
    payload: { data: libraries }
  });

  const endState = reducer(previous, action);
  assert.deepEqual(_.values(endState.library).length, 8);
});

test('The GET_LIBRARY_PROCESS_INFO process information to the state', function(assert) {
  const previous = Immutable.from({
    processList: null
  });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_LIBRARY_PROCESS_INFO });
  const startEndState = reducer(previous, startAction);
  assert.deepEqual(startEndState.processList, null);

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_LIBRARY_PROCESS_INFO,
    payload: { data: new Array(10) }
  });

  const endState = reducer(previous, action);
  assert.deepEqual(_.values(endState.processList).length, 10);
});
test('TOGGLE_SELECTED_LIBRARY should toggle the selected library', function(assert) {
  const previous = Immutable.from({
    selectedRowId: '123',
    selectedLibraryList: []
  });
  const library = {
    id: 0,
    checksumSha256: 0,
    signature: '',
    size: 0 };
  let result = reducer(previous, { type: ACTION_TYPES.TOGGLE_SELECTED_LIBRARY, payload: library });
  assert.equal(result.selectedLibraryList.length, 1);
  assert.equal(result.selectedLibraryList[0].id, 0);
  const next = Immutable.from({
    selectedRowId: '123',
    selectedLibraryList: [library]
  });
  result = reducer(next, { type: ACTION_TYPES.TOGGLE_SELECTED_LIBRARY, payload: library });
  assert.equal(result.selectedLibraryList.length, 0);
});
test('TOGGLE_ALL_LIBRARY_SELECTION should toggle the selected library', function(assert) {
  const previous = Immutable.from({
    selectedRowId: '123',
    selectedLibraryList: [],
    library: {
      libraryt_61: {
        id: '0'
      }
    }
  });
  const library = {
    id: 0,
    checksumSha256: 0,
    signature: '',
    size: 0 };
  let result = reducer(previous, { type: ACTION_TYPES.TOGGLE_ALL_LIBRARY_SELECTION });
  assert.equal(result.selectedLibraryList.length, 1);
  assert.equal(result.selectedLibraryList[0].id, 0);
  const next = Immutable.from({
    selectedRowId: '123',
    selectedLibraryList: [library]
  });
  result = reducer(next, { type: ACTION_TYPES.TOGGLE_ALL_LIBRARY_SELECTION, payload: library });
  assert.equal(result.selectedLibraryList.length, 0);
});
test('SAVE_LIBRARY_STATUS ', function(assert) {
  const previous = Immutable.from({
    selectedRowId: '123',
    selectedLibraryList: [],
    library: {
      library_61: {
        id: 'library_61',
        checksumSha256: 1,
        fileProperties: { fileStatus: 'blacklist' }
      }
    }
  });
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SAVE_LIBRARY_STATUS,
    payload: { request: { data: { fileStatus: 'whitelist', checksums: [ 1, 2] } } }
  });
  const endState = reducer(previous, action);
  assert.equal(endState.library.library_61.fileProperties.fileStatus, 'whitelist');
});
test('The GET_LIBRARY_STATUS set server response to state', function(assert) {
  const previous = Immutable.from({
    libraryStatusData: {}
  });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_LIBRARY_STATUS });
  const startEndState = reducer(previous, startAction);

  assert.deepEqual(startEndState.libraryStatusData, {});

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_LIBRARY_STATUS,
    payload: { data: [ { resultList: [ { data: 'Whitelist' } ] } ] }
  });
  const endState = reducer(previous, action);
  assert.equal(endState.libraryStatusData, 'Whitelist');
});
