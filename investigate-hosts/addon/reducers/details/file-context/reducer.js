import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { getSchema } from './schemas';
import { normalize } from 'normalizr';
import Immutable from 'seamless-immutable';
import _ from 'lodash';

const initialState = Immutable.from({
  fileContext: {}, // File context object , which holds the file information
  contextLoadingStatus: 'wait', // Indicates the files loading status
  contextLoadMoreStatus: null, // Status for paginated data
  selectedRowId: null, // Selected file context
  fileContextSelections: [], // file context selections, which includes, id, signature and size
  sortConfig: null, // Column sort configuration
  fileStatus: {}, // File status for selected file
  totalItems: null, // Total number of file context items
  hasNext: false,
  isRemediationAllowed: true,
  agentCountMapping: {},
  selectedRowIndex: null
});

const LOADING_STATUS = 'loading';

const _toggleSelection = (state, payload) => {
  const { fileContextSelections } = state;
  const { id, fileName, fileProperties, machineOsType, path, machineName } = payload;
  const { signature, size, checksumSha256, checksumSha1, checksumMd5, format, pe, downloadInfo = {} } = fileProperties;
  const features = pe ? pe.features : [];
  let selectedList = [];
  // Previously selected driver
  if (fileContextSelections.some((file) => file.id === id)) {
    selectedList = fileContextSelections.filter((file) => file.id !== id);
  } else {
    selectedList = [...fileContextSelections, { machineName, id, fileName, checksumSha1, checksumSha256, checksumMd5, signature, size, machineOsType, path, downloadInfo, features, format }];
  }
  return state.merge({ 'fileContextSelections': selectedList, 'fileStatus': {}, isRemediationAllowed: true });

};

const fileContext = reduxActions.handleActions({

  [ACTION_TYPES.RESET_CONTEXT_DATA]: (state) => state.merge(initialState),

  [ACTION_TYPES.SET_FILE_CONTEXT_ROW_SELECTION]: (state, { payload: { id, index } }) => state.merge({ selectedRowId: id, selectedRowIndex: index }),

  [ACTION_TYPES.SET_FILE_CONTEXT_COLUMN_SORT]: (s, { payload }) => {
    const { isDescending, field } = payload;
    return s.merge({ sortConfig: { isDescending, field }, selectedRowId: null, selectedRowIndex: null });
  },

  [ACTION_TYPES.FETCH_FILE_CONTEXT]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('contextLoadingStatus', 'wait'),
      success: (s) => {
        const contextType = action.meta.belongsTo;
        const normalizedData = normalize(action.payload.data, getSchema(contextType));
        const fileContext = normalizedData.entities[contextType];
        const totalItems = fileContext ? _.values(fileContext).length : 0;
        return s.merge({
          totalItems,
          fileContext,
          contextLoadingStatus: 'completed'
        });
      }
    });
  },

  [ACTION_TYPES.TOGGLE_FILE_CONTEXT_ROW_SELECTION]: (state, { payload }) => _toggleSelection(state, payload),

  [ACTION_TYPES.TOGGLE_FILE_CONTEXT_ALL_SELECTION]: (state) => {
    const { fileContext, fileContextSelections } = state;
    const contexts = Object.values(fileContext);
    if (fileContextSelections.length < contexts.length) {
      return state.set('fileContextSelections', contexts.map((driver) => {
        const { machineName, id, fileName, path, machineOsType, fileProperties: { signature, size, checksumSha256, checksumSha1, checksumMd5, format, pe, downloadInfo = {} } } = driver;
        const features = pe ? pe.features : [];
        return { machineName, id, fileName, checksumSha1, checksumSha256, checksumMd5, signature, size, path, downloadInfo, features, machineOsType, format };
      }));
    } else {
      return state.set('fileContextSelections', []);
    }
  },

  [ACTION_TYPES.DESELECT_FILE_CONTEXT_ALL_SELECTION]: (state) => state.set('fileContextSelections', []),

  [ACTION_TYPES.FILE_CONTEXT_RESET_SELECTION]: (state) => state.set('fileContextSelections', []),

  [ACTION_TYPES.SAVE_FILE_CONTEXT_FILE_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s, action) => {
        const { fileContext } = s;
        let { fileContextSelections } = s;
        const { payload: { request: { data } } } = action;
        const { fileStatus, checksums } = data;

        // top 100 files we can update at a time.
        if (fileContextSelections.length > 100) {
          fileContextSelections = fileContextSelections.slice(0, 100);
        }
        const updatedSelections = fileContextSelections.filter((selection) => {
          return checksums.includes(selection.checksumSha256);
        });
        for (let i = 0; i < updatedSelections.length; i++) {
          const { id } = updatedSelections[i];
          const obj = fileContext[id];
          const newData = obj.setIn(['fileProperties', 'fileStatus'], fileStatus);
          s = s.setIn(['fileContext', `${id}`], newData);
        }
        return s;
      }
    });
  },

  [ACTION_TYPES.GET_FILE_CONTEXT_FILE_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const [payLoadData] = action.payload.data;
        if (payLoadData && payLoadData.resultList.length) {
          return s.set('fileStatus', payLoadData.resultList[0].data);
        }
        return s;
      }
    });
  },

  [ACTION_TYPES.FETCH_REMEDIATION_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        return s.set('isRemediationAllowed', action.payload.data);
      }
    });
  },

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

export default fileContext;
