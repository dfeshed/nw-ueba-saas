import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  processList: null,
  // In list view, process view can be sorted based on processName, pid. By default, we fetch based on processName in ascending order.
  sortField: 'name',
  isDescOrder: false,

  processTree: null,
  processDetails: null,

  processDetailsLoading: false,
  isProcessTreeLoading: false,
  selectedProcessList: [],
  selectedRowIndex: null
});

const processReducer = handleActions({

  [ACTION_TYPES.RESET_HOST_DETAILS]: (state) => state.merge(initialState),

  [ACTION_TYPES.RESET_PROCESS_LIST]: (state) => state.set('processList', null),

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
    const { checksumSha256, name, pid, parentPid, hasChild, vpid } = action.payload;
    let selectedList = [];
    if (selectedProcessList.some((process) => process.pid === pid)) {
      selectedList = selectedProcessList.filter((process) => process.pid !== pid);
    } else {
      selectedList = [...selectedProcessList, { checksumSha256, name, pid, parentPid, hasChild, vpid }];
    }
    return state.merge({ 'selectedProcessList': selectedList });
  },

  [ACTION_TYPES.SELECT_ALL_PROCESS]: (state) => {
    const selectedList = Object.values(state.processList).map((process) => {
      const { checksumSha256, name, pid, parentPid, hasChild, vpid } = process;
      return { checksumSha256, name, pid, parentPid, hasChild, vpid };
    });
    return state.set('selectedProcessList', selectedList);
  },

  [ACTION_TYPES.DESELECT_ALL_PROCESS]: (state) => state.set('selectedProcessList', []),

  [ACTION_TYPES.SET_ROW_INDEX]: (state, action) => state.set('selectedRowIndex', action.payload),

  [ACTION_TYPES.CHANGE_DETAIL_TAB]: (state) => {
    return state.set('selectedRowIndex', null);
  }

}, initialState);

export default processReducer;
