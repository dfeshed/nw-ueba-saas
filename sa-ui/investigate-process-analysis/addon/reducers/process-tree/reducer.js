import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  queryInput: null,
  streaming: false,
  rawData: null,
  error: null,
  selectedProcess: null,
  path: [ '0' ],
  eventsSortField: null,
  eventsCount: 0,
  eventsFilteredCount: 0,
  filterApplied: false,
  fileProperty: {}
};


export default reduxActions.handleActions({

  [ACTION_TYPES.UPDATE_FILTER_ITEMS]: (state) => {
    return state.set('filterApplied', true);
  },
  [ACTION_TYPES.RESET_FILTER_ITEMS]: (state) => {
    return state.set('filterApplied', false);
  },
  [ACTION_TYPES.INIT_EVENTS_STREAMING]: (state) => {
    return state.merge({ streaming: true, error: null, rawData: [] });
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
  [ACTION_TYPES.SET_SERVER_ID]: (state, { payload }) => {
    let serverId = null;
    if (payload) {
      serverId = payload.split('nwe://')[1];
    }
    return state.set('selectedServerId', serverId);
  },
  [ACTION_TYPES.SET_SELECTED_EVENTS]: (state, { payload = [] }) => {
    const stateObj = { eventsData: payload, eventsFilteredCount: payload.length };
    if (!state.filterApplied) {
      stateObj.eventsCount = payload.length;
    }
    return state.merge(stateObj);
  },

  [ACTION_TYPES.SET_PROCESS_ANALYSIS_INPUT]: (state, { payload }) => {
    return state.merge({
      queryInput: payload,
      selectedServerId: payload.serverId
    });
  },

  [ACTION_TYPES.SET_SELECTED_PROCESS]: (state, { payload }) => {
    return state.set('selectedProcess', payload);
  },
  [ACTION_TYPES.SET_NODE_PATH]: (state, { payload }) => {
    return state.set('path', state.path.concat([payload]));
  },

  [ACTION_TYPES.SET_SORT_FIELD]: (state, action) => {
    return state.set('eventsSortField', action.payload);
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
  },

  [ACTION_TYPES.SET_EVENT_CATEGORY]: (state, { payload }) => {
    const { rawData } = state;
    const [network, file, registry] = payload.eventCategory;
    const networkLength = network.length;
    const fileLength = file.length;
    const registryLength = registry.length;
    if (networkLength || fileLength || registryLength) {
      const category = { pid: payload.pid, hasNetwork: networkLength, hasFile: fileLength, hasRegistry: registryLength };
      const newData = rawData.map((node) => {
        if (node.processId === payload.pid) {
          return { ...node, eventCategory: category };
        } else {
          return node;
        }
      });
      return state.set('rawData', newData);
    } else {
      return state;
    }
  },

  [ACTION_TYPES.SET_LOCAL_RISK_SCORE]: (state, { payload: { score } }) => {
    const { rawData } = state;
    const newData = rawData.map((data) => {
      let localScore = null;
      if (score) { // Error case score will be null
        localScore = 0; // Success case set the default value to 0
        const filter = score.filter((item) => {
          return item.id === data.checksumDst;
        });

        if (filter && filter.length) {
          localScore = filter[0].score;
        }
      }
      const newData = data.merge({ localScore });
      return newData;
    });
    return state.set('rawData', newData);
  },

  [ACTION_TYPES.GET_FILE_PROPERTY]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const [data] = action.payload.data && action.payload.data.length ? action.payload.data : [];
        return s.set('fileProperty', data);
      }
    });
  }

}, Immutable.from(initialState));
