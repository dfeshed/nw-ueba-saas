import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import ACTION_TYPES from 'respond/actions/types';
import reducer, { ruleNormalizer } from 'respond/reducers/respond/aggregation-rules/aggregation-rule';
import makePackAction from '../../helpers/make-pack-action';
import ruleData from '../../data/subscriptions/aggregation-rules/queryRecord/data';
import fieldData from '../../data/subscriptions/aggregation-fields/findAll/data';

module('Unit | Utility | Aggregation Rule Builder Reducers');

const initialState = {
  rule: null,
  ruleStatus: null,
  conditionGroups: null,
  conditions: null,
  fields: [],
  fieldsStatus: null
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

test('With FETCH_AGGREGATION_RULE starting, the ruleState is properly set', function(assert) {
  const action = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.FETCH_AGGREGATION_RULE
  });
  const expectedEndState = {
    ...initialState,
    ruleStatus: 'wait'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_AGGREGATION_RULE, the rule builder conditions are properly parsed and normalized', function(assert) {
  ruleNormalizer.resetCounters();
  const payload = { data: ruleData };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_AGGREGATION_RULE,
    payload
  });

  const expectedEndState = {
    ...initialState,
    rule: payload.data,
    ruleStatus: 'complete',
    conditionGroups: normalizedConditions.groups,
    conditions: normalizedConditions.conditions
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_AGGREGATION_RULE failure, the ruleState is properly set', function(assert) {
  const action = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.FETCH_AGGREGATION_RULE
  });
  const expectedEndState = {
    ...initialState,
    ruleStatus: 'error'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_AGGREGATION_FIELDS starting, the fieldState is properly set', function(assert) {
  const action = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.FETCH_AGGREGATION_FIELDS
  });
  const expectedEndState = {
    ...initialState,
    fieldsStatus: 'wait'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_AGGREGATION_FIELDS, the fields are updated in state', function(assert) {
  const payload = { data: fieldData };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_AGGREGATION_FIELDS,
    payload
  });

  const expectedEndState = {
    ...initialState,
    fields: payload.data,
    fieldsStatus: 'complete'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_AGGREGATION_FIELDS failure, the fieldsStatus is properly set', function(assert) {
  const action = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.FETCH_AGGREGATION_FIELDS
  });
  const expectedEndState = {
    ...initialState,
    fieldsStatus: 'error'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_ADD_CONDITION, a new condition is added to the conditions state', function(assert) {
  const conditionId = ruleNormalizer.conditionCounter;
  const initState = {
    conditions: {}
  };
  const expectedEndState = {
    conditions: { [conditionId]: { id: conditionId, groupId: 0 } }
  };
  const action = {
    type: ACTION_TYPES.AGGREGATION_RULES_ADD_CONDITION,
    payload: 0
  };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_REMOVE_CONDITION, the condition is removed from the conditions state', function(assert) {
  const initState = {
    conditions: { '7': { id: 7 } }
  };
  const expectedEndState = {
    conditions: {}
  };
  const action = {
    type: ACTION_TYPES.AGGREGATION_RULES_REMOVE_CONDITION,
    payload: 7
  };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_ADD_GROUP, a new group is added to the conditionGroups state', function(assert) {
  const groupId = ruleNormalizer.groupCounter;
  const initState = {
    conditionGroups: {}
  };
  const expectedEndState = {
    conditionGroups: { [groupId]: { id: groupId, logicalOperator: 'and', groupId: 0 } }
  };
  const action = { type: ACTION_TYPES.AGGREGATION_RULES_ADD_GROUP };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_REMOVE_GROUP, a new group is added to the conditionGroups state', function(assert) {
  const initState = {
    conditionGroups: { '5': { id: 5 }, '7': { id: 7 } }
  };
  const expectedEndState = {
    conditionGroups: { '5': { id: 5 } }
  };
  const action = { type: ACTION_TYPES.AGGREGATION_RULES_REMOVE_GROUP, payload: 7 };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_UPDATE_CONDITION, the condition is updated', function(assert) {
  const initState = {
    conditions: { '7': { id: 7, property: 'alert.created', operator: '=' } }
  };
  const expectedEndState = {
    conditions: { '7': { id: 7, property: 'alert.type', operator: '!=' } }
  };
  const action = {
    type: ACTION_TYPES.AGGREGATION_RULES_UPDATE_CONDITION,
    payload: { conditionId: 7, changes: { property: 'alert.type', operator: '!=' } }
  };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_UPDATE_GROUP, the group is updated', function(assert) {
  const initState = {
    conditionGroups: { '5': { id: 5, logicalOperator: 'and' }, '7': { id: 7 } }
  };
  const expectedEndState = {
    conditionGroups: { '5': { id: 5, logicalOperator: 'or' }, '7': { id: 7 } }
  };
  const action = {
    type: ACTION_TYPES.AGGREGATION_RULES_UPDATE_GROUP,
    payload: { groupId: 5, changes: { logicalOperator: 'or' } }
  };
  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});