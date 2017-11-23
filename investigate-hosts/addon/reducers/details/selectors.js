import reselect from 'reselect';
const { createSelector } = reselect;
const _snapShots = (state) => state.endpoint.detailsInput.snapShots;

export const hasScanTime = createSelector(
  _snapShots,
  (snapShots) => snapShots && !!snapShots.length
);

const _machineOsType = (state) => {
  if (state.endpoint.overview.hostDetails) {
    return state.endpoint.overview.hostDetails.machine.machineOsType;
  }
  return 'windows';
};

export const getColumnsConfig = (state, config) => config[_machineOsType(state)];
