import { createSelector } from 'reselect';

const _eventsData = (state) => state.risk.eventsData || [];
const _fileState = (state) => state.files;
const _riskType = (state) => state.riskType;

export const activeRiskSeverityTab = (state) => state.risk.activeRiskSeverityTab;
export const riskScoreContext = (state) => state.risk.riskScoreContext;
export const riskScoreContextError = (state) => state.risk.riskScoreContextError;
export const eventsLoadingStatus = (state) => state.risk.eventsLoadingStatus;
export const alertsError = (state) => state.risk.alertsError;
export const selectedAlert = (state) => state.risk.selectedAlert;
export const expandedEventId = (state) => state.risk.expandedEventId;
export const isRiskScoringServerOffline = (state) => state.risk.isRiskScoringServerOffline;

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

export const riskScoringServerError = createSelector(
  riskScoreContextError,
  (riskScoreContextError) => {
    if (riskScoreContextError) {
      return `investigateShared.endpoint.riskProperties.error.${riskScoreContextError.error}`;
    }
    return null;
  }
);

export const isRiskScoreContextEmpty = createSelector(
  riskScoreContext,
  (riskScoreContext) => {
    const alertCount = riskScoreContext ? { ...riskScoreContext.distinctAlertCount } : [];

    // Count All alerts by adding alerts of critical, high and medium severities.
    alertCount.all = alertCount.critical + alertCount.high + alertCount.medium;

    return !(alertCount.all > 0);
  }
);
