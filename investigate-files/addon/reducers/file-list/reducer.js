import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';
import { normalize } from 'normalizr';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import { contextDataParser } from 'investigate-shared/helpers/context-parser';
import { fileListSchema } from './schema';

const fileListState = Immutable.from({
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
  activeDataSourceTab: 'RISK_PROPERTIES',
  lookupData: [{}],
  contextError: null,
  contextLoadingStatus: 'wait',
  selectedFileList: [],
  fileData: {},
  agentCountMapping: {},
  fileStatusData: {},
  hostNameList: [],
  fetchHostNameListError: false,
  fetchMetaValueLoading: false,
  activeAlertTab: 'critical',
  alertsData: null,
  alertsError: null,
  isRemediationAllowed: true
});

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
      loadMoreStatus: data.hasNext ? 'stopped' : 'completed',
      hasNext: data.hasNext
    });
  };
};

const _toggleSelectedFile = (state, payload) => {
  const { selectedFileList } = state;
  const { id, firstFileName, signature, size, machineOsType, checksumSha256, checksumSha1, checksumMd5 } = payload;
  let selectedList = [];
  // Previously selected file

  if (selectedFileList.some((file) => file.id === id)) {
    selectedList = selectedFileList.filter((file) => file.id !== id);
  } else {
    selectedList = [...selectedFileList, { id, fileName: firstFileName, machineOsType, checksumSha256, checksumSha1, checksumMd5, signature, size }];
  }
  return state.merge({ 'selectedFileList': selectedList, 'fileStatusData': {} });

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

  [ACTION_TYPES.GET_FILE_STATUS_HISTORY]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('selectedFileStatusHistory', action.payload.data)
    });
  },
  [ACTION_TYPES.SET_SORT_BY]: (state, { payload: { sortField, isSortDescending } }) => state.merge({
    sortField,
    areFilesLoading: 'sorting',
    isSortDescending
  }),

  [ACTION_TYPES.RESET_FILES]: (state) => state.merge({
    fileData: {},
    pageNumber: -1,
    totalItems: 0,
    areFilesLoading: 'sorting'
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

  [ACTION_TYPES.GET_ALERTS_DATA]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('alertsData', action.payload.data),
      failure: (s) => s.set('alertsError', action.payload.meta)
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

  [ACTION_TYPES.SET_AGENT_COUNT]: (state, { payload }) => state.set('agentCountMapping', { ...state.agentCountMapping, ...payload }),

  [ACTION_TYPES.RESET_DOWNLOAD_ID]: (state) => state.set('downloadId', null),

  [ACTION_TYPES.CHANGE_DATASOURCE_TAB]: (state, { payload: { tabName } }) => state.set('activeDataSourceTab', tabName),

  [ACTION_TYPES.SET_CONTEXT_DATA]: (state, { payload }) => {
    const lookupData = [].concat(contextDataParser([payload, state.lookupData]));
    return state.merge({ lookupData, contextLoadingStatus: 'completed' });
  },

  [ACTION_TYPES.CLEAR_PREVIOUS_CONTEXT]: (state) => state.merge({ lookupData: [{}], contextLoadingStatus: 'wait' }),

  [ACTION_TYPES.CONTEXT_ERROR]: (state, { payload }) => state.set('contextError', payload),

  [ACTION_TYPES.FETCH_HOST_NAME_LIST_ERROR]: (state) => state.set('fetchHostNameListError', true),

  [ACTION_TYPES.INIT_FETCH_HOST_NAME_LIST]: (state) => state.merge({ fetchHostNameListError: false, fetchMetaValueLoading: true }),

  [ACTION_TYPES.SET_HOST_NAME_LIST]: (state, { payload }) => state.set('hostNameList', payload),

  [ACTION_TYPES.TOGGLE_SELECTED_FILE]: (state, { payload }) => _toggleSelectedFile(state, payload),

  [ACTION_TYPES.SELECT_ALL_FILES]: (state) => {
    const selectedList = Object.values(state.fileData).map((file) => {
      const { id, firstFileName, signature, size, checksumSha256, checksumSha1, checksumMd5, machineOsType } = file;
      return { id, fileName: firstFileName, checksumSha256, checksumSha1, checksumMd5, signature, size, machineOsType };
    });
    return state.set('selectedFileList', selectedList);
  },

  [ACTION_TYPES.DESELECT_ALL_FILES]: (state) => state.set('selectedFileList', []),

  [ACTION_TYPES.META_VALUE_COMPLETE]: (state) => state.set('fetchMetaValueLoading', false),

  [ACTION_TYPES.CHANGE_ALERT_TAB]: (state, { payload: { tabName } }) => {
    return state.set('activeAlertTab', tabName);
  },

  [ACTION_TYPES.FETCH_REMEDIATION_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        return s.set('isRemediationAllowed', action.payload.data);
      }
    });
  }
}, fileListState);

export default fileListReducer;
