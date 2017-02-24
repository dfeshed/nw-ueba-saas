import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  // incident details
  users: [],

  // either 'wait', 'error' or 'completed'
  usersStatus: null
};

export default reduxActions.handleActions({

  [ACTION_TYPES.FETCH_ALL_USERS]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, users: [], usersStatus: 'wait' }),
      failure: (s) => ({ ...s, usersStatus: 'error' }),
      success: (s) => ({ ...s, users: action.payload.data, usersStatus: 'completed' })
    });
  }

}, initialState);