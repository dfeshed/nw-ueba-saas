import reselect from 'reselect';
const { createSelector } = reselect;
const _snapShots = (state) => state.endpoint.detailsInput.snapShots;

export const hasScanTime = createSelector(
  _snapShots,
  (snapShots) => snapShots && !!snapShots.length
);

