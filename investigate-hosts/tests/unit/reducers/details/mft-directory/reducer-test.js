import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/mft-directory/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';

const initialState = {
  files: {},
  subDirectories: [],
  loadMoreStatus: 'completed',
  selectedIndex: -1,
  totalItems: 4,
  sortField: 'creationTime',
  isSortDescending: true,
  selectedFileList: [],
  selectedFile: {},
  pageNumber: 0,
  selectedParentDirectory: { recordNumber: 0 },
  openDirectories: [],
  selectedDirectoryForDetails: 0,
  isDirectories: true,
  inUse: true,
  pageSize: 65000,
  fileSource: ''
};

module('Unit | Reducers | mft-directory', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });

  test('The FETCH_MFT_SUBDIRECTORIES will fetch the root drive of the MFT', function(assert) {
    const previous = Immutable.from({
      subDirectories: [],
      selectedParentDirectory: {}
    });

    const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_MFT_SUBDIRECTORIES, payload: { data: { items: [{
      mftId: '5d19c6c7c8811e3057c68fd8',
      recordNumber: 5,
      allocatedSize: 0,
      directoryCount: 14,
      directory: true,
      name: 'C',
      fullPathName: 'C',
      parentDirectory: 0,
      ancestors: []
    }] } } });
    const endState = reducer(previous, action);

    assert.equal(endState.subDirectories[0].mftId, '5d19c6c7c8811e3057c68fd8');

  });

  test('The FETCH_MFT_SUBDIRECTORIES will fetch and add to the selected parent directory', function(assert) {
    const previous = Immutable.from({
      subDirectories: [{
        mftId: '5d19c6c7c8811e3057c68fd8',
        recordNumber: 5,
        allocatedSize: 0,
        directoryCount: 14,
        directory: true,
        name: 'C',
        fullPathName: 'C',
        parentDirectory: 0,
        ancestors: []
      }],
      selectedParentDirectory: { recordNumber: 5, ancestors: [] },
      openDirectories: []
    });

    const action1 = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_MFT_SUBDIRECTORIES, payload: { data: { items: [{
      mftId: '5d19c6c7c8811e3057c68fd8',
      recordNumber: 15,
      allocatedSize: 0,
      directoryCount: 14,
      directory: true,
      name: 'C',
      fullPathName: 'C',
      parentDirectory: 0,
      ancestors: [5]
    },
    {
      mftId: '5d19c6c7c8811e3057c68fd8',
      recordNumber: 16,
      allocatedSize: 0,
      directoryCount: 14,
      directory: true,
      name: 'C',
      fullPathName: 'C',
      parentDirectory: 0,
      ancestors: [5]
    }] } } });
    const endState1 = reducer(previous, action1);

    assert.equal(endState1.subDirectories[0].children.length, 2, 'Directories added to level 1');

    const selectedParentUpdated = reducer(endState1, { type: ACTION_TYPES.SET_SELECTED_MFT_PARENT_DIRECTORY, payload: {
      selectedParentDirectory: { recordNumber: 16, ancestors: [5], close: false },
      pageSize: 6500,
      isDirectories: true,
      inUse: true
    } });

    const action2 = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_MFT_SUBDIRECTORIES, payload: { data: { items: [{
      mftId: '5d19c6c7c8811e3057c68fd8',
      recordNumber: 20,
      allocatedSize: 0,
      directoryCount: 14,
      directory: true,
      name: 'C',
      fullPathName: 'C',
      parentDirectory: 0,
      ancestors: [5, 16]
    }] } } });
    const endState2 = reducer(selectedParentUpdated, action2);

    assert.equal(endState2.subDirectories[0].children[1].children.length, 1, 'Directories added to level 2');

  });

  test('SET_SELECTED_MFT_PARENT_DIRECTORY will set the selected parent and add it as a part of the open directory list', function(assert) {
    const previous = Immutable.from({
      subDirectories: [{
        mftId: '5d19c6c7c8811e3057c68fd8',
        recordNumber: 5,
        allocatedSize: 0,
        directoryCount: 14,
        directory: true,
        name: 'C',
        fullPathName: 'C',
        parentDirectory: 0,
        ancestors: []
      }],
      selectedParentDirectory: { recordNumber: 5, ancestors: [] },
      openDirectories: []
    });
    const endState1 = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_MFT_PARENT_DIRECTORY, payload: {
      selectedParentDirectory: { recordNumber: 16, ancestors: [5], close: false },
      pageSize: 6500,
      isDirectories: true,
      inUse: true
    } });
    assert.deepEqual(endState1.selectedParentDirectory, { recordNumber: 16, ancestors: [5], close: false }, 'New selected parent set');
  });

  test('SET_SELECTED_MFT_DIRECTORY_FOR_DETAILS will set the selected directory or file fetching option', function(assert) {
    const previous = Immutable.from({
      subDirectories: [{
        mftId: '5d19c6c7c8811e3057c68fd8',
        recordNumber: 5,
        allocatedSize: 0,
        directoryCount: 14,
        directory: true,
        name: 'C',
        fullPathName: 'C',
        parentDirectory: 0,
        ancestors: []
      }],
      selectedParentDirectory: { recordNumber: 5, ancestors: [] },
      openDirectories: [],
      selectedDirectoryForDetails: 0,
      fileSource: ''
    });

    const endState1 = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_MFT_DIRECTORY_FOR_DETAILS, payload: { selectedDirectoryForDetails: 16, fileSource: 'drive' } });
    assert.equal(endState1.selectedDirectoryForDetails, 16);
    assert.equal(endState1.fileSource, 'drive');
  });

  test('RESET_MFT_FILE_DATA will reset the state', function(assert) {
    const previous = Immutable.from({
      subDirectories: [{
        mftId: '5d19c6c7c8811e3057c68fd8',
        recordNumber: 5,
        allocatedSize: 0,
        directoryCount: 14,
        directory: true,
        name: 'C',
        fullPathName: 'C',
        parentDirectory: 0,
        ancestors: []
      }],
      selectedParentDirectory: { recordNumber: 5, ancestors: [] },
      openDirectories: [],
      selectedDirectoryForDetails: 16,
      fileSource: ''
    });

    const endState1 = reducer(previous, { type: ACTION_TYPES.RESET_MFT_FILE_DATA });
    assert.equal(endState1.selectedDirectoryForDetails, 0);
    assert.equal(endState1.fileSource, '');
    assert.deepEqual(endState1.subDirectories, []);
  });

  test('FETCH_MFT_SUBDIRECTORIES_AND_FILES fetches files and subdirectories for ', function(assert) {
    const previous = Immutable.from({
      files: {},
      subDirectories: [],
      selectedParentDirectory: {}
    });
    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_MFT_SUBDIRECTORIES_AND_FILES });
    const startEndState = reducer(previous, startAction);
    assert.equal(startEndState.loading, 'wait');

    const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_MFT_SUBDIRECTORIES_AND_FILES, payload: { data: { items: [{
      mftId: '5d19c6c7c8811e3057c68fd8',
      recordNumber: 5,
      allocatedSize: 0,
      directoryCount: 14,
      directory: true,
      name: 'C',
      fullPathName: 'C',
      parentDirectory: 0,
      ancestors: []
    }] } } });
    const endState = reducer(previous, action);
    assert.equal(endState.totalItems, 1);
    assert.equal(Object.keys(endState.files).length, 1);
    assert.equal(endState.loading, 'completed');
  });

});