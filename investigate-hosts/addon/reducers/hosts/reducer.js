import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import _ from 'lodash';
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

  totalItems: null,

  pageNumber: null

});

const _handleAppendMachines = (action) => {
  return (state) => {
    const { payload: { data } } = action;
    const { hostList } = state;
    return state.merge({
      hostList: [...hostList, ...data.items],
      pageNumber: data.pageNumber,
      loadMoreHostStatus: (data.hasNext) ? 'stopped' : ''
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
  const { id } = payload;
  let selected = [];
  // Previously selected host
  if (selectedHostList.includes(id)) {
    selected = selectedHostList.filter((item) => item !== id);
  } else {
    selected = [...selectedHostList, id];
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
    if (item.id === payload.agentId) {
      return {
        ...item,
        agentStatus: payload
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
      start: (s) => s.merge({ hostList: [], hostFetchStatus: 'wait', selectedHostList: [] }),
      failure: (s) => s.set('hostFetchStatus', 'error'),
      success: (s) => s.merge({
        hostList: action.payload.data.items,
        hostFetchStatus: 'completed',
        pageNumber: action.payload.data.pageNumber,
        totalItems: action.payload.data.totalItems,
        loadMoreHostStatus: (action.payload.data.hasNext) ? 'stopped' : ''
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

  [ACTION_TYPES.SET_SELECTED_HOST]: (state, { payload }) => state.set('selectedHostList', [payload.id]),

  [ACTION_TYPES.FETCH_AGENT_STATUS_STREAM_INITIALIZED]: (state, { payload }) => state.set('stopAgentStream', payload),

  [ACTION_TYPES.SET_HOST_COLUMN_SORT]: (state, { payload }) => state.set('hostColumnSort', [ payload ]),

  [ACTION_TYPES.USER_LEFT_HOST_LIST_PAGE]: (state) => state.set('hostExportLinkId', null),

  [ACTION_TYPES.FETCH_AGENT_STATUS]: (state, action) => _updateAgentStatus(state, action),

  [ACTION_TYPES.TOGGLE_MACHINE_SELECTED]: (state, { payload }) => _toggleMachineSelection(state, payload),

  [ACTION_TYPES.TOGGLE_ICON_VISIBILITY]: (state, { payload }) => _toggleIconVisibility(state, payload),

  [ACTION_TYPES.SELECT_ALL_HOSTS]: (state) => state.set('selectedHostList', _.map(state.hostList, 'id')),

  [ACTION_TYPES.DESELECT_ALL_HOSTS]: (state) => state.set('selectedHostList', [])

}, initialState);

export default hosts;

