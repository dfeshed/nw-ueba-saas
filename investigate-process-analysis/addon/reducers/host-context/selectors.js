import reselect from 'reselect';

const { createSelector } = reselect;

export const _listOfHostNames = (state) => state.processAnalysis.hostContext.hostList;

export const listOfHostNames = createSelector(
  [_listOfHostNames],
  (hostNames = []) => {
    return hostNames.map((host) => {
      return host.value;
    });
  }
);

export const hostCount = createSelector(
  listOfHostNames,
  (listOfHostNames) => {
    return listOfHostNames.length;
  }
);
