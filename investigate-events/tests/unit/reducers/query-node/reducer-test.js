import { test, module } from 'qunit';
import reducer from 'investigate-events/reducers/investigate/query-node/reducer';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import Immutable from 'seamless-immutable';

module('Unit | Reducers | query-node | Investigate');

const urlParsedParamsState = Immutable.from({
  serviceId: '2',
  previouslySelectedTimeRanges: {}
});

const noParamsInState = Immutable.from({
  previouslySelectedTimeRanges: {}
});

test('test ACTION_TYPES.REHYDRATE reducer when url has a serviceId and localStorage has a different serviceId', function(assert) {
  const action = {
    type: ACTION_TYPES.REHYDRATE,
    payload: {
      investigate: {
        queryNode: {
          previouslySelectedTimeRanges: {},
          serviceId: '5'
        }
      }
    }
  };
  const result = reducer(urlParsedParamsState, action);

  assert.equal(result.serviceId, '2');
});

test('test ACTION_TYPES.REHYDRATE reducer when url does not have a serviceId while the localStorage has one', function(assert) {
  const action = {
    type: ACTION_TYPES.REHYDRATE,
    payload: {
      investigate: {
        queryNode: {
          previouslySelectedTimeRanges: {},
          serviceId: '5'
        }
      }
    }
  };
  const result = reducer(noParamsInState, action);

  assert.equal(result.serviceId, '5');
});

test('test SET_PREFERENCES when payload contains queryTimeFormat', function(assert) {

  const prevState = Immutable.from({
    queryTimeFormat: null
  });
  const action = {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: {
      queryTimeFormat: 'DB'
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryTimeFormat, 'DB');
});

test('test SET_PREFERENCES when payload does not contain queryTimeFormat', function(assert) {

  const prevState = Immutable.from({
    queryTimeFormat: 'WALL'
  });
  const action = {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: { }
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryTimeFormat, 'WALL');
});

test('test SET_PREFERENCES when payload does not have queryTimeFormat and no current value set for queryTimeFormat', function(assert) {

  const prevState = Immutable.from({
    queryTimeFormat: undefined
  });
  const action = {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: { }
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryTimeFormat, undefined);
});

test('test SET_QUERY_VIEW reducer sets the correct mode provided', function(assert) {
  const prevState = Immutable.from({
    queryView: 'guided'
  });
  const action = {
    type: ACTION_TYPES.SET_QUERY_VIEW,
    payload: 'freeForm'
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryView, 'freeForm');
});

test('test SET_FREE_FORM_TEXT reducer sets the correct text query provided', function(assert) {
  const prevState = Immutable.from({
    freeFormText: ''
  });
  const action = {
    type: ACTION_TYPES.SET_FREE_FORM_TEXT,
    payload: 'medium = 1'
  };
  const result = reducer(prevState, action);

  assert.equal(result.freeFormText, 'medium = 1');
});

test('test TOGGLE_FOCUS_FLAG reducer sets the correct flag provided', function(assert) {
  const prevState = Immutable.from({
    toggledOnceFlag: false
  });
  const action = {
    type: ACTION_TYPES.TOGGLE_FOCUS_FLAG,
    payload: true
  };
  const result = reducer(prevState, action);

  assert.equal(result.toggledOnceFlag, true);
});