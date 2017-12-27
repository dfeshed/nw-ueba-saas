import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';
import CONFIG from './config';

import * as ACTION_TYPES from 'investigate-files/actions/types';

const schemaInitialState = Immutable.from({
  schema: null,
  schemaLoading: true,
  preferences: { machinePreference: null, filePreference: null }
});

const _handleGetPreferences = (action) => {
  return (state) => {
    const { payload } = action;
    const visibleColumns = payload && payload.filePreference ?
      action.payload.filePreference.visibleColumns : CONFIG.defaultPreferences.filePreference.visibleColumns;
    return state.merge({ schema:
      state.schema.map((field) => {
        return {
          ...field,
          visible: visibleColumns.includes(field.name)
        };
      }),
      preferences: payload ? payload : state.preferences
    });
  };
};

const schemaReducer = handleActions({
  [ACTION_TYPES.SCHEMA_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('schemaLoading', true),
      finish: (s) => s.set('schemaLoading', false),
      success: (s) => s.set('schema', action.payload.data.fields)
    });
  },

  [ACTION_TYPES.GET_PREFERENCES]: (state, action) => {
    return handle(state, action, {
      success: _handleGetPreferences(action)
    });
  },

  [ACTION_TYPES.UPDATE_COLUMN_VISIBILITY]: (state, { payload }) => state.merge({
    schema: state.schema.map((field) => {
      let { visible } = field;
      if (field.name === payload.field) {
        visible = !payload.visible;
      }
      return {
        ...field,
        visible
      };
    })
  }),

  [ACTION_TYPES.SET_VISIBLE_COLUMNS]: (state, { payload }) => state.set('visibleColumns', payload)
}, schemaInitialState);

export default schemaReducer;
