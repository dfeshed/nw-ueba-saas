import reselect from 'reselect';

const { createSelector } = reselect;

const riskScoringState = (state) => state.configure.respond.riskScoring;

export const getRiskScoringExpanded = createSelector(
  riskScoringState,
  (riskScoringState) => riskScoringState.riskScoringExpanded
);

export const getRiskScoringStatus = createSelector(
  riskScoringState,
  (riskScoringState) => riskScoringState.riskScoringStatus
);

export const getRiskScoringSettings = createSelector(
  riskScoringState,
  (riskScoringState) => riskScoringState.riskScoringSettings
);
