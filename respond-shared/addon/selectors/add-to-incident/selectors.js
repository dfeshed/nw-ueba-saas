import reselect from 'reselect';

const { createSelector } = reselect;

const incidentSearchParamsState = (state) => state.respondShared.incidentSearchParams;

export const getIncidentSearchStatus = createSelector(
  incidentSearchParamsState,
  (searchState) => searchState.incidentSearchStatus
);

export const getIncidentSearchResults = createSelector(
  incidentSearchParamsState,
  (searchState) => searchState.incidentSearchResults
);

export const getSelectedIncident = createSelector(
  incidentSearchParamsState,
  (searchState) => searchState.selectedIncident
);

export const getIncidentSearchSortBy = createSelector(
  incidentSearchParamsState,
  (searchState) => searchState.incidentSearchSortBy
);

export const getIncidentSearchSortIsDescending = createSelector(
  incidentSearchParamsState,
  (searchState) => {
    return searchState.incidentSearchSortIsDescending;
  }
);

export const getIncidentSearchText = createSelector(
  incidentSearchParamsState,
  (searchState) => searchState.incidentSearchText
);

export const hasSearchQuery = createSelector(
  getIncidentSearchText,
  (searchText) => searchText && searchText.length >= 3
);

export const getIsAddAlertsInProgress = createSelector(
  incidentSearchParamsState,
  (searchState) => searchState.isAddToIncidentInProgress
);

export const getIsIncidentNotSelected = createSelector(
  getIsAddAlertsInProgress,
  getSelectedIncident,
  (isAddToAlertsInProgress, selectedIncident) => isAddToAlertsInProgress || !selectedIncident
);