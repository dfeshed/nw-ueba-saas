import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import reduxActions from 'redux-actions';

const initialState = {
  queryInput: null,
  streaming: false,
  rawData: null,
  error: null,
  selectedProcess: null,
  path: [ '0' ]
};


export default reduxActions.handleActions({

  [ACTION_TYPES.INIT_EVENTS_STREAMING]: (state) => {
    return state.merge({ streaming: true, error: null });
  },

  [ACTION_TYPES.COMPLETED_EVENTS_STREAMING]: (state) => {
    return state.merge({
      streaming: false
    });
  },

  [ACTION_TYPES.SET_EVENTS_PAGE_ERROR]: (state, { payload }) => {
    return state.merge(payload);
  },

  [ACTION_TYPES.SET_EVENTS]: (state, { payload = [] }) => {
    return state.set('rawData', payload);
  },

  [ACTION_TYPES.SET_PROCESS_ANALYSIS_INPUT]: (state, { payload }) => {
    return state.set('queryInput', payload);
  },

  [ACTION_TYPES.SET_SELECTED_PROCESS]: (state, { payload }) => {
    return state.set('selectedProcess', payload);
  },
  [ACTION_TYPES.SET_NODE_PATH]: (state, { payload }) => {
    return state.set('path', state.path.concat([payload]));
  },

  [ACTION_TYPES.SET_EVENTS_COUNT]: (state, { payload }) => {
    const { rawData } = state;
    const newData = rawData.map((data) => {
      if (payload && payload[data.processId]) {
        return data.set('childCount', payload[data.processId].data);
      }
      return data;
    });
    return state.set('rawData', newData);
  }


}, Immutable.from(initialState));
