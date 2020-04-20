import reselect from 'reselect';

const { createSelector } = reselect;
const riacState = (state) => state.respond.riac;

export const isRiacEnabled = createSelector(
  riacState,
  (riacState) => (riacState.isRiacEnabled)
);

export const getAdminRoles = createSelector(
  riacState,
  (riacState) => (riacState.adminRoles)
);