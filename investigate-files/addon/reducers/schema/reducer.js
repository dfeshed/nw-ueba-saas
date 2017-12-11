import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'investigate-files/actions/types';

const schemaInitialState = Immutable.from({
  schema: null,
  schemaLoading: true,
  visibleColumns: []
});

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
      success: (s) => s.set('visibleColumns', action.payload.filePreference.visibleColumns)
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
