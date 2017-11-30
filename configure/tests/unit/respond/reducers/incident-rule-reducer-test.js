import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import ACTION_TYPES from 'configure/actions/types/respond';
import reducer from 'configure/reducers/respond/incident-rules/rule/reducer';
import ruleNormalizer from 'configure/reducers/respond/incident-rules/incident-rule-normalizer';
import ruleData from '../../../data/subscriptions/incident-rules/queryRecord/data';
import fieldData from '../../../data/subscriptions/incident-fields/findAll/data';

module('Unit | Utility | Incident Rule Reducers');

const initialState = {
  ruleInfo: null,
  ruleStatus: null,
  conditionGroups: null,
  conditions: null,
  fields: [],
  fieldsStatus: null,
  visited: []
};

const normalizedConditions = {
  groups: {
    '0': { filterType: 'FILTER_GROUP', logicalOperator: 'and', id: 0 },
    '1': { filterType: 'FILTER_GROUP', logicalOperator: 'or', id: 1, groupId: 0 },
    '2': { filterType: 'FILTER_GROUP', logicalOperator: 'not', id: 2, groupId: 0 }
  },
  conditions: {
    '0': { filterType: 'FILTER', property: 'alert.type', operator: '=', value: 'Network', groupId: 0, id: 0 },
    '1': { filterType: 'FILTER', property: 'alert.events.domain', operator: '=', value: 'meiske.com', groupId: 0, id: 1 },
    '2': { filterType: 'FILTER', property: 'alert.events.destination.device.port', operator: '=', value: 8080, groupId: 1, id: 2 },
    '3': { filterType: 'FILTER', property: 'alert.severity', operator: '>=', value: 80, groupId: 2, id: 3 }
  }
};

// A new rule has only one group
const baseConditionGroups = {
  0: {
    filterType: 'FILTER_GROUP',
    id: 0,
    logicalOperator: 'and'
  }
};

// A new rule has only one condition (incomplete)
const baseConditions = {
  0: {
    filterType: 'FILTER',
    groupId: 0,
    id: 0,
    operator: null,
    property: null,
    value: null
  }
};

