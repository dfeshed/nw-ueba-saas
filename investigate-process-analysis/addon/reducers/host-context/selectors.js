import reselect from 'reselect';

const { createSelector } = reselect;

export const _listOfHostNames = (state) => state.processAnalysis.hostContext.hostList;
export const listOfHostNames = createSelector(
  _listOfHostNames,
  (hostList = {}) => {
    return hostList.data;
  }
);

export const hostCount = createSelector(
  listOfHostNames,
  (listOfHostNames = []) => {
    return listOfHostNames.length;
  }
);
