import reselect from 'reselect';

const { createSelector } = reselect;

const aggregationRuleState = (state) => state.respond.aggregationRules;

export const getAggregationRules = createSelector(
  aggregationRuleState,
  (ruleState) => ruleState.rules
);

export const getAggregationRulesStatus = createSelector(
  aggregationRuleState,
  (ruleState) => ruleState.rulesStatus
);

export const getSelectedAggregationRuleId = createSelector(
  aggregationRuleState,
  (ruleState) => ruleState.selectedRule
);

export const getIsAggregationRulesTransactionUnderway = createSelector(
  aggregationRuleState,
  (ruleState) => ruleState.isTransactionUnderway
);

export const hasSelectedRule = createSelector(
  getSelectedAggregationRuleId,
  (selectedRuleId) => !!selectedRuleId
);