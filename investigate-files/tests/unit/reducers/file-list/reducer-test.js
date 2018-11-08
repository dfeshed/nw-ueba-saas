import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/file-list/reducer';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Reducers | file-list', function() {
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

  const contextData = {
    data: [
      {
        'dataSourceType': 'Alerts',
        'dataSourceGroup': 'Alerts',
        'resultList': [
          {
            '_id': {
              '$oid': '5ae9b79826362f0bbbfae082'
            },
            'receivedTime': {
              '$date': '2018-05-02T13:05:28.222Z'
            },
            'alert': {
              'source': 'Event Stream Analysis',
              'timestamp': {
                '$date': '2018-05-02T13:05:28.000Z'
              },
              'risk_score': 70,
              'name': 'Unsigned Creates Remote Thread',
              'numEvents': 1
            }
          }
        ]
      }
    ]
  };

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, {
      fileData: {},
      areFilesLoading: 'wait',
      loadMoreStatus: 'completed',
      pageNumber: -1,
      totalItems: 0,
      hasNext: false,
      sortField: 'score',
      isSortDescending: true,
      downloadStatus: 'completed',
      downloadId: null,
      listOfServices: null,
      activeDataSourceTab: 'RISK_PROPERTIES',
      lookupData: [{}],
      contextError: null,
      contextLoadingStatus: 'wait',
      selectedFileList: [],
      agentCountMapping: {},
      fileStatusData: {},
      hostNameList: [],
      fetchHostNameListError: false,
      fetchMetaValueLoading: false,
      riskScoreContext: null,
      riskScoreContextError: null,
      isRemediationAllowed: true,
      selectedFile: {},
      selectedDetailFile: null,
      selectedIndex: null
    });
  });

  test('INITIALIZE_FILE_DETAIL sets the selected file', function(assert) {
    const previous = Immutable.from({
      selectedDetailFile: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.INITIALIZE_FILE_DETAIL, payload: { data: [{ firstFileName: 'dtf.exe', id: 'checksum-123' }] } });
    assert.equal(result.selectedDetailFile.id, 'checksum-123', 'File matching with id is returned');
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
      fileData: { a: { firstFileName: 'test.dll' } },
      pageNumber: 0,
      areFilesLoading: 'completed'
    });
    const result = reducer(previous, { type: ACTION_TYPES.RESET_FILES });
    assert.equal(Object.values(result.fileData).length, 0);
    assert.equal(result.pageNumber, -1);
    assert.equal(result.areFilesLoading, 'wait');
  });

  test('INCREMENT_PAGE_NUMBER action should increment page number', function(assert) {
    const previous = Immutable.from({
      pageNumber: 0
    });
    const result = reducer(previous, { type: ACTION_TYPES.INCREMENT_PAGE_NUMBER });
    assert.equal(result.pageNumber, 1);
  });

  test('SET_SELECTED_FILE set the selected file', function(assert) {
    const previous = Immutable.from({
      selectedFile: {}
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_FILE, payload: { fileName: 'test.exe' } });
    assert.equal(result.selectedFile.fileName, 'test.exe');
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
      fileData: FILE_LIST,
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
      fileData: FILE_LIST,
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
    assert.equal(Object.values(newEndState.fileData).length, 4);
  });

  test('The FETCH_NEXT_FILES sets load more state properly', function(assert) {
    const previous = Immutable.from({
      fileData: FILE_LIST,
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
    assert.equal(Object.values(newEndState.fileData).length, 4);

    const successAction1 = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_NEXT_FILES,
      payload: { data: { hasNext: false, items: [{ id: 11 }] } }
    });
    const newEndState1 = reducer(previous, successAction1);

    assert.equal(newEndState1.loadMoreStatus, 'completed', 'load more status is completed');
  });

  test('The FETCH_NEXT_FILES sets load more state properly when totalItems > 1000', function(assert) {
    const previous = Immutable.from({
      fileData: FILE_LIST,
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
    assert.equal(Object.values(newEndState.fileData).length, 4);

    const successAction1 = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_NEXT_FILES,
      payload: { data: { hasNext: true, items: [{ id: 11 }], totalItems: 1500 } }
    });
    const newEndState1 = reducer(previous, successAction1);

    assert.equal(newEndState1.loadMoreStatus, 'stopped', 'load more status is stopped');
  });

  test('The FETCH_NEXT_FILES sets load more state properly when totalItems > 1000 and hasNext is false', function(assert) {
    const previous = Immutable.from({
      fileData: FILE_LIST,
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
    assert.equal(Object.values(newEndState.fileData).length, 4);

    const successAction1 = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_NEXT_FILES,
      payload: { data: { hasNext: false, items: [{ id: 11 }], totalItems: 1500 } }
    });
    const newEndState1 = reducer(previous, successAction1);

    assert.equal(newEndState1.loadMoreStatus, 'completed', 'load more status is completed');
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

  test('Fetch the data from context server', function(assert) {
    const previous = Immutable.from({
      lookupData: [{}]
    });
    const newEndState = reducer(previous, { type: ACTION_TYPES.SET_CONTEXT_DATA, payload: contextData.data });
    assert.equal(newEndState.lookupData.length, 1);
  });

  test('The context state being cleared', function(assert) {
    const previous = Immutable.from({
      lookupData: contextData.data
    });
    const newEndState = reducer(previous, { type: ACTION_TYPES.CLEAR_PREVIOUS_CONTEXT });
    assert.deepEqual(newEndState.lookupData[0], {}, 'lookupData state is cleared.');
  });

  test('contextError state when context server is not reachable', function(assert) {
    const previous = Immutable.from({
      contextError: null
    });
    const newEndState = reducer(previous, { type: ACTION_TYPES.CONTEXT_ERROR, payload: 'context.error.timeout' });
    assert.deepEqual(newEndState.contextError, 'context.error.timeout', 'contextError state has been changed to true.');
  });

  test('toggling selected file in filelist ', function(assert) {
    const previous = Immutable.from({
      selectedFileList: [],
      fileStatusData: { status: 'Blacklsit' }
    });
    const newEndState = reducer(previous, { type: ACTION_TYPES.TOGGLE_SELECTED_FILE, payload: { id: 1, checksumSha256: 'abc' } });
    assert.deepEqual(newEndState.selectedFileList.length, 1, 'state for selected file list updating.');
    assert.deepEqual(newEndState.fileStatusData, {});
  });

  test('SELECT ALL FILES in filelist ', function(assert) {
    const previous = Immutable.from({
      fileData: {
        1: {
          id: 1,
          checksumSha256: 'ABC'
        },
        2: {
          id: 2,
          checksumSha256: 'EFG'
        }
      }
    });
    const newEndState = reducer(previous, { type: ACTION_TYPES.SELECT_ALL_FILES });
    assert.deepEqual(newEndState.selectedFileList.length, 2, 'state for selected all file list updated.');
  });
  test('DESELECT ALL FILES in filelist ', function(assert) {
    const previous = Immutable.from({
      fileData: {
        1: {
          id: 1,
          checksumSha256: 'ABC'
        },
        2: {
          id: 2,
          checksumSha256: 'EFG'
        }
      }
    });
    const newEndState = reducer(previous, { type: ACTION_TYPES.DESELECT_ALL_FILES });
    assert.deepEqual(newEndState.selectedFileList.length, 0, 'state for Deselected all file list updated.');
  });
  test('The GET_FILE_STATUS_HISTORY update to state', function(assert) {
    const previous = Immutable.from({
      downloadId: null,
      downloadStatus: 'completed'
    });
    const successAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_FILE_STATUS_HISTORY,
      payload: { data: [ { id: 111 } ] }
    });
    const newEndState = reducer(previous, successAction);
    assert.equal(newEndState.selectedFileStatusHistory.length, 1);
  });

  test('The GET_FILE_STATUS update to state when valid response', function(assert) {
    const previous = Immutable.from({
      fileStatusData: {},
      alertsData: null
    });
    const successAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_FILE_STATUS,
      payload: { data: [ { resultList: [ { data: { status: 'Blacklist' } }] } ] }
    });
    const newEndState = reducer(previous, successAction);
    assert.equal(newEndState.fileStatusData.status, 'Blacklist');
  });

  test('The GET_FILE_STATUS update to state default state', function(assert) {
    const previous = Immutable.from({
      fileStatusData: {}
    });
    const successAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_FILE_STATUS,
      payload: { data: [ { resultList: [] } ] }
    });
    const newEndState = reducer(previous, successAction);
    assert.deepEqual(newEndState.fileStatusData, {});
  });

  test('Fetch the data from context server', function(assert) {
    const previous = Immutable.from({
      lookupData: [{}]
    });
    const newEndState = reducer(previous, { type: ACTION_TYPES.SET_CONTEXT_DATA, payload: contextData.data });
    assert.equal(newEndState.lookupData.length, 1);
  });


  test('Fetch host name error is set to true', function(assert) {
    const previous = Immutable.from({
      fetchHostNameListError: false
    });
    const newEndState = reducer(previous, { type: ACTION_TYPES.FETCH_HOST_NAME_LIST_ERROR });
    assert.equal(newEndState.fetchHostNameListError, true);
  });

  test('Fetch host name error is set to false', function(assert) {
    const previous = Immutable.from({
      fetchHostNameListError: true
    });
    const newEndState = reducer(previous, { type: ACTION_TYPES.INIT_FETCH_HOST_NAME_LIST });
    assert.equal(newEndState.fetchHostNameListError, false);
  });

  test('Fetch host name error is set to false', function(assert) {
    const previous = Immutable.from({
      hostNameList: []
    });
    const newEndState = reducer(previous, { type: ACTION_TYPES.SET_HOST_NAME_LIST, payload: new Array(10) });
    assert.equal(newEndState.hostNameList.length, 10);
  });

  test('Fetch Complete will set to false', function(assert) {
    const previous = Immutable.from({
      fetchMetaValueLoading: true
    });
    const newEndState = reducer(previous, { type: ACTION_TYPES.META_VALUE_COMPLETE });
    assert.equal(newEndState.fetchMetaValueLoading, false);
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

  test('SET_SELECTED_INDEX', function(assert) {
    const previous = Immutable.from({
      selectedIndex: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_INDEX, payload: 2 });
    assert.equal(result.selectedIndex, 2);
  });

  test('The FETCH_ALL_FILES sets the first set of files', function(assert) {
    const previous = Immutable.from({
      fileData: {},
      loadMoreStatus: null
    });
    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ALL_FILES });
    const endState = reducer(previous, startAction);

    assert.equal(endState.areFilesLoading, 'wait');

    const successAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_NEXT_FILES,
      payload: { data: { hasNext: true, items: [{ id: 11 }], totalItems: 1500 } }
    });
    const newEndState = reducer(previous, successAction);

    assert.equal(newEndState.areFilesLoading, 'completed', 'data loaded');
    assert.equal(Object.values(newEndState.fileData).length, 1);
  });
});
