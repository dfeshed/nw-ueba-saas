import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'investigate-files/actions/types';
import { contextDataParser } from 'investigate-shared/helpers/context-parser';

const fileListState = Immutable.from({
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
  listOfServices: null,
  activeDataSourceTab: 'ALERT',
  lookupData: [{}],
  showRiskPanel: false,
  contextError: null,
  contextLoadingStatus: 'wait',
  selectedFileList: []
});

const _handleAppendFiles = (action) => {
  return (state) => {
    const { payload: { data } } = action;
    const { files } = state;

    return state.merge({
      files: [...files, ...data.items],
      totalItems: data.totalItems,
      pageNumber: data.pageNumber,
      loadMoreStatus: data.hasNext || data.totalItems >= 1000 ? 'stopped' : 'completed',
      hasNext: data.hasNext
    });
  };
};

const _toggleSelectedFile = (state, payload) => {
  const { selectedFileList } = state;
  const { id, checksumSha256 } = payload;
  let selected = [];
  // Previously selected file
  if (selectedFileList.some((file) => file.id === id)) {
    selected = selectedFileList.filter((file) => file.id !== id);
  } else {
    selected = [...selectedFileList, { id, checksumSha256 }];
  }
  return state.set('selectedFileList', selected);

};
const fileListReducer = handleActions({
  [ACTION_TYPES.FETCH_NEXT_FILES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('loadMoreStatus', 'streaming'),
      failure: (s) => s.set('loadMoreStatus', 'error'),
      success: _handleAppendFiles(action),
      finish: (s) => s.set('areFilesLoading', 'completed')
    });
  },

  [ACTION_TYPES.DOWNLOAD_FILE_AS_CSV]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('downloadStatus', 'streaming'),
      success: (s) => s.merge({
        downloadId: action.payload.data.id,
        downloadStatus: 'completed'
      })
    });
  },

  [ACTION_TYPES.SET_SORT_BY]: (state, { payload: { sortField, isSortDescending } }) => state.merge({
    sortField,
    areFilesLoading: 'sorting',
    isSortDescending
  }),

  [ACTION_TYPES.RESET_FILES]: (state) => state.merge({
    files: [],
    pageNumber: -1,
    totalItems: 0,
    areFilesLoading: 'sorting'
  }),

  [ACTION_TYPES.GET_LIST_OF_SERVICES]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('listOfServices', action.payload.data)
    });
  },

  [ACTION_TYPES.INCREMENT_PAGE_NUMBER]: (state) => state.set('pageNumber', state.pageNumber + 1),

  [ACTION_TYPES.RESET_DOWNLOAD_ID]: (state) => state.set('downloadId', null),

  [ACTION_TYPES.CHANGE_DATASOURCE_TAB]: (state, { payload: { tabName } }) => state.set('activeDataSourceTab', tabName),

  [ACTION_TYPES.SET_CONTEXT_DATA]: (state, { payload }) => {
    const lookupData = [].concat(contextDataParser([payload, state.lookupData]));
    return state.merge({ lookupData, contextLoadingStatus: 'completed' });
  },

  [ACTION_TYPES.CLEAR_PREVIOUS_CONTEXT]: (state) => state.merge({ lookupData: [{}], contextLoadingStatus: 'wait' }),

  [ACTION_TYPES.TOGGLE_RISK_PANEL_VISIBILITY]: (state, { payload }) => state.set('showRiskPanel', payload),

  [ACTION_TYPES.CONTEXT_ERROR]: (state, { payload }) => state.set('contextError', payload),

  [ACTION_TYPES.TOGGLE_SELECTED_FILE]: (state, { payload }) => _toggleSelectedFile(state, payload),

  [ACTION_TYPES.SELECT_ALL_FILES]: (state) => state.set('selectedFileList', state.files.map((file) => ({ id: file.id, checksumSha256: file.checksumSha256 }))),

  [ACTION_TYPES.DESELECT_ALL_FILES]: (state) => state.set('selectedFileList', [])
}, fileListState);

export default fileListReducer;
