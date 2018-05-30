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
    selectedParserRuleIndex: -1,
    parserRules: [{ name: 'foo2' }],
    deleteRuleStatus: 'completed'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.DELETE_PARSER_RULE });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});
