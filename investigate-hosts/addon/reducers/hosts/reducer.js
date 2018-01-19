import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  // List of all the hosts, returned by server
  hostList: null,

  // Applied column sort on host list table
  hostColumnSort: [],

  selectedHostList: [],

  // State to indicate host retriving status from the server
  hostFetchStatus: 'wait',

  loadMoreHostStatus: 'stopped',

  // Host export related state
  hostExportStatus: 'completed',
  hostExportLinkId: null,

  totalItems: 0,

  hasNext: false,

  pageNumber: null,

  listOfServices: null

});

const _handleAppendMachines = (action) => {
  return (state) => {
    const { payload: { data } } = action;
    const { hostList } = state;
    return state.merge({
      hostList: [...hostList, ...data.items],
      pageNumber: data.pageNumber,
      loadMoreHostStatus: data.hasNext ? 'stopped' : 'completed',
      hasNext: data.hasNext
    });
  };
};

/**
 * One selection of machine row update the machine selected state. If selection type is 'all' toggle the state of all
 * all the machine. IF type is single toggle the single machine
 * @param state
 * @param payload
 * @private
 */
const _toggleMachineSelection = (state, payload) => {
  const { selectedHostList } = state;
  const { id, version } = payload;
  let selected = [];
  // Previously selected host
  if (selectedHostList.some((host) => host.id === id)) {
    selected = selectedHostList.filter((host) => host.id !== id);
  } else {
    selected = [...selectedHostList, { id, version }];
  }
  return state.set('selectedHostList', selected);
};
/**
 * Updating the agent status. Agent status is coming in as stream, so updating only matching agent id.
 * @param state
 * @param payload
 * @returns {{hostList: *, agentStatus: *}}
 * @private
 */
const _updateAgentStatus = (state, { payload }) => {
  const { hostList } = state;
  const list = hostList.map((item) => {
    const agentStatus = payload ? payload[item.id] : null;

    if (agentStatus) {
      return {
        ...item,
        agentStatus
      };
    } else {
      return item;
    }
  });
  return state.merge({ hostList: list });
};

const _toggleIconVisibility = (state, { id, flag }) => {
  const { hostList } = state;
  const items = hostList.map((item) => {
    return { item, showIcon: item.id === id && flag };
  });

  return state.set('hostList', items);
};

const hosts = reduxActions.handleActions({

  // On start loading of the schema reset the host list
  [ACTION_TYPES.FETCH_ALL_SCHEMAS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('hostList', [])
    });
  },
  [ACTION_TYPES.FETCH_ALL_MACHINES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ hostList: [], hostFetchStatus: 'wait', totalItems: 0, selectedHostList: [] }),
      failure: (s) => s.set('hostFetchStatus', 'error'),
      success: (s) => s.merge({
        hostList: action.payload.data.items,
        hostFetchStatus: 'completed',
        pageNumber: action.payload.data.pageNumber,
        totalItems: action.payload.data.totalItems,
        loadMoreHostStatus: (action.payload.data.hasNext) ? 'stopped' : 'completed'
      })
    });
  },

  [ACTION_TYPES.FETCH_NEXT_MACHINES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('loadMoreHostStatus', 'streaming'),
      failure: (s) => s.set('loadMoreHostStatus', 'error'),
      success: _handleAppendMachines(action)
    });
  },

  [ACTION_TYPES.FETCH_DOWNLOAD_JOB_ID]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('hostExportStatus', 'streaming'),
      success: (s) => s.merge({ hostExportLinkId: action.payload.data.id, hostExportStatus: 'completed' })
    });
  },

  [ACTION_TYPES.SET_SELECTED_HOST]: (state, { payload: { id, version } }) => state.set('selectedHostList', [{ id, version }]),

  [ACTION_TYPES.FETCH_AGENT_STATUS_STREAM_INITIALIZED]: (state, { payload }) => state.set('stopAgentStream', payload),

  [ACTION_TYPES.SET_HOST_COLUMN_SORT]: (state, { payload }) => state.set('hostColumnSort', [ payload ]),

  [ACTION_TYPES.USER_LEFT_HOST_LIST_PAGE]: (state) => state.set('hostExportLinkId', null),

  [ACTION_TYPES.FETCH_AGENT_STATUS]: (state, action) => _updateAgentStatus(state, action),

  [ACTION_TYPES.TOGGLE_MACHINE_SELECTED]: (state, { payload }) => _toggleMachineSelection(state, payload),

  [ACTION_TYPES.TOGGLE_ICON_VISIBILITY]: (state, { payload }) => _toggleIconVisibility(state, payload),

  [ACTION_TYPES.SELECT_ALL_HOSTS]: (state) => state.set('selectedHostList', state.hostList.map((host) => ({ id: host.id, version: host.machine.agentVersion }))),

  [ACTION_TYPES.DESELECT_ALL_HOSTS]: (state) => state.set('selectedHostList', []),

  [ACTION_TYPES.DELETE_HOSTS]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('hostList', [])
    });
  },

  [ACTION_TYPES.GET_LIST_OF_SERVICES]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('listOfServices', action.payload.data)
    });
  },

  [ACTION_TYPES.RESET_HOST_DOWNLOAD_LINK]: (state) => state.set('hostExportLinkId', null)

}, initialState);

export default hosts;

