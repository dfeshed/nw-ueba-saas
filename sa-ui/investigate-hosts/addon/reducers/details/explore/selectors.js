import reselect from 'reselect';

const { createSelector } = reselect;
const _searchStatus = (state) => state.endpoint.explore.searchStatus;
const _hostDetails = (state) => state.endpoint.overview.hostDetails;

export const searchResultLoading = createSelector(
  _searchStatus,
  (searchStatus) => searchStatus === 'wait'
);

export const searchResultNotFound = createSelector(
  _searchStatus,
  (searchStatus) => searchStatus === 'complete'
);

export const hasValidID = createSelector(
  _hostDetails,
  (hostDetails) => hostDetails ? hostDetails.id : null
);

