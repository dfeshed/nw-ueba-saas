import reduxActions from 'redux-actions';
import { PRIORITY_TYPES } from 'respond/utils/priority-types';
import { STATUS_TYPES } from 'respond/utils/status-types';
import { handle } from 'redux-pack';

const initialState = {
  priorityTypes: PRIORITY_TYPES,
  statusTypes: STATUS_TYPES
};

export default reduxActions.handleActions({
  dictionaries: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, users: [], usersStatus: 'wait' }),
      failure: (s) => ({ ...s, usersStatus: 'error' }),
      success: (s) => ({ ...s, users: action.payload.data, usersStatus: 'completed' })
    });
  }
}, initialState);