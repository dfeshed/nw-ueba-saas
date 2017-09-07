import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-files/actions/types';

const schemaInitialState = {
  schema: null,
  schemaError: null,
  schemaLoading: true
};

const schemaReducer = handleActions({
  [ACTION_TYPES.SCHEMA_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, schemaLoading: true }),
      finish: (s) => ({ ...s, schemaLoading: false }),
      failure: (s) => ({ ...s, schemaError: true }),
      success: (s) => ({ ...s, schema: action.payload.data.fields })
    });
  }
}, schemaInitialState);

export default schemaReducer;
