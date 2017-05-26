import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  // all enabled users
  enabledUsers: [],

  // either 'wait', 'error' or 'completed'
  enabledUsersStatus: null,

  // all users (including enabled and disabled)
  allUsers: [],

  // either 'wait', 'error' or 'completed'
  allUsersStatus: null
};

export default reduxActions.handleActions({

  [ACTION_TYPES.FETCH_ALL_ENABLED_USERS]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, enabledUsers: [], enabledUsersStatus: 'wait' }),
      failure: (s) => ({ ...s, enabledUsersStatus: 'error' }),
      success: (s) => ({ ...s, enabledUsers: action.payload.data, enabledUsersStatus: 'completed' })
    });
  },

  [ACTION_TYPES.FETCH_ALL_USERS]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, allUsers: [], allUsersStatus: 'wait' }),
      failure: (s) => ({ ...s, allUsersStatus: 'error' }),
      success: (s) => ({ ...s, allUsers: action.payload.data, allUsersStatus: 'completed' })
    });
  }

}, initialState);