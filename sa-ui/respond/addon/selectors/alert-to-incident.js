import reselect from 'reselect';

const { createSelector } = reselect;

const alertIncidentAssociationState = (state) => state.respond.alertIncidentAssociation;

export const getIncidentSearchStatus = createSelector(
  alertIncidentAssociationState,
  (alertsState) => alertsState.incidentSearchStatus
);

export const getIncidentSearchResults = createSelector(
  alertIncidentAssociationState,
  (alertsState) => alertsState.incidentSearchResults
);

export const getSelectedIncident = createSelector(
  alertIncidentAssociationState,
  (alertsState) => alertsState.selectedIncident
);

export const getIncidentSearchSortBy = createSelector(
  alertIncidentAssociationState,
  (alertsState) => alertsState.incidentSearchSortBy
);

export const getIncidentSearchSortIsDescending = createSelector(
  alertIncidentAssociationState,
  (alertsState) => alertsState.incidentSearchSortIsDescending
);

export const getIncidentSearchText = createSelector(
  alertIncidentAssociationState,
  (alertsState) => alertsState.incidentSearchText
);

export const hasSearchQuery = createSelector(
  getIncidentSearchText,
  (searchText) => searchText && searchText.length >= 3
);

export const getIsAddAlertsInProgress = createSelector(
  alertIncidentAssociationState,
  (alertsState) => alertsState.isAddAlertsInProgress
);

export const getIsAddToAlertsUnavailable = createSelector(
  getIsAddAlertsInProgress,
  getSelectedIncident,
  (isAddToAlertsInProgress, selectedIncident) => isAddToAlertsInProgress || !selectedIncident
);