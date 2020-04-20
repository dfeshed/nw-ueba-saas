import { module, test } from 'qunit';
import ACTION_TYPES from 'configure/actions/types/respond';
import reducer, { initialState } from 'configure/reducers/respond/risk-scoring/reducer';
import riskScoring from '../../../data/subscriptions/risk/score/settings/findAll/data';
import { normalizedState } from '../../../integration/component/respond/risk-scoring/data';

const { configure: { respond: { riskScoring: { riskScoringSettings } } } } = normalizedState;

module('Unit | Utility | Respond Risk Scoring Reducers');

test('With FETCH_RISK_SCORING_SETTINGS_STARTED, riskScoringStatus is properly set', function(assert) {
  assert.expect(1);

  const action = {
    type: ACTION_TYPES.FETCH_RISK_SCORING_SETTINGS_STARTED
  };

  const expected = {
    ...initialState,
    riskScoringStatus: 'wait'
  };

  const state = {
    ...initialState,
    riskScoringStatus: null
  };

  assert.deepEqual(reducer(state, action), expected);
});

test('With FETCH_RISK_SCORING_SETTINGS, state is properly set', function(assert) {
  assert.expect(1);

  const action = {
    type: ACTION_TYPES.FETCH_RISK_SCORING_SETTINGS,
    payload: {
      data: riskScoring
    }
  };

  const expected = {
    riskScoringStatus: 'completed',
    riskScoringExpanded: false,
    isTransactionUnderway: false,
    riskScoringSettings
  };

  const state = {
    ...initialState,
    riskScoringExpanded: false,
    riskScoringStatus: null,
    riskScoringSettings: {}
  };

  assert.deepEqual(reducer(state, action), expected);
});

test('With FETCH_RISK_SCORING_SETTINGS_FAILED, riskScoringStatus is properly set', function(assert) {
  assert.expect(1);

  const action = {
    type: ACTION_TYPES.FETCH_RISK_SCORING_SETTINGS_FAILED
  };

  const expected = {
    ...initialState,
    riskScoringStatus: 'error'
  };

  const state = {
    ...initialState,
    riskScoringStatus: null
  };

  assert.deepEqual(reducer(state, action), expected);
});

test('With TOGGLE_RISK_SCORING_EXPANDED, riskScoringExpanded is properly set', function(assert) {
  assert.expect(2);

  const action = {
    type: ACTION_TYPES.TOGGLE_RISK_SCORING_EXPANDED
  };

  const expected = {
    ...initialState,
    riskScoringExpanded: true
  };

  const state = {
    ...initialState,
    riskScoringExpanded: false
  };

  assert.deepEqual(reducer(state, action), expected);

  assert.deepEqual(reducer(expected, action), state);
});

test('initial state is frozen and will throw error when mutated', function(assert) {
  assert.expect(1);

  let exception;

  try {
    initialState.foobar = '123';
  } catch (e) {
    exception = e.message;
  }

  assert.ok(exception.indexOf('object is not extensible') !== -1);
});

test('With UPDATE_RISK_SCORING_SETTINGS, state is properly set', function(assert) {
  assert.expect(1);

  const action = {
    type: ACTION_TYPES.UPDATE_RISK_SCORING_SETTINGS,
    payload: {
      data: riskScoring
    }
  };

  const expected = {
    riskScoringStatus: null,
    riskScoringExpanded: false,
    isTransactionUnderway: false,
    riskScoringSettings
  };

  const state = {
    ...initialState,
    riskScoringExpanded: false,
    isTransactionUnderway: true,
    riskScoringSettings: {}
  };

  assert.deepEqual(reducer(state, action), expected);
});

test('With UPDATE_RISK_SCORING_SETTINGS_STARTED, isTransactionUnderway is properly set', function(assert) {
  assert.expect(1);

  const action = {
    type: ACTION_TYPES.UPDATE_RISK_SCORING_SETTINGS_STARTED
  };

  const expected = {
    ...initialState,
    isTransactionUnderway: true
  };

  const state = {
    ...initialState,
    isTransactionUnderway: false
  };

  assert.deepEqual(reducer(state, action), expected);
});

test('With UPDATE_RISK_SCORING_SETTINGS_FAILED, isTransactionUnderway is properly set', function(assert) {
  assert.expect(1);

  const action = {
    type: ACTION_TYPES.UPDATE_RISK_SCORING_SETTINGS_FAILED
  };

  const expected = {
    ...initialState,
    isTransactionUnderway: false
  };

  const state = {
    ...initialState,
    isTransactionUnderway: true
  };

  assert.deepEqual(reducer(state, action), expected);
});
