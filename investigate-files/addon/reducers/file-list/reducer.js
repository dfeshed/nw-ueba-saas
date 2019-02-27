import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';
import { normalize } from 'normalizr';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';
import { fileListSchema } from './schema';

const fileListState = Immutable.from({
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
  activeDataSourceTab: 'FILE_DETAILS',
  selectedFileList: [],
  fileData: {},
  agentCountMapping: {},
  fileStatusData: {},
  hostNameList: [],
  fetchHostNameListError: false,
  fetchMetaValueLoading: false,
  isRemediationAllowed: true,
  selectedFile: {},
  selectedDetailFile: null,
  selectedIndex: -1,
  downloadLink: null,
  machineFilePathInfoList: []
});
const LOADING_STATUS = 'loading';

const _handleAppendFiles = (action) => {
  return (state) => {
    const { payload: { data } } = action;
    const { fileData } = state;
    const normalizedData = normalize(action.payload.data.items, [fileListSchema]);
    const { file } = normalizedData.entities;
    return state.merge({
      fileData: { ...fileData, ...file },
      totalItems: data.totalItems,
      pageNumber: data.pageNumber,
      areFilesLoading: 'completed',
      loadMoreStatus: data.hasNext ? 'stopped' : 'completed',
      hasNext: data.hasNext
    });
  };
};

const _toggleSelectedFile = (state, payload) => {
  const { selectedFileList } = state;
  const { id, firstFileName, signature, size, machineOsType, checksumSha256, checksumSha1, checksumMd5, features, format, downloadInfo, serviceId } = payload;
  let selectedList = [];
  // Previously selected file

  if (selectedFileList.some((file) => file.id === id)) {
    selectedList = selectedFileList.filter((file) => file.id !== id);
  } else {
    selectedList = [...selectedFileList,
      { id, fileName: firstFileName, machineOsType, checksumSha256, checksumSha1, checksumMd5, signature, size, features, format, downloadInfo, serviceId }];
  }
  return state.merge({ 'selectedFileList': selectedList, 'fileStatusData': {}, isRemediationAllowed: true });

};
const fileListReducer = handleActions({

  [ACTION_TYPES.INITIALIZE_FILE_DETAIL]: (state, action) => {
    const data = action.payload ? action.payload.data : null;
    const selectedFileProperties = data ? data[0] : {};
    return state.set('selectedDetailFile', selectedFileProperties);
  },

  [ACTION_TYPES.FETCH_ALL_FILES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({
        fileData: {},
        areFilesLoading: 'wait',
        totalItems: 0,
        selectedFileList: [],
        selectedIndex: -1,
        selectedDetailFile: null
      }),
      failure: (s) => s.set('hostFetchStatus', 'error'),
      success: _handleAppendFiles(action)
    });
  },

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

  [ACTION_TYPES.GET_FILE_STATUS_HISTORY]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('selectedFileStatusHistory', action.payload.data)
    });
  },
  [ACTION_TYPES.SET_SORT_BY]: (state, { payload: { sortField, isSortDescending } }) => state.merge({
    sortField,
    isSortDescending
  }),

  [ACTION_TYPES.GET_LIST_OF_SERVICES]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('listOfServices', action.payload.data)
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

  [ACTION_TYPES.SAVE_FILE_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s, action) => {
        const { fileData } = s;
        const { payload: { request: { data } } } = action;
        const { checksums, fileStatus } = data;
        let newData;
        for (let i = 0; i < checksums.length; i++) {
          const file = fileData[checksums[i]];
          newData = { ...file, fileStatus, fileStatusData: data };
          s = s.setIn(['fileData', `${checksums[i]}`], newData);
        }
        return s;
      }
    });
  },

  [ACTION_TYPES.INCREMENT_PAGE_NUMBER]: (state) => state.set('pageNumber', state.pageNumber + 1),

  [ACTION_TYPES.SET_SELECTED_FILE]: (state, { payload }) => state.set('selectedFile', payload),

  [ACTION_TYPES.SET_AGENT_COUNT]: (state, { payload }) => state.set('agentCountMapping', { ...state.agentCountMapping, ...payload }),

  [ACTION_TYPES.RESET_DOWNLOAD_ID]: (state) => state.set('downloadId', null),

  [ACTION_TYPES.AGENT_COUNT_INIT]: (state, { payload }) => {
    const data = {};
    payload.forEach((checksum) => {
      data[checksum] = LOADING_STATUS;
    });
    return state.set('agentCountMapping', { ...state.agentCountMapping, ...data });
  },

  [ACTION_TYPES.FETCH_HOST_NAME_LIST_ERROR]: (state) => state.set('fetchHostNameListError', true),

  [ACTION_TYPES.INIT_FETCH_HOST_NAME_LIST]: (state) => state.merge({ fetchHostNameListError: false, fetchMetaValueLoading: true }),

  [ACTION_TYPES.SET_HOST_NAME_LIST]: (state, { payload }) => state.set('hostNameList', payload),

  [ACTION_TYPES.TOGGLE_SELECTED_FILE]: (state, { payload }) => _toggleSelectedFile(state, payload),

  [ACTION_TYPES.SELECT_ALL_FILES]: (state) => {
    const selectedList = Object.values(state.fileData).map((file) => {
      const { id, firstFileName, signature, size, checksumSha256, checksumSha1, checksumMd5, machineOsType, pe, format, downloadInfo, serviceId } = file;
      const features = pe ? pe.features : [];
      return { id, fileName: firstFileName, checksumSha256, checksumSha1, checksumMd5, signature, size, machineOsType, features, format, downloadInfo, serviceId };
    });
    return state.set('selectedFileList', selectedList);
  },

  [ACTION_TYPES.DESELECT_ALL_FILES]: (state) => state.set('selectedFileList', []),

  [ACTION_TYPES.META_VALUE_COMPLETE]: (state) => state.set('fetchMetaValueLoading', false),

  [ACTION_TYPES.FETCH_REMEDIATION_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        return s.set('isRemediationAllowed', action.payload.data);
      }
    });
  },

  [ACTION_TYPES.SET_SELECTED_INDEX]: (state, { payload }) => state.set('selectedIndex', payload),

  [SHARED_ACTION_TYPES.SET_DOWNLOAD_FILE_LINK]: (state, { payload }) => state.set('downloadLink', payload),

  [ACTION_TYPES.SET_MACHINE_FILE_PATH_LIST]: (state, { payload }) => state.set('machineFilePathInfoList', payload),

  [ACTION_TYPES.RESET_FILES]: (state) => state.merge({
    areFilesLoading: 'wait',
    loadMoreStatus: 'completed',
    pageNumber: -1,
    totalItems: 0,
    hasNext: false,
    downloadStatus: 'completed',
    downloadId: null,
    activeDataSourceTab: 'FILE_DETAILS',
    selectedFileList: [],
    fileData: {},
    agentCountMapping: {},
    fileStatusData: {},
    hostNameList: [],
    fetchHostNameListError: false,
    fetchMetaValueLoading: false,
    isRemediationAllowed: true,
    selectedFile: {},
    selectedDetailFile: null,
    selectedIndex: -1,
    downloadLink: null,
    machineFilePathInfoList: []
  })
}, fileListState);

export default fileListReducer;
