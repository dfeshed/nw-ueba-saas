import { createSelector } from 'reselect';

const _eventsData = (state) => state.risk.eventsData || [];
export const activeRiskSeverityTab = (state) => state.risk.activeRiskSeverityTab;
export const riskScoreContext = (state) => state.risk.riskScoreContext;
export const riskScoreContextError = (state) => state.risk.riskScoreContextError;
export const eventsLoadingStatus = (state) => state.risk.eventsLoadingStatus;
export const alertsError = (state) => state.risk.alertsError;
export const selectedAlert = (state) => state.risk.selectedAlert;
export const expandedEventId = (state) => state.risk.expandedEventId;

export const events = createSelector(
  _eventsData,
  (events) => {
    return events;
  }
);
