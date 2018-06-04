import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import * as ACTION_TYPES from 'configure/actions/types/content';
import reducer from 'configure/reducers/content/log-parser-rules/reducer';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Utility | log-parser-rules Reducer');

const initialState = Immutable.from({
  selectedParserRuleIndex: 0,
  parserRules: [{ name: 'foo' }, { name: 'foo2' }],
  deleteRuleStatus: null
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