import reselect from 'reselect';
import { parseDuration } from 'configure/utils/date/duration';
import { getGroupedCategories } from 'configure/reducers/respond/dictionaries/selectors';
import { getEnabledUsers } from 'configure/reducers/respond/users/selectors';
import { isBlank } from '@ember/utils';
import _ from 'lodash';

const { createSelector } = reselect;

function isBetween(value, min, max) {
  if (!_.isNumber(value) || !_.isNumber(min) || !_.isNumber(max)) {
    return false;
  }
  return value >= min && value <= max;
}

const incidentRuleState = (state) => state.configure.respond.incidentRule;

export const getRuleInfo = createSelector(
  incidentRuleState,
  (ruleState) => ruleState.ruleInfo || {}
);

export const getRuleStatus = createSelector(
  incidentRuleState,
  (ruleState) => ruleState.ruleStatus
);

export const getRuleConditionGroups = createSelector(
  incidentRuleState,
  (ruleState) => ruleState.conditionGroups
);

export const getRuleConditions = createSelector(
  incidentRuleState,
  (ruleState) => ruleState.conditions
);

export const hasAdvancedQuery = createSelector(
  getRuleInfo,
  (ruleInfo) => ruleInfo.advancedUiFilterConditions
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
  incidentRuleState,
  (ruleState) => ruleState.fieldsStatus
);

export const getFields = createSelector(
  incidentRuleState,
  (ruleState) => ruleState.fields
);

// The field Domain for Suspected C&C is the same as Domain from a condition's perspective
// Removing the Domain for Suspected C&C from the available fields for conditions, since it cannot
// be distinguished from "Domain" which has the same value.
export const getConditionFields = createSelector(
  getFields,
  (fields) => fields.filter((field) => field.groupByField !== 'alert.groupby_c2domain')
);

export const getRuleGroupBy = createSelector(
  getRuleInfo,
  (ruleInfo) => {
    ruleInfo = ruleInfo || {};
    return ruleInfo.groupByFields || [];
  }
);

export const getTimeWindow = createSelector(
  getRuleInfo,
  (ruleInfo) => {
    ruleInfo = ruleInfo || {};
    return parseDuration(ruleInfo.timeWindow, '1h');
  }
);

export const getTimeWindowUnit = createSelector(
  getTimeWindow,
  (timeWindow) => timeWindow && timeWindow.unit ? timeWindow.unit : 'HOUR'
);

export const getTimeWindowValue = createSelector(
  getTimeWindow,
  (timeWindow) => !timeWindow || !timeWindow.value ? null : timeWindow.value
);

export const hasInvalidTimeValue = createSelector(
  [getRuleInfo, getTimeWindowUnit, getTimeWindowValue],
  (ruleInfo, unit, value) => (ruleInfo.action !== 'SUPPRESS_ALERT' && (!value || value < 1 || value > 100 || (unit === 'DAY' && value > 24)))
);

export const getSelectedGroupByFields = createSelector(
  getRuleGroupBy, getFields,
  (ruleGroupBy, groupByFields) => groupByFields.filter((field) => {
    const groupBy = field.groupByField || field.value;
    return ruleGroupBy.includes(groupBy);
  })
);

export const hasInvalidGroupByFields = createSelector(
  [getRuleInfo, getSelectedGroupByFields],
  (ruleInfo, groupBy) => (ruleInfo.action === 'SUPPRESS_ALERT') ? false : groupBy.length > 2 || groupBy.length < 1
);

export const isLoading = createSelector(
  getFieldsStatus,
  getRuleStatus,
  getGroupedCategories,
  (fieldsStatus, ruleStatus, categories) => (fieldsStatus === 'wait' || ruleStatus === 'wait' || categories.length === 0)
);

export const getIncidentCreationOptions = createSelector(
  getRuleInfo,
  (ruleInfo) => ruleInfo.incidentCreationOptions || {}
);

export const getIncidentTitle = createSelector(
  getIncidentCreationOptions,
  (incidentCreationOptions) => incidentCreationOptions.ruleTitle
);

export const getSelectedCategories = createSelector(
  getIncidentCreationOptions,
  (incidentCreationOptions) => incidentCreationOptions.categories || []
);


export const getSelectedAssignee = createSelector(
  getIncidentCreationOptions,
  (incidentCreationOptions) => incidentCreationOptions.assignee
);

