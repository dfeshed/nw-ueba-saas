import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import * as ACTION_TYPES from 'configure/actions/types/content';
import reducer, { baselineSampleLog } from 'configure/reducers/content/log-parser-rules/reducer';
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
  isTransactionUnderway: false
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

test('With FETCH_DEVICE_TYPES, the success handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    deviceTypes: [{}, { name: 'my_type' }],
    deviceTypesStatus: 'completed'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_DEVICE_TYPES,
    payload: { data: [{ name: 'my_type' }] }
  });
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

test('With FETCH_DEVICE_CLASSES, the success handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    deviceClasses: ['Test Class'],
    deviceClassesStatus: 'completed'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_DEVICE_CLASSES,
    payload: { data: ['Test Class'] }
  });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With FETCH_METAS, the start handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    metasStatus: 'wait',
    metas: []
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_METAS });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With FETCH_METAS, the error handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    metasStatus: 'error'
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_METAS });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With FETCH_METAS, the success handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    metas: [{ metaName: 'device.ip', displayName: 'IPV4', format: 'IPv4' }],
    metasStatus: 'completed'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_METAS,
    payload: { data: [{ metaName: 'device.ip', displayName: 'IPV4', format: 'IPv4' }] }
  });
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


test('With HIGHLIGHT_SAMPLE_LOGS, the start handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    sampleLogsStatus: 'wait'
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.HIGHLIGHT_SAMPLE_LOGS });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With HIGHLIGHT_SAMPLE_LOGS, the error handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    sampleLogsStatus: 'error'
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.HIGHLIGHT_SAMPLE_LOGS });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With HIGHLIGHT_SAMPLE_LOGS, the success handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    sampleLogs: 'One two three',
    sampleLogsStatus: 'completed'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.HIGHLIGHT_SAMPLE_LOGS,
    payload: { data: 'One two three' }
  });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With UPDATE_SELECTED_PARSER_RULE, the selected rule is updated', function(assert) {
  const updatedRule = { name: 'ciscopix', pattern: { format: 'regex' } };
  const expectedEndState = {
    ...initialState,
    parserRules: [updatedRule, { name: 'foo2' }]
  };

  const action = {
    type: ACTION_TYPES.UPDATE_SELECTED_PARSER_RULE,
    payload: updatedRule
  };
  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With HIGHLIGHT_SAMPLE_LOGS, the base sample logs are returned if the hightlight response has no data', function(assert) {
  const expectedResult = {
    ...initialState,
    sampleLogs: baselineSampleLog,
    sampleLogsStatus: 'completed'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.HIGHLIGHT_SAMPLE_LOGS,
    payload: { }
  });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With FETCH_PARSER_RULES, the start handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    parserRules: [],
    parserRulesStatus: 'wait'
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_PARSER_RULES });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With FETCH_PARSER_RULES, the error handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    parserRulesStatus: 'error'
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_PARSER_RULES });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With FETCH_PARSER_RULES, the success handler updates state', function(assert) {
  const expectedResult = {
    ...initialState,
    parserRules: [{ name: 'ciscopix', literals: [{ 'value': 'ad.domain.dst ' }] }],
    parserRulesStatus: 'completed',
    selectedParserRuleIndex: 0
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_PARSER_RULES,
    payload: { data: [{ name: 'ciscopix', literals: [{ 'value': 'ad.domain.dst ' }] }] }
  });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});
