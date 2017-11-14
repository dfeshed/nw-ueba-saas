import { module, test } from 'qunit';
import {
  getRuleInfo,
  getRuleStatus,
  getRuleConditionGroups,
  getRuleConditions,
  getRootConditionGroup,
  getFieldsStatus,
  getFields,
  isLoading,
  hasAdvancedQuery,
  getRuleGroupBy,
  getTimeWindow,
  getTimeWindowUnit,
  getTimeWindowValue,
  hasInvalidTimeValue,
  getSelectedGroupByFields,
  hasInvalidGroupByFields,
  getIncidentCreationOptions,
  getIncidentTitle,
  getSelectedCategories,
  getSelectedAssignee,
  getAssigneeOptions,
  getPriorityScale,
  getIncidentScoringOptions,
  hasInvalidPriorityScale,
  isTransactionUnderway,
  hasGroupsWithoutConditions,
  hasMissingConditionInfo,
  isRuleBuilderInvalid,
  getVisited,
  hasMissingInformation
} from 'respond/selectors/aggregation-rule';
import ruleInfo from '../../data/subscriptions/aggregation-rules/queryRecord/data';
import fields from '../../data/subscriptions/aggregation-fields/findAll/data';
import enabledUsers from '../../data/subscriptions/users/findAll/data';

module('Unit | Utility | Aggregation Rule Selectors');

const rootGroup = { id: 2 };
const conditionGroups = { 2: rootGroup, 3: { id: 3 } };
const conditions = {
  1: { id: 1, groupId: 2, property: 'alert.id', operator: '=', value: 'test' },
  2: { id: 2, groupId: 3, property: 'alert.name', operator: '!=', value: 'tested' }
};

const aggregationRule = {
  ruleInfo,
  ruleStatus: 'wait',
  conditionGroups,
  conditions,
  fields,
  fieldsStatus: 'error',
  isTransactionUnderway: true
};

const state = {
  respond: {
    aggregationRule,
    dictionaries: {
      categoryTags: []
    },
    users: {
      enabledUsers
    }
  }
};

function createRuleState(ruleState) {
  return {
    respond: {
      aggregationRule: {
        ...ruleState,
        fields
      },
      dictionaries: {
        categoryTags: []
      },
      users: {
        enabledUsers
      }
    }
  };
}

function createRuleInfoState(ruleInfo) {
  return createRuleState({ ruleInfo });
}

test('Basic Aggregation Rules selectors', function(assert) {
  assert.equal(getRuleInfo(state), ruleInfo, 'The returned value from the getRuleInfo selector is as expected');
  assert.equal(getRuleStatus(state), 'wait', 'The returned value from the getRuleStatus selector is as expected');
  assert.equal(getRuleConditionGroups(state), conditionGroups, 'The returned value from the getRuleConditionGroups selector is as expected');
  assert.equal(getRuleConditions(state), conditions, 'The returned value from the getRuleConditions selector is as expected');
  assert.equal(getFieldsStatus(state), 'error', 'The returned value from the getFieldsStatus selector is as expected');
  assert.equal(getFields(state), fields, 'The returned value from the getFields selector is as expected');
  assert.equal(getRootConditionGroup(state), rootGroup, 'The returned value form the getRootConditionsGroup selector is as expected');
  assert.equal(isLoading(state), true, 'The returned value form the isLoading selector is as expected');
  assert.equal(hasAdvancedQuery(state), false, 'The returned value from hasAdvancedQuery is as expected');
  assert.equal(getIncidentCreationOptions(state), ruleInfo.incidentCreationOptions, 'The returned value from getIncidentCreationOptions selector is as expected');
  assert.equal(getIncidentTitle(state), ruleInfo.incidentCreationOptions.ruleTitle, 'The returned value from getIncidentTitle selector is as expected');
  assert.equal(getSelectedCategories(state), ruleInfo.incidentCreationOptions.categories, 'The returned value from getSelectedCategories selector is as expected');
  assert.equal(getSelectedAssignee(state), ruleInfo.incidentCreationOptions.assignee, 'The returned value from getSelectedAssignee selector is as expected');
  assert.deepEqual(getAssigneeOptions(state), [{ id: null }, ...enabledUsers], 'When there is a selected assignee, the assignee options has a place holder user object with an id of null used for unassigning');
  assert.deepEqual(getPriorityScale(createRuleInfoState({})), {
    CRITICAL: 90,
    HIGH: 50,
    MEDIUM: 20,
    LOW: 1
  }, 'When there is no priority scale, a default is provided');
  assert.equal(getIncidentScoringOptions(state), ruleInfo.incidentScoringOptions, 'The returned value from getIncidentScoringOptions selector is as expected');
  assert.equal(isTransactionUnderway(state), true, 'The returned value from isTransactionUnderway selector is as expected');
  assert.equal(getVisited(state), state.visited, 'The returned value from getVisited selector is as expected');
});

