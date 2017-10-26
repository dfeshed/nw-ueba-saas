import reselect from 'reselect';

const { createSelector } = reselect;

const aggregationRuleState = (state) => state.respond.aggregationRule;

export const getRuleInfo = createSelector(
  aggregationRuleState,
  (ruleState) => ruleState.rule
);

export const getRuleStatus = createSelector(
  aggregationRuleState,
  (ruleState) => ruleState.ruleStatus
);

export const getRuleConditionGroups = createSelector(
  aggregationRuleState,
  (ruleState) => ruleState.conditionGroups
);

export const getRuleConditions = createSelector(
  aggregationRuleState,
  (ruleState) => ruleState.conditions
);

export const getRootConditionGroup = createSelector(
  getRuleConditionGroups,
  (groups) => {
    groups = groups || {};
    const keys = Object.keys(groups);
    if (!keys.length) {
      return null;
    }
    const [rootKey] = keys;
    return groups[rootKey];
  }
);

export const getFieldsStatus = createSelector(
  aggregationRuleState,
  (ruleState) => ruleState.fieldsStatus
);

export const getFields = createSelector(
  aggregationRuleState,
  (ruleState) => ruleState.fields
);

export const isLoading = createSelector(
  getFieldsStatus,
  getRuleStatus,
  (fieldsStatus, ruleStatus) => fieldsStatus === 'wait' || ruleStatus === 'wait'
);


