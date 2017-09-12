import Immutable from 'seamless-immutable';
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

/**
 * The user object returned from the service has a property named 'disabled', which can cause issues with UI
 * components (e.g., ember power select), which interprets that property in unanticipated ways. The ember power select
 * component, for example, disables the user entry in the dropdown list because of the presence of this property, even
 * when that behavior is not desired.
 *
 * This function remaps the 'disabled' property onto a new property 'isInactive', and deletes the original property to
 * avoid this name collision.
 * @private
 * @param users
 */
const remapDisabledProperty = (users) => {
  return users.map((user) => {
    const isDisabled = !!user.disabled;
    const newUser = { ...user, isInactive: isDisabled };
    delete newUser.disabled;
    return newUser;
  });
};

export default reduxActions.handleActions({

  [ACTION_TYPES.FETCH_ALL_ENABLED_USERS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ enabledUsers: [], enabledUsersStatus: 'wait' }),
      failure: (s) => s.set('enabledUsersStatus', 'error'),
      success: (s) => s.merge({ enabledUsers: remapDisabledProperty(action.payload.data), enabledUsersStatus: 'completed' })
    });
  },

  [ACTION_TYPES.FETCH_ALL_USERS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ allUsers: [], allUsersStatus: 'wait' }),
      failure: (s) => s.set('allUsersStatus', 'error'),
      success: (s) => s.merge({ allUsers: remapDisabledProperty(action.payload.data), allUsersStatus: 'completed' })
    });
  }

}, Immutable.from(initialState));