test('Time Window Selectors', function(assert) {
  function createTimeWindowState(timeWindow, action = 'GROUP_INTO_INCIDENT') {
    return createRuleInfoState({ timeWindow, action });
  }

  assert.equal(hasInvalidTimeValue(state), false, 'The hasInvalidTimeValue selector should return false when the timeWindow is "7d"');
  assert.deepEqual(getTimeWindow(state), {
    unit: 'DAY',
    value: 7
  }, 'The returned value from the getTimeWindow selector is as expected');
  assert.equal(getTimeWindowUnit(state), 'DAY', 'The returned value from the getTimeWindowUnit selector is as expected');
  assert.equal(getTimeWindowValue(state), 7, 'The returned value from the getTimeWindowValue selector is as expected');
  assert.equal(hasInvalidTimeValue(createTimeWindowState('26d')), true, 'You cannot have more than 25 days for the time window');
  assert.equal(hasInvalidTimeValue(createTimeWindowState('101m')), true, 'You cannot have more than 100 minutes for the time window');
  assert.equal(hasInvalidTimeValue(createTimeWindowState('101h')), true, 'You cannot have more than 100 hours for the time window');
  assert.equal(hasInvalidTimeValue(createTimeWindowState('99h')), false, '99 hours is a valid time window');
  assert.equal(hasInvalidTimeValue(createTimeWindowState('500h', 'SUPPRESS_ALERT')), false, 'If the action is SUPPRESS_ALERT, the hasInvalidTimeValue always returns false');
});

test('Group By Selectors', function(assert) {
  function createGroupByState(groupBy, action = 'GROUP_INTO_INCIDENT') {
    return createRuleInfoState({ groupBy, action });
  }

  assert.equal(getRuleGroupBy(state), ruleInfo.groupByFields, 'The returned value from getRuleGroupBy selector is as expected');
  assert.deepEqual(getSelectedGroupByFields(state), [fields[0]], 'The getSelectedGroupByFields returns the field(s) that are referenced from the ruleInfo.groupBy prop');
  assert.equal(hasInvalidGroupByFields(state), false, 'The group by field value is valid');
  assert.equal(hasInvalidGroupByFields(createGroupByState([])), true, 'The group by field value is invalid because it has no group by options');
  assert.equal(hasInvalidGroupByFields(createGroupByState(['alert.name', 'alert.id', 'alert.type'])), true, 'The group by field value is invalid because it has more than two options');
  assert.equal(hasInvalidGroupByFields(createGroupByState([], 'SUPPRESS_ALERT')), false, 'The group by field value is invalid because it has no group by options');
});

test('Priority Scale Selectors', function(assert) {
  function createPriorityScaleState([CRITICAL, HIGH, MEDIUM, LOW], action = 'GROUP_INTO_INCIDENT') {
    return createRuleInfoState({ priorityScale: { CRITICAL, HIGH, MEDIUM, LOW }, action });
  }

  assert.equal(getPriorityScale(state), ruleInfo.priorityScale, 'The returned value from getPriorityScale selector is as expected');
  assert.equal(hasInvalidPriorityScale(createPriorityScaleState([90, 50, 20, 1])), false, 'The priority scale is not invalid');
  assert.equal(hasInvalidPriorityScale(createPriorityScaleState([101, 50, 20, 1])), true, 'Critical cannot exceed 100');
  assert.equal(hasInvalidPriorityScale(createPriorityScaleState([60, 100, 20, 1])), true, 'High cannot be greater or equal to critical');
  assert.equal(hasInvalidPriorityScale(createPriorityScaleState([90, 50, 51, 1])), true, 'Medium cannot be greater or equal to high');
  assert.equal(hasInvalidPriorityScale(createPriorityScaleState([90, 50, 20, 20])), true, 'Low cannot be greater or equal to medium');
  assert.equal(hasInvalidPriorityScale(createPriorityScaleState([1000, 1000, 1000, 1000], 'SUPPRESS_ALERT')), false, 'Always returns false when there is a supress alert action');
});

