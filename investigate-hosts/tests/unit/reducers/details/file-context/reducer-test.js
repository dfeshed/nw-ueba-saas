import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/file-context/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';
import { driversData, filesData } from '../../../state/state';
import _ from 'lodash';

const initialState = {
  fileContext: {},
  contextLoadingStatus: 'wait',
  contextLoadMoreStatus: null,
  selectedRowId: null,
  fileContextSelections: [],
  sortConfig: null,
  fileStatus: {},
  totalItems: null,
  pageNumber: -1,
  hasNext: false,
  isRemediationAllowed: true
};

module('Unit | Reducers | File Context', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });


  test('The RESET_CONTEXT_DATA will reset the state', function(assert) {
    const previous = Immutable.from({
      fileContext: { 1: { path: '/root', fileProperties: { fileName: 'test' } } }
    });
    const result = reducer(previous, { type: ACTION_TYPES.RESET_CONTEXT_DATA });

    assert.deepEqual(result, initialState);
  });

  test('The SET_FILE_CONTEXT_ROW_SELECTION set the row selection', function(assert) {
    const previous = Immutable.from({
      selectedRowId: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_FILE_CONTEXT_ROW_SELECTION, payload: { id: 123 } });

    assert.equal(result.selectedRowId, 123, 'Expected to match the selected id 123');
  });

  test('The SET_FILE_CONTEXT_COLUMN_SORT set the column', function(assert) {
    const previous = Immutable.from({
      selectedRowId: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_FILE_CONTEXT_COLUMN_SORT, payload: { isDescending: true, field: 'fileName' } });

    assert.equal(result.sortConfig.field, 'fileName', 'Expected to match the column sort field');
  });

  test('The FETCH_FILE_CONTEXT sets the host details information', function(assert) {
    const previous = Immutable.from({
      fileContext: {},
      contextLoadingStatus: 'completed'
    });

    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_FILE_CONTEXT });
    const startEndState = reducer(previous, startAction);
    assert.deepEqual(startEndState.contextLoadingStatus, 'wait');

    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_FILE_CONTEXT,
      payload: { data: driversData },
      meta: {
        belongsTo: 'DRIVER'
      }
    });

    const endState = reducer(previous, action);
    assert.deepEqual(_.values(endState.fileContext).length, 4);
  });
  test('The HOST_DETAILS_DATATABLE_SORT_CONFIG resets the selected row id', function(assert) {
    const previous = Immutable.from({
      selectedRowId: '123'
    });
    const result = reducer(previous, { type: ACTION_TYPES.CHANGE_AUTORUNS_TAB });
    assert.equal(result.selectedFileId, null);
  });
  test('TOGGLE_FILE_CONTEXT_ROW_SELECTION should toggle the selected driver', function(assert) {
    const previous = Immutable.from({
      selectedRowId: '123',
      fileContextSelections: []
    });
    const driver = {
      id: 0,
      checksumSha256: 0,
      signature: '',
      size: 0,
      fileProperties: {
        checksumSha256: 'test',
        checksumSha1: 'test',
        checksumMd5: 'test',
        signature: {
          thumbprint: 1
        }
      }
    };
    let result = reducer(previous, { type: ACTION_TYPES.TOGGLE_FILE_CONTEXT_ROW_SELECTION, payload: driver, meta: { belongsTo: 'DRIVER' } });
    assert.equal(result.fileContextSelections.length, 1);
    assert.equal(result.fileContextSelections[0].id, 0);
    const next = Immutable.from({
      selectedRowId: '123',
      fileContextSelections: [driver]
    });
    result = reducer(next, { type: ACTION_TYPES.TOGGLE_FILE_CONTEXT_ROW_SELECTION, payload: driver, meta: { belongsTo: 'DRIVER' } });
    assert.equal(result.fileContextSelections.length, 0);
  });

  test('TOGGLE_FILE_CONTEXT_ALL_SELECTION should toggle the selected driver', function(assert) {
    const previous = Immutable.from({
      selectedRowId: '123',
      fileContextSelections: [],
      fileContext: {
        drivers_61: {
          id: '0',
          fileProperties: {
            checksumSha256: 'test',
            checksumSha1: 'test',
            checksumMd5: 'test',
            signature: {
              thumbprint: 1
            }
          }
        }
      }
    });
    const driver = {
      id: 0,
      checksumSha256: 0,
      signature: '',
      size: 0,
      fileProperties: {
        checksumSha256: 'test',
        checksumSha1: 'test',
        checksumMd5: 'test',
        signature: {
          thumbprint: 1
        }
      }
    };
    let result = reducer(previous, { type: ACTION_TYPES.TOGGLE_FILE_CONTEXT_ALL_SELECTION });
    assert.equal(result.fileContextSelections.length, 1);
    assert.equal(result.fileContextSelections[0].id, 0);
    const next = Immutable.from({
      fileContext: {
        drivers_61: {
          id: '0',
          fileProperties: {
            signature: {
              thumbprint: 1
            }
          }
        }
      },
      selectedRowId: '123',
      fileContextSelections: [driver]
    });
    result = reducer(next, { type: ACTION_TYPES.TOGGLE_FILE_CONTEXT_ALL_SELECTION, payload: driver });
    assert.equal(result.fileContextSelections.length, 0);
  });

  test('SAVE_FILE_CONTEXT_FILE_STATUS ', function(assert) {
    const previous = Immutable.from({
      selectedRowId: '123',
      fileContextSelections: [{ id: 'library_61' }],
      fileContext: {
        library_61: {
          id: 'library_61',
          checksumSha256: 1,
          fileProperties: { fileStatus: 'blacklist' }
        }
      }
    });
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SAVE_FILE_CONTEXT_FILE_STATUS,
      payload: { request: { data: { fileStatus: 'whitelist', checksums: [ 1, 2] } } }
    });
    const endState = reducer(previous, action);
    assert.equal(endState.fileContext.library_61.fileProperties.fileStatus, 'whitelist');
  });


  test('The GET_FILE_CONTEXT_FILE_STATUS set server response to state', function(assert) {
    const previous = Immutable.from({
      fileStatus: {}
    });

    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_FILE_CONTEXT_FILE_STATUS });
    const startEndState = reducer(previous, startAction);

    assert.deepEqual(startEndState.fileStatus, {});

    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_FILE_CONTEXT_FILE_STATUS,
      payload: { data: [ { resultList: [ { data: 'Whitelist' } ] } ] }
    });
    const endState = reducer(previous, action);
    assert.equal(endState.fileStatus, 'Whitelist');
  });

  test('The GET_FILE_CONTEXT_FILE_STATUS not change the state', function(assert) {
    const previous = Immutable.from({
      fileStatus: 'Blacklist'
    });
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_FILE_CONTEXT_FILE_STATUS,
      payload: { data: [ { resultList: [] } ] }
    });
    const endState = reducer(previous, action);
    assert.equal(endState.fileStatus, 'Blacklist');
  });

  test('The INCREMENT_PAGE_NUMBER will increment page number sets the state', function(assert) {
    const previous = Immutable.from({
      pageNumber: 0
    });
    const result = reducer(previous, { type: ACTION_TYPES.INCREMENT_PAGE_NUMBER });
    assert.equal(result.pageNumber, 1);
  });

  test('The FETCH_FILE_CONTEXT_PAGINATED sets normalized server response to state', function(assert) {
    const previous = Immutable.from({
      fileContext: {},
      selectedFileId: null,
      pageNumber: -1,
      totalItems: 0,
      contextLoadMoreStatus: null
    });

    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_FILE_CONTEXT_PAGINATED, meta: { belongsTo: 'FILE' } });
    const startEndState = reducer(previous, startAction);
    assert.deepEqual(startEndState.contextLoadMoreStatus, 'streaming');

    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_FILE_CONTEXT_PAGINATED,
      payload: { data: filesData },
      meta: { belongsTo: 'FILE' }
    });
    const endState = reducer(previous, action);

    assert.equal(endState.pageNumber, 10);

    const data = { ...filesData, hasNext: false };

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_FILE_CONTEXT_PAGINATED,
      payload: { data },
      meta: { belongsTo: 'FILE' }
    });

    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.contextLoadMoreStatus, 'completed');
  });
  test('FILE_CONTEXT_RESET_SELECTION should selected all drivers', function(assert) {
    const driver = {
      id: 0,
      checksumSha256: 0,
      signature: '',
      size: 0,
      fileProperties: {
        checksumSha256: 'test',
        checksumSha1: 'test',
        checksumMd5: 'test'
      }
    };
    const previous = Immutable.from({
      selectedRowId: '123',
      fileContextSelections: [driver]
    });
    const result = reducer(previous, { type: ACTION_TYPES.FILE_CONTEXT_RESET_SELECTION, meta: { belongsTo: 'DRIVER' } });
    assert.equal(result.fileContextSelections.length, 0);
  });

  test('FETCH_REMEDIATION_STATUS', function(assert) {
    const previous = Immutable.from({
      isRemediationAllowed: true
    });

    const successAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_REMEDIATION_STATUS,
      payload: { data: false }
    });
    const newEndState = reducer(previous, successAction);
    assert.equal(newEndState.isRemediationAllowed, false);
  });

});
