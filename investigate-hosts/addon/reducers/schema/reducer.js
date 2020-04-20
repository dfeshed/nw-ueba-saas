import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

const schemaInitialState = Immutable.from({
  schema: null,
  schemaLoading: true,
  preferences: { machinePreference: null, filePreference: null }
});

const schemas = reduxActions.handleActions({

  [ACTION_TYPES.FETCH_ALL_SCHEMAS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('schemaLoading', true),
      finish: (s) => s.set('schemaLoading', false),
      success: (s) => s.set('schema', action.payload.data.fields)
    });
  },
  [ACTION_TYPES.USER_LEFT_HOST_LIST_PAGE]: (state) => state.set('schema', null)

}, schemaInitialState);

export default schemas;
