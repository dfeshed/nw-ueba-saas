import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

const preferencesInitialState = Immutable.from({
  preferences: {
    machinePreference: null,
    filePreference: null
  }
});
const DEFAULT_HOSTS_PREFERENCE = {
  machinePreference: {
    visibleColumns: [
      'machine.machineOsType',
      'machine.scanStartTime',
      'machine.users.name',
      'agentStatus.lastSeenTime',
      'agentStatus.scanStatus'
    ],
    sortField: 'machine.scanStartTime:desc'
  }
};

const _handleGetPreferences = ({ payload }) => {
  return (state) => {
    let preferences = payload.data;
    if (!preferences) {
      preferences = DEFAULT_HOSTS_PREFERENCE;
    } else if (!preferences.machinePreference) {
      preferences = { ...preferences, ...DEFAULT_HOSTS_PREFERENCE };
    }
    return state.set('preferences', preferences);
  };
};

const preferences = reduxActions.handleActions({

  [ACTION_TYPES.SET_HOST_COLUMN_SORT]: (state, { payload }) => state.setIn(['preferences', 'machinePreference', 'sortField' ], JSON.stringify(payload)),

  [ACTION_TYPES.UPDATE_COLUMN_VISIBILITY]: (state, { payload }) => {
    const key = ['preferences', 'machinePreference', 'visibleColumns'];
    const visibleColumns = state.getIn(key);
    const { selected, field } = payload;
    if (selected) {
      const updatedVisibleColumns = visibleColumns.concat([field]);
      return state.setIn(key, updatedVisibleColumns.uniq());
    } else {
      const newColumns = visibleColumns.filter((column) => column !== field);
      return state.setIn(key, newColumns.uniq());
    }
  },

  [ACTION_TYPES.GET_PREFERENCES]: (state, action) => {
    return handle(state, action, {
      success: _handleGetPreferences(action)
    });
  }
}, preferencesInitialState);

export default preferences;
