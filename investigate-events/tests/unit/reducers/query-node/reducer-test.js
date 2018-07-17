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

test('ACTION_TYPES.REHYDRATE reducer when url has a serviceId and localStorage has a different serviceId', function(assert) {
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

test('ACTION_TYPES.REHYDRATE reducer when url does not have a serviceId while the localStorage has one', function(assert) {
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

test('SET_PREFERENCES when payload contains queryTimeFormat', function(assert) {

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

test('SET_PREFERENCES when payload does not contain queryTimeFormat', function(assert) {

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

test('SET_PREFERENCES when payload does not have queryTimeFormat and no current value set for queryTimeFormat', function(assert) {

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

test('SET_QUERY_VIEW reducer sets the correct mode provided', function(assert) {
  const prevState = Immutable.from({
    queryView: 'nextGen'
  });
  const action = {
    type: ACTION_TYPES.SET_QUERY_VIEW,
    payload: {
      queryView: 'freeForm'
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryView, 'freeForm');
});

test('INITIALIZE_INVESTIGATE reducer sets the correct view from localStorage', function(assert) {
  /* INTENT- overwrites queryView */
  const prevState = Immutable.from({
    queryView: 'freeForm',
    previouslySelectedTimeRanges: { 2: 'LAST_24_HOURS' }
  });
  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
    payload: {
      queryParams: {
        metaFilter: {},
        selectedTimeRangeId: 'ALL_DATA'
      }
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryView, 'freeForm');
});

test('ACTION_TYPES.ADD_NEXT_GEN_PILL sets query to dirty', function(assert) {
  const prevState = Immutable.from({
    isDirty: false
  });

  const action = {
    type: ACTION_TYPES.ADD_NEXT_GEN_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 0
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.isDirty, true, 'isDirty is set correctly');
});

test('ACTION_TYPES.DELETE_NEXT_GEN_PILLS sets query to dirty', function(assert) {
  const prevState = Immutable.from({
    isDirty: false
  });

  const action = {
    type: ACTION_TYPES.DELETE_NEXT_GEN_PILLS,
    payload: {
      pillData: { id: 1 }
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.isDirty, true, 'isDirty is set correctly');
});

test('ACTION_TYPES.EDIT_NEXT_GEN_PILL sets query to dirty', function(assert) {
  const prevState = Immutable.from({
    isDirty: false
  });

  const action = {
    type: ACTION_TYPES.EDIT_NEXT_GEN_PILL,
    payload: {
      pillData: { id: 1 }
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.isDirty, true, 'isDirty is set correctly');
});