import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-shared/actions/types/endpoint';
import reduxActions from 'redux-actions';

const initialState = {
  queryInput: null,
  streaming: false,
  rawData: [],
  rootNode: null,
  error: null
};

const _rootNode = (rootNode, rawData, processName, checksum, agentId) => {
  return {
    processName,
    checksum,
    agentId,
    children: rawData
  };
};

export default reduxActions.handleActions({

  [ACTION_TYPES.INIT_EVENTS_STREAMING]: (state) => {
    return state.merge({ streaming: true, error: null });
  },

  [ACTION_TYPES.COMPLETED_EVENTS_STREAMING]: (state) => {
    const { processName, agentId, checksum } = state.queryInput;
    const { rawData, rootNode } = state;
    return state.merge({ streaming: false, rootNode: _rootNode(rootNode, rawData, processName, checksum, agentId) });
  },

  [ACTION_TYPES.SET_EVENTS_PAGE_ERROR]: (state, { payload }) => {
    return state.merge(payload);
  },

  [ACTION_TYPES.SET_EVENTS]: (state, { payload }) => {
    return state.set('rawData', payload);
  },

  [ACTION_TYPES.SET_PROCESS_ANALYSIS_INPUT]: (state, { payload }) => {
    return state.set('queryInput', payload);
  }
}, Immutable.from(initialState));
