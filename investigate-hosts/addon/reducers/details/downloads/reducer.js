import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';
import { fileListSchema } from './schema';
import { normalize } from 'normalizr';

const initialState = Immutable.from({
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

const _toggleSelectedFile = (state, payload) => {
  const { selectedFileList } = state;
  const { id, filename, size, checksumSha256, fileType, status, serviceId } = payload;
  let selectedList = [];
  // Previously selected file

  if (selectedFileList.some((file) => file.id === id)) {
    selectedList = selectedFileList.filter((file) => file.id !== id);
  } else {
    selectedList = [...selectedFileList,
      { id, filename, size, checksumSha256, fileType, status, serviceId }];
  }
  return state.set('selectedFileList', selectedList);

};

const _handleAppendFiles = (action) => {
  return (state) => {
    const { payload: { data } } = action;
    const { files } = state;
    const normalizedData = normalize(action.payload.data.items, [fileListSchema]);
    const { file } = normalizedData.entities;
    return state.merge({
      files: { ...files, ...file },
      totalItems: data.totalItems,
      pageNumber: data.pageNumber,
      areFilesLoading: 'completed',
      loadMoreStatus: data.hasNext ? 'stopped' : 'completed',
      hasNext: data.hasNext
    });
  };
};

const hostDownloads = reduxActions.handleActions({

  [ACTION_TYPES.FETCH_ALL_DOWNLOADED_FILES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({
        files: {},
        areFilesLoading: 'wait',
        totalItems: 0,
        selectedFileList: [],
        selectedIndex: -1,
        selectedDetailFile: null
      }),
      failure: (s) => s.set('loadMoreStatus', 'error'),
      success: _handleAppendFiles(action)
    });
  },

  [ACTION_TYPES.FETCH_NEXT_DOWNLOADED_FILES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('loadMoreStatus', 'streaming'),
      failure: (s) => s.set('loadMoreStatus', 'error'),
      success: _handleAppendFiles(action),
      finish: (s) => s.set('areFilesLoading', 'completed')
    });
  },

  [ACTION_TYPES.SET_DOWNLOADED_FILES_SORT_BY]: (state, { payload: { sortField, isSortDescending } }) => state.merge({
    sortField,
    isSortDescending
  }),

  [ACTION_TYPES.INCREMENT_DOWNLOADED_FILES_PAGE_NUMBER]: (state) => state.set('pageNumber', state.pageNumber + 1),

  [ACTION_TYPES.TOGGLE_SELECTED_DOWNLOADED_FILE]: (state, { payload }) => _toggleSelectedFile(state, payload),

  [ACTION_TYPES.SELECT_ALL_DOWNLOADED_FILES]: (state) => {
    const selectedList = Object.values(state.files).map(({ id, filename, size, checksumSha256, fileType, status, serviceId }) => ({
      id, filename, size, checksumSha256, fileType, status, serviceId
    }));
    return state.set('selectedFileList', selectedList);
  },

  [ACTION_TYPES.DESELECT_ALL_DOWNLOADED_FILES]: (state) => state.set('selectedFileList', []),

  [ACTION_TYPES.RESET_DOWNLOADED_FILES]: (state) => {
    const { isSortDescending } = state;
    return state.merge({ ...initialState, isSortDescending });
  },

  [ACTION_TYPES.SET_SELECTED_DOWNLOADED_FILE_INDEX]: (state, { payload }) => state.set('selectedIndex', payload)
}, initialState);

export default hostDownloads;