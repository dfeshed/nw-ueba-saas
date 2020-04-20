import reselect from 'reselect';

const { createSelector } = reselect;

const _riskScoreContext = (state) => state.processAnalysis.risk.riskScoreContext;

export const riskState = (state) => state.processAnalysis.risk || {};

export const selectedTab = (state) => state.processAnalysis.processVisuals.detailsTabSelected;

export const isEventsSelected = createSelector(
  [selectedTab],
  (selectedTab) => {
    if (selectedTab) {
      return selectedTab.name === 'events';
    }
    return false;
  }
);

export const allAlertCount = createSelector(
  _riskScoreContext,
  (riskScoreContext) => {
    const alertCount = riskScoreContext ? { ...riskScoreContext.distinctAlertCount } : null;
    return alertCount ? alertCount.critical + alertCount.high + alertCount.medium + alertCount.low : 0;
  }
);
