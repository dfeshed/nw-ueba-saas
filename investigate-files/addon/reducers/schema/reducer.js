import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'investigate-files/actions/types';

const schemaInitialState = Immutable.from({
  schema: null,
  schemaLoading: true
});


const schemaReducer = handleActions({
  [ACTION_TYPES.SCHEMA_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('schemaLoading', true),
      finish: (s) => s.set('schemaLoading', false),
      success: (s) => s.set('schema', action.payload.data.fields)
    });
  }
}, schemaInitialState);

export default schemaReducer;