export const getAssigneeOptions = createSelector(
  getEnabledUsers, getSelectedAssignee,
  (enabledUsers, selectedAssignee) => {
    // Add a placeholder option for leaving the assignee unassigned
    return selectedAssignee ? [{ id: null }, ...enabledUsers ] : enabledUsers;
  }
);

export const getPriorityScale = createSelector(
  getRuleInfo,
  (ruleInfo) => ruleInfo.priorityScale || { CRITICAL: 90, HIGH: 50, MEDIUM: 20, LOW: 1 }
);

export const getIncidentScoringOptions = createSelector(
  getRuleInfo,
  (ruleInfo) => ruleInfo.incidentScoringOptions || { type: 'average' }
);

export const hasInvalidPriorityScale = createSelector(
  [getRuleInfo, getPriorityScale],
  (ruleInfo, priorityScale) => {
    if (ruleInfo.action === 'SUPPRESS_ALERT') {
      return false;
    }
    let isInvalid = false;
    const values = _.values(priorityScale);
    const { CRITICAL, HIGH, MEDIUM, LOW } = priorityScale;
    if (!_.every(values, _.isNumber)) {
      isInvalid = true; // the priority scale is invalid if there are any non-numeric values
    }
    if (!isBetween(CRITICAL, 4, 100) || !isBetween(HIGH, 3, 99) || !isBetween(MEDIUM, 2, 98) || !isBetween(LOW, 1, 97)) {
      isInvalid = true; // the priority scale is invalid if any of the values fail to fall within the prescribed range
    }
    if (LOW >= MEDIUM || MEDIUM >= HIGH || HIGH >= CRITICAL) {
      isInvalid = true; // the priority scale is invalid if LOW exceeds MEDIUM, MEDIUM exceeds HIGH, or HIGH exceeds CRITICAL
    }
    return isInvalid;
  }
);

export const isTransactionUnderway = createSelector(
  incidentRuleState,
  (ruleState) => ruleState.isTransactionUnderway
);

export const hasGroupsWithoutConditions = createSelector(
  getRuleConditionGroups, getRuleConditions,
  (groups, conditions) => {
    if (!groups || !conditions) {
      return false;
    }
    const groupIds = _.values(groups).map((group) => group.id);
    const conditionGroupIds = _.values(conditions).map((condition) => condition.groupId).uniq();
    return _.difference(groupIds, conditionGroupIds).length > 0;
  }
);

export const hasMissingConditionInfo = createSelector(
  getRuleConditions,
  (conditions) => {
    if (!conditions) {
      return false;
    }
    return _.values(conditions).reduce((hasMissingInfo, { operator, value, property }) => {
      if (!hasMissingInfo) {
        hasMissingInfo = isBlank(operator) || isBlank(value) || isBlank(property);
      }
      return hasMissingInfo;
    }, false);
  }
);

export const isRuleBuilderInvalid = createSelector(
  [getRuleInfo, hasGroupsWithoutConditions, hasMissingConditionInfo],
  (ruleInfo, hasGroupsWithoutConditions, hasMissingConditionInfo) => {
    return !ruleInfo.advancedUiFilterConditions && (hasGroupsWithoutConditions || hasMissingConditionInfo);
  }
);

export const hasMissingInformation = createSelector(
  [
    getRuleInfo,
    isRuleBuilderInvalid,
    getIncidentCreationOptions,
    hasInvalidTimeValue,
    hasInvalidPriorityScale,
    hasInvalidGroupByFields,
    getIncidentTitle
  ],
  (ruleInfo,
   isRuleBuilderInvalid,
   incidentCreationOptions,
   hasInvalidTimeValue,
   hasInvalidPriorityScale,
   hasInvalidGroupByFields,
   incidentTitle) => {
    return isBlank(ruleInfo.name) ||
      isRuleBuilderInvalid ||
      isBlank(incidentTitle) ||
      hasInvalidTimeValue ||
      hasInvalidPriorityScale ||
      hasInvalidGroupByFields ||
      isBlank(ruleInfo.action) ||
      (ruleInfo.advancedUiFilterConditions && isBlank(ruleInfo.uiFilterConditions));
  }
);

export const getVisited = createSelector(
  incidentRuleState,
  (state) => state.visited
);
