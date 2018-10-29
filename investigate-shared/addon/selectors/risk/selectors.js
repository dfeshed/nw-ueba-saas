import { createSelector } from 'reselect';

const _eventsData = (state) => state.risk.eventsData || [];
export const activeRiskSeverityTab = (state) => state.risk.activeRiskSeverityTab;
export const riskScoreContext = (state) => state.risk.riskScoreContext;
export const riskScoreContextError = (state) => state.risk.riskScoreContextError;
export const eventsLoadingStatus = (state) => state.risk.eventsLoadingStatus;
export const alertsError = (state) => state.risk.alertsError;
export const selectedAlert = (state) => state.risk.selectedAlert;
export const expandedEventId = (state) => state.risk.expandedEventId;
const _fileState = (state) => state.files;
const _riskType = (state) => state.riskType;


export const events = createSelector(
  _eventsData,
  (events) => {
    return events;
  }
);

export const riskType = createSelector(
  _fileState, _riskType,
  (fileState, riskType) => {
    if (riskType) {
      return riskType;
    } else {
      return fileState ? 'FILE' : 'HOST';
    }
  }
);


