import { createSelector } from 'reselect';
import _ from 'lodash';

const _fileState = (state) => state.files;
const _riskType = (state) => state.riskType;

export const activeRiskSeverityTab = (state) => state.risk.activeRiskSeverityTab;
export const riskScoreContext = (state) => state.risk.riskScoreContext;
export const riskScoreContextError = (state) => state.risk.riskScoreContextError;
export const eventsLoadingStatus = (state) => state.risk.eventsLoadingStatus;
export const alertsError = (state) => state.risk.alertsError;
export const selectedAlert = (state) => state.risk.selectedAlert || '';
export const expandedEventId = (state) => state.risk.expandedEventId;
export const isRespondServerOffline = (state) => state.risk.isRespondServerOffline;
export const events = (state) => state.risk.eventsData;
export const alertsLoadingStatus = (state) => state.risk.alertsLoadingStatus;
export const eventContext = (state) => state.risk.eventContext;
export const currentEntityId = (state) => state.risk.currentEntityId;


export const alertCategory = createSelector(
  activeRiskSeverityTab,
  (activeRiskSeverityTab) => {
    return _.upperFirst(activeRiskSeverityTab) || 'Critical';
  }
);

export const currentSeverityContext = createSelector(
  [riskScoreContext, alertCategory],
  (riskScoreContext, alertCategory) => {
    const alertContext = riskScoreContext && riskScoreContext.categorizedAlerts ? riskScoreContext.categorizedAlerts[alertCategory] : null;
    if (alertContext) {
      return Object.keys(alertContext).map((key) => ({
        alertName: key,
        alertCount: alertContext[key].alertCount,
        eventCount: alertContext[key].eventCount
      }));
    }
    return null;
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
      if (riskScoreContextError.message && riskScoreContextError.message === 'Access is denied') {
        return 'investigateShared.endpoint.riskProperties.error.accessDeniedError';
      }
      return `investigateShared.endpoint.riskProperties.error.${riskScoreContextError.error}`;
    }
    return null;
  }
);

export const isRiskScoreContextEmpty = createSelector(
  riskScoreContext,
  (riskScoreContext) => {
    const alertCount = riskScoreContext ? { ...riskScoreContext.distinctAlertCount } : [];

    // Count All alerts by adding alerts of critical, high, medium and low severities.
    alertCount.all = alertCount.critical + alertCount.high + alertCount.medium + alertCount.low;

    return !(alertCount.all > 0);
  }
);
