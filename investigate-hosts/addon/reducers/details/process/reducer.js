import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import Immutable from 'seamless-immutable';


const updateTreeData = (data, checksums, fileStatus) => {
  return data.map((d) => {
    if (checksums.includes(d.fileProperties.checksumSha256)) {
      d = d.setIn(['fileProperties', 'fileStatus'], fileStatus);
    }
    if (d.childProcesses) {
      const children = updateTreeData(d.childProcesses, checksums, fileStatus);
      d = d.set('childProcesses', children);
    }
    return d;
  });
};


const initialState = Immutable.from({
  processList: null,
  // In list view, process view can be sorted based on processName, pid. By default, we fetch based on processName in ascending order.
  sortField: 'fileProperties.score',
  isDescOrder: true,

  processTree: null,
  processDetails: null,

  processDetailsLoading: false,
  isProcessTreeLoading: false,
  selectedProcessList: [],
  selectedRowIndex: null,
  selectedDllItem: null,
  selectedDllRowIndex: -1,
  agentCountMapping: {}
});
const LOADING_STATUS = 'loading';

const processReducer = handleActions({

  [ACTION_TYPES.RESET_HOST_DETAILS]: (state) => state.merge(initialState),

  [ACTION_TYPES.SET_PROCESS_DLL_ROW_ID]: (state, action) => state.set('selectedDllRowIndex', action.payload),

  [ACTION_TYPES.RESET_PROCESS_LIST]: (state) => state.set('processList', null),

  [ACTION_TYPES.TOGGLE_PROCESS_DETAILS_ROW]: (state, action) => state.set('selectedDllItem', action.payload),

  [ACTION_TYPES.SET_SORT_BY]: (state, { payload: { sortField, isDescOrder } }) => {
    return state.merge({ sortField, isDescOrder });
  },

  [ACTION_TYPES.GET_PROCESS_LIST]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ processDetails: null, isProcessTreeLoading: true }),
      success: (s) => s.merge({ processList: action.payload.data, isProcessTreeLoading: false })
    });
  },

  [ACTION_TYPES.GET_PROCESS_TREE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ processDetails: null, isProcessTreeLoading: true }),
      success: (s) => s.merge({ processTree: action.payload.data, isProcessTreeLoading: false })
    });
  },

  [ACTION_TYPES.GET_PROCESS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('processDetailsLoading', true),
      success: (s) => s.merge({ processDetails: action.payload.data, processDetailsLoading: false })
    });
  },

  [ACTION_TYPES.GET_PROCESS_FILE_CONTEXT]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('dllList', null),
      success: (s) => s.set('dllList', action.payload.data)
    });
  },

  [ACTION_TYPES.SET_SELECTED_PROCESS_ID]: (state, action) => state.set('selectedProcessId', action.payload),

  [ACTION_TYPES.SET_SELECTED_PROCESS]: (state, action) => {
    const { selectedProcessList } = state;
    const { pid, name, fileProperties, path, parentPid, vpid, hasChild } = action.payload;
    const { score, fileStatus, signature, size, checksumSha256, checksumSha1, checksumMd5, downloadInfo = {}, machineOsType, format, pe } = fileProperties;

    const features = pe ? pe.features : [];
    let selectedList = [];
    if (selectedProcessList.some((process) => process.pid === pid)) {
      selectedList = selectedProcessList.filter((process) => process.pid !== pid);
    } else {
      selectedList = [...selectedProcessList,
        {
          machineOsType,
          downloadInfo,
          format,
          features,
          checksumSha256,
          checksumSha1,
          checksumMd5,
          name,
          fileName: name,
          pid,
          id: pid,
          parentPid,
          vpid,
          path,
          signature,
          size,
          hasChild,
          score,
          fileStatus
        }
      ];
    }
    return state.merge({ 'selectedProcessList': selectedList });
  },

  [ACTION_TYPES.SELECT_ALL_PROCESS]: (state) => {
    const { selectedProcessList, processList } = state;
    if (selectedProcessList.length < processList.length) {
      return state.set('selectedProcessList', processList.map((process) => {
        const { pid, name, fileProperties, path, parentPid, vpid, hasChild } = process;
        const { score, fileStatus, signature, size, checksumSha256, checksumSha1, checksumMd5, downloadInfo = {}, machineOsType, format, pe } = fileProperties;
        const features = pe ? pe.features : [];

        return {
          machineOsType,
          downloadInfo,
          format,
          features,
          checksumSha256,
          checksumSha1,
          checksumMd5,
          name,
          fileName: name,
          pid,
          id: pid,
          parentPid,
          vpid,
          path,
          signature,
          size,
          hasChild,
          score,
          fileStatus };
      }));
    } else {
      return state.set('selectedProcessList', []);
    }
  },

  [ACTION_TYPES.DESELECT_ALL_PROCESS]: (state) => state.set('selectedProcessList', []),

  [ACTION_TYPES.SET_ROW_INDEX]: (state, action) => state.set('selectedRowIndex', action.payload),

  [ACTION_TYPES.CHANGE_DETAIL_TAB]: (state) => {
    return state.set('selectedRowIndex', null);
  },

  [ACTION_TYPES.SAVE_FILE_CONTEXT_FILE_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s, action) => {
        const { processList = [], processTree = [] } = s;
        if (processList && processList.length) {
          const { payload: { request: { data } } } = action;
          const { fileStatus, checksums } = data;
          const newProcessList = processList.map((process) => {
            if (checksums.includes(process.checksumSha256)) {
              process = process.setIn(['fileProperties', 'fileStatus'], fileStatus);
            }
            return process;
          });
          const treeData = updateTreeData(processTree, checksums, fileStatus);
          return s.merge({ processList: newProcessList, processTree: treeData });
        }
        return s;
      }
    });
  },

  [ACTION_TYPES.TOGGLE_PROCESS_VIEW]: (state) => state.merge({ selectedRowIndex: -1, selectedProcessList: [] }),

  [ACTION_TYPES.AGENT_COUNT_INIT]: (state, { payload }) => {
    const data = {};
    payload.forEach((checksum) => {
      data[checksum] = LOADING_STATUS;
    });
    return state.set('agentCountMapping', { ...state.agentCountMapping, ...data });
  },

  [ACTION_TYPES.SET_AGENT_COUNT]: (state, { payload }) => {
    return state.set('agentCountMapping', { ...state.agentCountMapping, ...payload });
  }

}, initialState);

export default processReducer;
