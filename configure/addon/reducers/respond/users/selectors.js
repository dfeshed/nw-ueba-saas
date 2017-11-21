import reselect from 'reselect';
import Immutable from 'seamless-immutable';

const { createSelector } = reselect;

const UNASSIGN_USER = { id: 'UNASSIGNED' };
const usersState = (state) => state.configure.respond.users;

export const getEnabledUsers = createSelector(
  usersState,
  (usersState) => usersState.enabledUsers
);

export const getAllUsers = createSelector(
  usersState,
  (usersState) => usersState.allUsers
);

export const getEnabledUsersStatus = createSelector(
  usersState,
  (usersState) => usersState.enabledUsersStatus
);

export const getAllUsersStatus = createSelector(
  usersState,
  (usersState) => usersState.allUsersStatus
);

export const getAssigneeOptions = createSelector(
  getEnabledUsers,
  (enabledUsers) => {
    const users = enabledUsers.asMutable();
    users.unshift(UNASSIGN_USER);
    return Immutable.from(users);
  }
);