test('Invalid Rule Builder Configuration', function(assert) {
  function createRuleBuilderState({ conditionGroups, conditions }, ruleInfo = {}) {
    return createRuleState({ conditionGroups, conditions, ruleInfo });
  }

  const validGroupAndConditions = {
    conditionGroups: { '0': { id: 0 } },
    conditions: { '0': { id: 0, property: 'alert.id', operator: '!=', value: 'blah', groupId: 0 } }
  };

  const groupHasMissingConditions = {
    conditionGroups: { '0': { id: 0 }, '1': { id: 1, groupId: 0 } },
    conditions: { '0': { id: 0, property: 'alert.id', operator: '!=', value: 'blah', groupId: 0 } }
  };

  const conditionIsMissingProperty = {
    conditionGroups: { '0': { id: 0 }, '1': { id: 1, groupId: 0 } },
    conditions: { '0': { id: 0, operator: '!=', value: 'blah', groupId: 0 } }
  };

  const conditionIsMissingOperator = {
    conditionGroups: { '0': { id: 0 } },
    conditions: { '0': { id: 0, property: 'alert.id', value: 'blah', groupId: 0 } }
  };

  const conditionIsMissingValue = {
    conditionGroups: { '0': { id: 0 } },
    conditions: { '0': { id: 0, property: 'alert.id', operator: '!=', groupId: 0 } }
  };

  assert.equal(hasGroupsWithoutConditions(createRuleBuilderState(validGroupAndConditions)), false, 'The group has conditions');
  assert.equal(hasGroupsWithoutConditions(createRuleBuilderState(groupHasMissingConditions)), true, 'The group has no conditions');
  assert.equal(hasMissingConditionInfo(createRuleBuilderState(validGroupAndConditions)), false, 'The condition has all information');
  assert.equal(hasMissingConditionInfo(createRuleBuilderState(conditionIsMissingProperty)), true, 'The condition is missing the property information');
  assert.equal(hasMissingConditionInfo(createRuleBuilderState(conditionIsMissingOperator)), true, 'The condition is missing the operator information');
  assert.equal(hasMissingConditionInfo(createRuleBuilderState(conditionIsMissingValue)), true, 'The condition is missing the value information');
  assert.equal(isRuleBuilderInvalid(createRuleBuilderState(validGroupAndConditions)), false, 'The rule builder is not invalid');
  assert.equal(isRuleBuilderInvalid(createRuleBuilderState(groupHasMissingConditions, { advancedUiFilterConditions: true })), false,
    'Even if the group is invalid, the isRuleBuilderInvalid selector returns false if advancedUiFilterConditions is true');
  assert.equal(isRuleBuilderInvalid(createRuleBuilderState(conditionIsMissingProperty, { advancedUiFilterConditions: true })), false,
    'Even if the condition is invalid, the isRuleBuilderInvalid selector returns false if advancedUiFilterConditions is true');
});

test('Missing information selector', function(assert) {
  const incidentCreationOptions = {
    ruleTitle: 'test'
  };
  const ruleInfo = {
    name: 'test',
    action: 'GROUP_INTO_INCIDENT',
    groupByFields: ['alert.name'],
    timeWindow: '7d',
    priorityScale: { 'LOW': 1, 'HIGH': 50, 'CRITICAL': 90, 'MEDIUM': 20 },
    advancedUiFilterConditions: false,
    uiFiltersCondition: ''
  };
  assert.equal(hasMissingInformation(createRuleState({
    conditionGroups,
    conditions,
    ruleInfo: { ...ruleInfo, incidentCreationOptions }
  })), false, 'The state has all of the information needed');
  assert.equal(hasMissingInformation(createRuleState({
    conditionGroups,
    conditions,
    ruleInfo: { ...ruleInfo, name: '', incidentCreationOptions }
  })), true, 'Without rule name, hasMissingInformation is true');
  assert.equal(hasMissingInformation(createRuleState({
    conditionGroups,
    conditions,
    ruleInfo: { ...ruleInfo, action: null, incidentCreationOptions }
  })), true, 'Without rule action, hasMissingInformation is true');
  assert.equal(hasMissingInformation(createRuleState({
    conditionGroups,
    conditions,
    ruleInfo: { ...ruleInfo, groupByFields: [], incidentCreationOptions }
  })), true, 'Without group by, hasMissingInformation is true');
  assert.equal(hasMissingInformation(createRuleState({
    conditionGroups,
    conditions,
    ruleInfo: { ...ruleInfo, timeWindow: '1000d', incidentCreationOptions }
  })), true, 'With incorrect time window, hasMissingInformation is true');
  assert.equal(hasMissingInformation(createRuleState({
    conditionGroups,
    conditions,
    ruleInfo: { ...ruleInfo, incidentCreationOptions: { ...incidentCreationOptions, ruleTitle: '' } }
  })), true, 'Without incident title, hasMissingInformation is true');
  assert.equal(hasMissingInformation(createRuleState({
    conditionGroups,
    conditions,
    ruleInfo: { ...ruleInfo, priorityScale: { 'LOW': 100, 'HIGH': 50, 'CRITICAL': 90, 'MEDIUM': 20 } }
  })), true, 'With incorrect priority scale, hasMissingInformation is true');
  assert.equal(hasMissingInformation(createRuleState({
    conditionGroups,
    conditions,
    ruleInfo: { ...ruleInfo, advancedUiFilterConditions: true }
  })), true, 'With advancedUiFilterConditions true, hasMissingInformation is true because uiFiltersCondition cannot be empty');
});