import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import Immutable from 'seamless-immutable';

const DEFAULT_HOSTS_PREFERENCE = {
  machinePreference: {
    visibleColumns: [
      'machine.machineOsType',
      'machine.scanStartTime',
      'machine.users.name',
      'agentStatus.lastSeenTime',
      'agentStatus.scanStatus'
    ],
    sortField: '{ "key": "machine.scanStartTime", "descending": true }'
  }
};

const preferencesInitialState = Immutable.from({
  preferences: DEFAULT_HOSTS_PREFERENCE
});

const preferences = reduxActions.handleActions({

  [ACTION_TYPES.SET_HOST_COLUMN_SORT]: (state, { payload }) => state.setIn(['preferences', 'machinePreference', 'sortField' ], JSON.stringify(payload)),

  [ACTION_TYPES.UPDATE_COLUMN_VISIBILITY]: (state, { payload }) => {
    const key = ['preferences', 'machinePreference', 'visibleColumns'];
    const visibleColumns = state.getIn(key);
    const { selected, field } = payload;
    if (selected) {
      const updatedVisibleColumns = visibleColumns.concat([field]);
      return state.setIn(key, updatedVisibleColumns);
    } else {
      const newColumns = visibleColumns.filter((column) => column !== field);
      return state.setIn(key, newColumns);
    }
  },

  [ACTION_TYPES.SET_PREFERENCES]: (state, { payload }) => {
    return state.set('preferences', payload);
  }
}, preferencesInitialState);

export default preferences;
