import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import ACTION_TYPES from 'respond/actions/types';
import reducer from 'respond/reducers/respond/aggregation-rules/aggregation-rules';
import rules from '../../data/subscriptions/aggregation-rules/findAll/data';

module('Unit | Utility | Aggregation Rules Reducers');

const initialState = {
  rules: [],
  rulesStatus: null,
  selectedRule: null
};

test('With FETCH_AGGREGATION_RULES_STARTED, the rulesState is properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    rulesStatus: 'wait'
  };

  const endState = reducer(Immutable.from(initialState), {
    type: ACTION_TYPES.FETCH_AGGREGATION_RULES_STARTED
  });
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_AGGREGATION_RULES, the rules are properly updated', function(assert) {
  const payload = { data: rules };
  const action = {
    type: ACTION_TYPES.FETCH_AGGREGATION_RULES,
    payload
  };

  const expectedEndState = {
    ...initialState,
    rules: payload.data,
    rulesStatus: 'complete'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_AGGREGATION_RULES_FAILED, the ruleState is properly set', function(assert) {
  const action = {
    type: ACTION_TYPES.FETCH_AGGREGATION_RULES_FAILED
  };
  const expectedEndState = {
    ...initialState,
    rulesStatus: 'error'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_SELECT_RULE, the selectedRule is properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    selectedRule: '1234'
  };

  const endState = reducer(Immutable.from(initialState), {
    type: ACTION_TYPES.AGGREGATION_RULES_SELECT_RULE,
    payload: '1234'
  });
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_SELECT_RULE, the selectedRule is removed if the rule ID is already set', function(assert) {
  const initState = {
    ...initialState,
    selectedRule: '1234'
  };
  const expectedEndState = {
    ...initialState,
    selectedRule: null
  };

  const endState = reducer(Immutable.from(initState), {
    type: ACTION_TYPES.AGGREGATION_RULES_SELECT_RULE,
    payload: '1234'
  });
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_DELETE_STARTED, the isTransactionUnderway prop is properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    isTransactionUnderway: true
  };

  const endState = reducer(Immutable.from(initialState), {
    type: ACTION_TYPES.AGGREGATION_RULES_DELETE_STARTED
  });
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_DELETE, the rules are properly updated', function(assert) {
  const initState = {
    ...initialState,
    rules: [{ id: '54321', order: 1 }, { id: '12345', order: 2 }, { id: '787933', order: 3 }],
    isTransactionUnderway: true,
    selectedRule: '54321'
  };
  const payload = { data: { id: '54321' } };
  const action = {
    type: ACTION_TYPES.AGGREGATION_RULES_DELETE,
    payload
  };

  const expectedEndState = {
    ...initialState,
    rules: [{ id: '12345', order: 1 }, { id: '787933', order: 2 }], // the order must be properly updated on all non-deleted items
    isTransactionUnderway: false,
    selectedRule: null
  };

  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_DELETE_FAILED, the isTransactionUnderway prop is properly set', function(assert) {
  const initState = { ...initialState, isTransactionUnderway: true };
  const action = {
    type: ACTION_TYPES.AGGREGATION_RULES_DELETE_FAILED
  };
  const expectedEndState = {
    ...initialState,
    isTransactionUnderway: false
  };

  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_REORDER_STARTED, the isTransactionUnderway prop is properly set', function(assert) {
  const initState = { ...initialState };
  const action = {
    type: ACTION_TYPES.AGGREGATION_RULES_REORDER_STARTED
  };
  const expectedEndState = {
    ...initialState,
    isTransactionUnderway: true
  };

  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_REORDER_FAILED, the isTransactionUnderway prop is properly set', function(assert) {
  const initState = { ...initialState, isTransactionUnderway: true };
  const action = {
    type: ACTION_TYPES.AGGREGATION_RULES_REORDER_FAILED
  };
  const expectedEndState = {
    ...initialState,
    isTransactionUnderway: false
  };

  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});


test('With AGGREGATION_RULES_REORDER, the rules are properly updated', function(assert) {
  const initState = {
    ...initialState,
    rules: [{ id: '1' }, { id: '2' }],
    isTransactionUnderway: true
  };
  const updatedRules = [{ id: '2' }, { id: '1' }];
  const action = {
    type: ACTION_TYPES.AGGREGATION_RULES_REORDER,
    payload: { data: updatedRules }
  };
  const expectedEndState = {
    ...initialState,
    rules: updatedRules,
    isTransactionUnderway: false
  };

  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});


test('With AGGREGATION_RULES_CLONE_STARTED, the isTransactionUnderway prop is set to true', function(assert) {
  const initState = { ...initialState };
  const action = {
    type: ACTION_TYPES.AGGREGATION_RULES_CLONE_STARTED
  };
  const expectedEndState = {
    ...initialState,
    isTransactionUnderway: true
  };

  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_CLONE, the isTransactionUnderway prop is set to false', function(assert) {
  const initState = { ...initialState };
  const action = {
    type: ACTION_TYPES.AGGREGATION_RULES_CLONE
  };
  const expectedEndState = {
    ...initialState,
    isTransactionUnderway: false
  };

  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With AGGREGATION_RULES_CLONE_FAILED, the isTransactionUnderway prop is set to false', function(assert) {
  const initState = { ...initialState, isTransactionUnderway: true };
  const action = {
    type: ACTION_TYPES.AGGREGATION_RULES_CLONE_FAILED
  };
  const expectedEndState = {
    ...initialState,
    isTransactionUnderway: false
  };

  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});