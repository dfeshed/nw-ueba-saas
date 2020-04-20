import reselect from 'reselect';

const { createSelector } = reselect;

const usersState = (state) => state.respond.users;

export const getAllUsers = createSelector(
  usersState,
  (usersState) => usersState.allUsers
);

export const getAllUsersStatus = createSelector(
  usersState,
  (usersState) => usersState.allUsersStatus
);
