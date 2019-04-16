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

export const getSelectedIncidentRules = createSelector(
  incidentRuleState,
  (ruleState) => ruleState.selectedRules
);

export const getIsIncidentRulesTransactionUnderway = createSelector(
  incidentRuleState,
  (ruleState) => ruleState.isTransactionUnderway
);

export const hasOneSelectedRule = createSelector(
  getSelectedIncidentRules,
  (selectedRules) => selectedRules && selectedRules.length === 1
);