test('With FETCH_INCIDENT_RULE_STARTED, the ruleState is properly set', function(assert) {
  const action = {
    type: ACTION_TYPES.FETCH_INCIDENT_RULE_STARTED
  };
  const expectedEndState = {
    ...initialState,
    ruleStatus: 'wait'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_INCIDENT_RULE, the rule builder conditions are properly parsed and normalized', function(assert) {
  ruleNormalizer.resetCounters();
  const payload = { data: ruleData };
  const action = {
    type: ACTION_TYPES.FETCH_INCIDENT_RULE,
    payload
  };

  const expectedEndState = {
    ...initialState,
    ruleInfo: payload.data,
    ruleStatus: 'complete',
    conditionGroups: normalizedConditions.groups,
    conditions: normalizedConditions.conditions
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_INCIDENT_RULE failure, the ruleState is properly set', function(assert) {
  const action = {
    type: ACTION_TYPES.FETCH_INCIDENT_RULE_FAILED
  };
  const expectedEndState = {
    ...initialState,
    ruleStatus: 'error'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_INCIDENT_FIELDS_STARTED starting, the fieldState is properly set', function(assert) {
  const action = {
    type: ACTION_TYPES.FETCH_INCIDENT_FIELDS_STARTED
  };
  const expectedEndState = {
    ...initialState,
    fieldsStatus: 'wait'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_INCIDENT_FIELDS, the fields are updated in state', function(assert) {
  const payload = { data: fieldData };
  const action = {
    type: ACTION_TYPES.FETCH_INCIDENT_FIELDS,
    payload
  };

  const expectedEndState = {
    ...initialState,
    fields: payload.data,
    fieldsStatus: 'complete'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_INCIDENT_FIELDS_FAILED failure, the fieldsStatus is properly set', function(assert) {
  const action = {
    type: ACTION_TYPES.FETCH_INCIDENT_FIELDS_FAILED
  };
  const expectedEndState = {
    ...initialState,
    fieldsStatus: 'error'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With INCIDENT_RULES_ADD_CONDITION, a new condition is added to the conditions state', function(assert) {
  const conditionId = ruleNormalizer.conditionCounter;
  const initState = {
    conditions: {}
  };
  const expectedEndState = {
    conditions: { [conditionId]: { id: conditionId, filterType: 'FILTER', groupId: 0 } }
  };
  const action = {
    type: ACTION_TYPES.INCIDENT_RULES_ADD_CONDITION,
    payload: 0
  };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With INCIDENT_RULES_REMOVE_CONDITION, the condition is removed from the conditions state', function(assert) {
  const initState = {
    conditions: { '7': { id: 7 } }
  };
  const expectedEndState = {
    conditions: {}
  };
  const action = {
    type: ACTION_TYPES.INCIDENT_RULES_REMOVE_CONDITION,
    payload: 7
  };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With INCIDENT_RULES_ADD_GROUP, a new group is added to the conditionGroups state', function(assert) {
  const groupId = ruleNormalizer.groupCounter;
  const initState = {
    conditionGroups: {}
  };
  const expectedEndState = {
    conditionGroups: { [groupId]: { id: groupId, filterType: 'FILTER_GROUP', logicalOperator: 'and', groupId: 0 } }
  };
  const action = { type: ACTION_TYPES.INCIDENT_RULES_ADD_GROUP };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With INCIDENT_RULES_REMOVE_GROUP, a new group is added to the conditionGroups state', function(assert) {
  const initState = {
    conditionGroups: { '5': { id: 5 }, '7': { id: 7 } }
  };
  const expectedEndState = {
    conditionGroups: { '5': { id: 5 } }
  };
  const action = { type: ACTION_TYPES.INCIDENT_RULES_REMOVE_GROUP, payload: 7 };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With INCIDENT_RULES_UPDATE_CONDITION, the condition is updated', function(assert) {
  const initState = {
    conditions: { '7': { id: 7, property: 'alert.created', operator: '=' } }
  };
  const expectedEndState = {
    conditions: { '7': { id: 7, property: 'alert.type', operator: '!=' } }
  };
  const action = {
    type: ACTION_TYPES.INCIDENT_RULES_UPDATE_CONDITION,
    payload: { conditionId: 7, changes: { property: 'alert.type', operator: '!=' } }
  };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With INCIDENT_RULES_UPDATE_GROUP, the group is updated', function(assert) {
  const initState = {
    conditionGroups: { '5': { id: 5, logicalOperator: 'and' }, '7': { id: 7 } }
  };
  const expectedEndState = {
    conditionGroups: { '5': { id: 5, logicalOperator: 'or' }, '7': { id: 7 } }
  };
  const action = {
    type: ACTION_TYPES.INCIDENT_RULES_UPDATE_GROUP,
    payload: { groupId: 5, changes: { logicalOperator: 'or' } }
  };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With NEW_INCIDENT_RULE, the ruleInfo and associated state is reset to defaults', function(assert) {
  const initState = {
    ...initialState,
    ruleInfo: {
      incidentCreationOptions: {
        ruleTitle: 'Test',
        categories: [{}]
      },
      priorityScale: {
        CRITICAL: 70,
        HIGH: 40,
        MEDIUM: 20,
        LOW: 10
      },
      incidentScoringOptions: {
        type: 'high'
      },
      notificationOptions: { test: 'test' }
    },
    conditionGroups: normalizedConditions.groups,
    conditions: normalizedConditions.conditions
  };

  const expectedEndState = {
    ...initialState,
    ruleInfo: {
      action: 'GROUP_INTO_INCIDENT',
      incidentCreationOptions: {
        ruleTitle: '${ruleName} for ${groupByValue1}',
        categories: []
      },
      priorityScale: {
        CRITICAL: 90,
        HIGH: 50,
        MEDIUM: 20,
        LOW: 1
      },
      incidentScoringOptions: {
        type: 'average'
      },
      timeWindow: '1h',
      notificationOptions: {}
    },
    conditionGroups: baseConditionGroups,
    conditions: baseConditions
  };

  const action = {
    type: ACTION_TYPES.NEW_INCIDENT_RULE
  };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With INCIDENT_RULES_UPDATE_INFO, the rule field is updated', function(assert) {
  const initState = {
    ruleInfo: {
      incidentCreationOptions: {
        ruleTitle: ''
      }
    },
    visited: []
  };

  const expectedEndState = {
    ruleInfo: {
      incidentCreationOptions: {
        ruleTitle: 'Testing 123'
      }
    },
    visited: ['ruleInfo.incidentCreationOptions.ruleTitle']
  };

  const action = {
    type: ACTION_TYPES.INCIDENT_RULES_UPDATE_INFO,
    payload: {
      field: 'ruleInfo.incidentCreationOptions.ruleTitle',
      value: 'Testing 123'
    }
  };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With INCIDENT_RULES_CLEAR_MATCH_CONDITIONS, the match conditions get reverted to one-group-one-condition state', function(assert) {
  const initState = {
    ruleInfo: {
      uiFilterConditions: 'Non empty',
      matchConditions: 'Non empty'
    },
    conditionGroups: normalizedConditions.groups,
    conditions: normalizedConditions.conditions
  };

  const expectedEndState = {
    ruleInfo: {
      uiFilterConditions: '',
      matchConditions: ''
    },
    conditionGroups: baseConditionGroups,
    conditions: baseConditions
  };

  const action = {
    type: ACTION_TYPES.INCIDENT_RULES_CLEAR_MATCH_CONDITIONS
  };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With INCIDENT_RULES_SAVE_STARTED, the ruleState is properly set', function(assert) {
  const action = {
    type: ACTION_TYPES.INCIDENT_RULES_SAVE_STARTED
  };
  const expectedEndState = {
    ...initialState,
    isTransactionUnderway: true
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With INCIDENT_RULES_SAVE, the rule builder conditions are properly parsed and normalized', function(assert) {
  ruleNormalizer.resetCounters();
  const initState = { isTransactionUnderway: true };
  const payload = { data: ruleData };
  const action = {
    type: ACTION_TYPES.INCIDENT_RULES_SAVE,
    payload
  };

  const expectedEndState = {
    ruleInfo: payload.data,
    conditionGroups: normalizedConditions.groups,
    conditions: normalizedConditions.conditions,
    isTransactionUnderway: false
  };

  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With INCIDENT_RULES_SAVE_FAILED, the ruleState is properly set', function(assert) {
  const action = {
    type: ACTION_TYPES.INCIDENT_RULES_SAVE_FAILED
  };
  const expectedEndState = {
    ...initialState,
    isTransactionUnderway: false
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});