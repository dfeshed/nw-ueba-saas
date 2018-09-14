import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

const hostFilesState = Immutable.from({
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

const _handleAppendFiles = (action) => {
  return (state) => {
    const { payload: { data } } = action;
    const { files = [] } = state;
    const allFiles = [...files, ...data.items];
    return state.merge({
      totalItems: data.totalItems,
      files: allFiles,
      pageNumber: data.pageNumber,
      filesLoadMoreStatus: data.hasNext ? 'stopped' : 'completed'
    });
  };
};

const _toggleSelectedFile = (state, payload) => {
  const { selectedFileList } = state;
  const { id, checksumSha256, signature, size } = payload;
  let selectedList = [];
  // Previously selected file

  if (selectedFileList.some((file) => file.id === id)) {
    selectedList = selectedFileList.filter((file) => file.id !== id);
  } else {
    selectedList = [...selectedFileList, { id, checksumSha256, signature, size }];
  }
  return state.merge({ 'selectedFileList': selectedList, 'fileStatusData': {} });

};

const hostFilesReducer = handleActions({

  [ACTION_TYPES.RESET_HOST_DETAILS]: (state) => state.merge(hostFilesState),

  [ACTION_TYPES.INCREMENT_PAGE_NUMBER]: (state) => state.set('pageNumber', state.pageNumber + 1),

  [ACTION_TYPES.RESET_HOST_FILES]: (state) => state.merge({ pageNumber: -1, files: [] }),

  [ACTION_TYPES.SET_SELECTED_FILE]: (state, { payload: { id } }) => state.set('selectedFileId', id),

  [ACTION_TYPES.TOGGLE_SELECTED_FILE]: (state, { payload }) => _toggleSelectedFile(state, payload),

  [ACTION_TYPES.SELECT_ALL_FILES]: (state) => state.set('selectedFileList', Object.values(state.files).map((file) => ({ id: file.id, checksumSha256: file.checksumSha256 }))),

  [ACTION_TYPES.DESELECT_ALL_FILES]: (state) => state.set('selectedFileList', []),

  [ACTION_TYPES.SAVE_FILE_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s, action) => {
        const { files } = s;
        const { payload: { request: { data } } } = action;
        const { checksums, fileStatus } = data;
        let newData = [];
        for (let i = 0; i < checksums.length; i++) {
          newData = Object.values(files).map((file) => {
            if (file.checksumSha256 == checksums[i]) {
              const newFileProperties = { ...file.fileProperties, fileStatus };
              return { ...file, fileProperties: newFileProperties };
            }
            return file;
          });
        }
        return s.set('files', newData);
      }
    });
  },

  [ACTION_TYPES.GET_FILE_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const [payLoadData] = action.payload.data;
        if (payLoadData && payLoadData.resultList.length) {
          return s.set('fileStatusData', payLoadData.resultList[0].data);
        }
        return s;
      }
    });
  },

  [ACTION_TYPES.GET_HOST_FILES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('filesLoadMoreStatus', 'streaming'),
      finish: (s) => s.set('filesLoadingStatus', 'completed'),
      failure: (s) => s.merge({ filesLoadingStatus: 'error', filesLoadMoreStatus: 'error' }),
      success: _handleAppendFiles(action)
    });
  },

  [ACTION_TYPES.SET_HOST_FILES_SORT_BY]: (state, { payload: { sortOption: { sortField, isDescOrder } } }) => {
    return state.merge({
      sortField,
      filesLoadingStatus: 'sorting',
      isDescOrder
    });
  }

}, hostFilesState);

export default hostFilesReducer;
