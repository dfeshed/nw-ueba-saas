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
    queryView: 'guided',
    toggledOnceFlag: false
  });
  const action = {
    type: ACTION_TYPES.SET_QUERY_VIEW,
    payload: {
      queryView: 'freeForm',
      toggledOnceFlag: true
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryView, 'freeForm');
  assert.equal(result.toggledOnceFlag, true);
});

test('test INITIALIZE_INVESTIGATE reducer sets the correct view from localStorage', function(assert) {
  /* INTENT- overwrites queryView */
  const prevState = Immutable.from({
    queryView: 'freeForm',
    previouslySelectedTimeRanges: { 2: 'LAST_24_HOURS' }
  });
  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
    payload: {
      metaFilter: {},
      selectedTimeRangeId: 'ALL_DATA'
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryView, 'freeForm');
});