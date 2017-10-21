import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

const schemaInitialState = Immutable.from({
  schema: null,
  schemaLoading: true
});

const schemas = reduxActions.handleActions({

  [ACTION_TYPES.FETCH_ALL_SCHEMAS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('schemaLoading', true),
      finish: (s) => s.set('schemaLoading', false),
      success: (s) => s.set('schema', action.payload.data.fields)
    });
  },
  [ACTION_TYPES.UPDATE_COLUMN_VISIBILITY]: (state, { payload }) => state.merge({
    schema: state.schema.map((field) => {
      let { defaultProjection } = field;
      if (field.name === payload.field) {
        defaultProjection = !payload.visible;
      }
      return {
        ...field,
        defaultProjection
      };
    })
  })
}, schemaInitialState);

export default schemas;
