import reselect from 'reselect';

const { createSelector } = reselect;

const incidentRuleState = (state) => state.configure.respond.incidentRules;

export const getIncidentRules = createSelector(
  incidentRuleState,
  (ruleState) => ruleState.rules
);

export const getIncidentRulesStatus = createSelector(
  incidentRuleState,
  (ruleState) => ruleState.rulesStatus
);

export const getSelectedIncidentRuleId = createSelector(
  incidentRuleState,
  (ruleState) => ruleState.selectedRule
);

export const getIsIncidentRulesTransactionUnderway = createSelector(
  incidentRuleState,
  (ruleState) => ruleState.isTransactionUnderway
);

export const hasSelectedRule = createSelector(
  getSelectedIncidentRuleId,
  (selectedRuleId) => !!selectedRuleId
);