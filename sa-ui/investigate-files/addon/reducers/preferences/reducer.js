import * as ACTION_TYPES from 'investigate-files/actions/types';
import reduxActions from 'redux-actions';
import Immutable from 'seamless-immutable';
import { convertPixelToVW } from 'investigate-shared/utils/common-util';

const key = ['preferences', 'filePreference', 'columnConfig'];

function _updateConfig(tableId, updated, columnConfig) {
  let data = [];
  const updatedConfig = { tableId, columns: updated };
  if (columnConfig && columnConfig.length) {
    const index = columnConfig.findIndex((col) => col.tableId === tableId);
    if (index > -1) {
      data = columnConfig.set(index, updatedConfig);
    } else {
      data = columnConfig.concat([updatedConfig]);
    }
  } else {
    data.push(updatedConfig);
  }
  return data;
}

const filePreferencesInitialState = Immutable.from({
  preferences: {
    machinePreference: {
      sortField: '{ "key": "score", "descending": true }'
    }
  }
});

const filePreferences = reduxActions.handleActions({

  [ACTION_TYPES.SET_SORT_BY]: (state, { payload }) => state.setIn(['preferences', 'filePreference', 'sortField' ], JSON.stringify(payload)),

  [ACTION_TYPES.SAVE_COLUMN_CONFIG]: (state, { payload }) => {
    const { columns, tableId } = payload;
    const columnConfig = state.getIn(key);
    const updated = columns.sortBy('preferredDisplayIndex').map((column, index) => {
      const { width, field } = column;
      return { field, width: convertPixelToVW(width), displayIndex: index };
    });
    const data = _updateConfig(tableId, updated, columnConfig);
    return state.setIn(key, data);
  },

  [ACTION_TYPES.SET_FILE_PREFERENCES]: (state, { payload }) => {
    return state.set('preferences', payload);
  }
}, filePreferencesInitialState);

export default filePreferences;
