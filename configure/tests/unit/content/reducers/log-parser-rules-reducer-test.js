import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import * as ACTION_TYPES from 'configure/actions/types/content';
import reducer from 'configure/reducers/content/log-parser-rules/reducer';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Utility | log-parser-rules Reducer');

const initialState = Immutable.from({
  logParsers: [{ name: 'ciscopix' }],
  selectedParserRuleIndex: 0,
  parserRules: [{ name: 'foo' }, { name: 'foo2' }],
  deleteRuleStatus: null,
  deviceTypes: [],
  deviceTypesStatus: null,
  deviceClasses: [],
  deviceClassesStatus: null,
  isTransactionUnderway: false,
  parserRuleTokens: [{ 'value': 'foo' }]
});

test('With FETCH_DEVICE_TYPES, the start handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    deviceTypesStatus: 'wait'
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_DEVICE_TYPES });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With FETCH_DEVICE_TYPES, the error handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    deviceTypesStatus: 'error'
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_DEVICE_TYPES });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With FETCH_DEVICE_CLASSES, the start handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    deviceClassesStatus: 'wait'
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_DEVICE_CLASSES });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With FETCH_DEVICE_CLASSES, the error handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    deviceClassesStatus: 'error'
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_DEVICE_CLASSES });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With ADD_LOG_PARSER, the start handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    isTransactionUnderway: true
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.ADD_LOG_PARSER });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With ADD_LOG_PARSER, the error handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    isTransactionUnderway: false
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.ADD_LOG_PARSER });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With ADD_LOG_PARSER, the success handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    logParsers: [{ name: 'ciscopix' }, { name: 'test' }],
    selectedLogParserIndex: 1,
    isTransactionUnderway: false
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.ADD_LOG_PARSER, payload: { data: { name: 'test' } } });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With DELETE_LOG_PARSER, the start handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    isTransactionUnderway: true
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.DELETE_LOG_PARSER });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With DELETE_LOG_PARSER, the error handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    isTransactionUnderway: false
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.DELETE_LOG_PARSER });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With DELETE_LOG_PARSER, the success handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    logParsers: [],
    selectedLogParserIndex: 0,
    isTransactionUnderway: false
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.DELETE_LOG_PARSER, payload: { data: { name: 'ciscopix' } } });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With DELETE_PARSER_RULE, the action has started', function(assert) {
  const expectedResult = {
    ...initialState,
    deleteRuleStatus: 'wait'
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.DELETE_PARSER_RULE });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With DELETE_PARSER_RULE, the action has errors', function(assert) {
  const expectedResult = {
    ...initialState,
    deleteRuleStatus: 'error'
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.DELETE_PARSER_RULE });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With DELETE_PARSER_RULE, the action is successfull', function(assert) {
  const expectedResult = {
    ...initialState,
    selectedParserRuleIndex: 0,
    parserRules: [{ name: 'foo2' }],
    deleteRuleStatus: 'completed'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.DELETE_PARSER_RULE });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With ADD_NEW_PARSER_RULE, the action is successfull', function(assert) {
  const expectedResult = {
    ...initialState,
    selectedParserRuleIndex: 2,
    parserRules: [
      { name: 'foo' },
      { name: 'foo2' },
      {
        name: '123',
        literals: [],
        pattern: {
          captures: [
            {
            }
          ],
          regex: ''
        },
        ruleMetas: [],
        dirty: true,
        outOfBox: false,
        override: false
      }
    ]
  };
  const payload = '123';
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.ADD_NEW_PARSER_RULE, payload });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With SAVE_PARSER_RULE, the action has started', function(assert) {
  const expectedResult = {
    ...initialState,
    saveRuleStatus: 'wait'
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.SAVE_PARSER_RULE });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With SAVE_PARSER_RULE, the action has errors', function(assert) {
  const expectedResult = {
    ...initialState,
    saveRuleStatus: 'error'
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.SAVE_PARSER_RULE });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With SAVE_PARSER_RULE, the action is successfull', function(assert) {
  const expectedResult = {
    ...initialState,
    saveRuleStatus: 'completed'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.SAVE_PARSER_RULE });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With ACTION_TYPES.ADD_RULE_TOKEN, the action is successfull', function(assert) {
  const expectedResult = {
    ...initialState,
    parserRuleTokens: [{ 'value': 'foo2' }, { 'value': 'foo' }]
  };
  const payload = 'foo2';
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.ADD_RULE_TOKEN, payload });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With ACTION_TYPES.DELETE_RULE_TOKEN, the action is successfull', function(assert) {
  const expectedResult = {
    ...initialState,
    parserRuleTokens: []
  };
  const payload = 0;
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.DELETE_RULE_TOKEN, payload });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With ACTION_TYPES.EDIT_RULE_TOKEN, the action is successfull', function(assert) {
  const expectedResult = {
    ...initialState,
    parserRuleTokens: [{ 'value': 'fooX' }]
  };
  const payload = { index: 0, token: 'fooX' };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.EDIT_RULE_TOKEN, payload });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With ACTION_TYPES.ADD_RULE_TOKEN, add existing token', function(assert) {
  const expectedResult = {
    ...initialState
  };
  const payload = 'foo';
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.ADD_RULE_TOKEN, payload });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});
