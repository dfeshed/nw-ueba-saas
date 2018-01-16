import * as ACTION_TYPES from 'investigate-files/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

const DEFAULT_FILE_PREFERENCES = {
  filePreference: {
    visibleColumns: [
      'firstFileName',
      'firstSeenTime',
      'machineOsType',
      'signature.features',
      'size',
      'checksumSha256',
      'entropy'
    ],
    sortField: { sortField: 'firstSeenTime', isSortDescending: false }
  }
};

const filePreferencesInitialState = Immutable.from({
  preferences: {
    machinePreference: null,
    filePreference: null
  }
});


const _handleGetPreferences = ({ payload }) => {
  return (state) => {
    let preferences = payload.data;
    if (!preferences) {
      preferences = DEFAULT_FILE_PREFERENCES;
    } else if (!preferences.filePreference) {
      preferences = { ...preferences, ...DEFAULT_FILE_PREFERENCES };
    }
    return state.set('preferences', preferences);
  };
};

const filePreferences = reduxActions.handleActions({

  [ACTION_TYPES.SET_SORT_BY]: (state, { payload }) => state.setIn(['preferences', 'filePreference', 'sortField' ], JSON.stringify(payload)),

  [ACTION_TYPES.UPDATE_COLUMN_VISIBILITY]: (state, { payload }) => {
    const key = ['preferences', 'filePreference', 'visibleColumns'];
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

  [ACTION_TYPES.GET_FILE_PREFERENCES]: (state, action) => {
    return handle(state, action, {
      success: _handleGetPreferences(action)
    });
  }
}, filePreferencesInitialState);

export default filePreferences;
