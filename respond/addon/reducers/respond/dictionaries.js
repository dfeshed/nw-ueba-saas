import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  priorityTypes: [],
  statusTypes: []
};

export default reduxActions.handleActions({

  [ACTION_TYPES.FETCH_PRIORITY_TYPES]: (state, action) => (
    handle(state, action, {
      start: (s) => ({ ...s, priorityTypes: [] }),
      failure: (s) => ({ ...s, priorityTypes: [] }),
      success: (s) => ({ ...s, priorityTypes: action.payload.data }) }
    )
  ),

  [ACTION_TYPES.FETCH_STATUS_TYPES]: (state, action) => (
    handle(state, action, {
      start: (s) => ({ ...s, statusTypes: [] }),
      failure: (s) => ({ ...s, statusTypes: [] }),
      success: (s) => ({ ...s, statusTypes: action.payload.data }) }
    )
  )
}, initialState);