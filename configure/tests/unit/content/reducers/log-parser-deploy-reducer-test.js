import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import * as ACTION_TYPES from 'configure/actions/types/content';
import reducer from 'configure/reducers/content/log-parser-rules/reducer';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Utility | log-parser-deploy Reducer');

const initialState = Immutable.from({
  deployLogParserStatus: null,
  logParsers: [{ name: 'ciscopix', dirty: true }],
  selectedParserRuleIndex: 0,
  selectedLogParserIndex: 0,
  parserRules: [{ name: 'foo', dirty: true }, { name: 'foo2', dirty: true }]
});

test('With DEPLOY_LOG_PARSER, the action has started', function(assert) {
  const expectedResult = {
    ...initialState,
    deployLogParserStatus: 'wait'
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.DEPLOY_LOG_PARSER });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With DEPLOY_LOG_PARSER, the action has errors', function(assert) {
  const expectedResult = {
    ...initialState,
    deployLogParserStatus: 'error'
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.DEPLOY_LOG_PARSER });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});

test('With DEPLOY_LOG_PARSER, the action is successfull', function(assert) {
  const expectedResult = {
    ...initialState,
    logParsers: [{ name: 'ciscopix', dirty: false }],
    parserRules: [{ name: 'foo', dirty: false }, { name: 'foo2', dirty: false }],
    parserRulesOriginal: [{ name: 'foo', dirty: false }, { name: 'foo2', dirty: false }],
    deployLogParserStatus: 'completed'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.DEPLOY_LOG_PARSER,
    payload: {
      data: 'COMPLETE',
      parserRules: [{ name: 'foo', dirty: false }, { name: 'foo2', dirty: false }],
      logDeviceParser: { name: 'ciscopix', dirty: false }
    }
  });
  const result = reducer(initialState, action);
  assert.deepEqual(result, expectedResult);
});
