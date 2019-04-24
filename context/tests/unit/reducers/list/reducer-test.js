import { test, module } from 'qunit';
import reducer from 'context/reducers/list/reducer';
import * as ACTION_TYPES from 'context/actions/types';
import Immutable from 'seamless-immutable';

module('Unit | Reducers | Add-to-list');

const initialState = Immutable.from({
  isListView: true,
  errorMessage: null,
  list: [],
  entityType: null
});

const resultState = Immutable.from({
  isListView: true,
  errorMessage: 'context.error',
  list: [{
    enabled: true,
    name: 'test'
  }],
  entityType: {
    type: 'IP',
    id: '10.10.10.10'
  }
});

let result;

const reducerFunction = (type, payload) => {
  const action = {
    type,
    payload
  };
  return reducer(initialState, action);
};

test('test INITIALIZE_ADD_TO_LIST_PARAM', function(assert) {
  result = reducerFunction(ACTION_TYPES.INITIALIZE_ADD_TO_LIST_PARAM, resultState.entityType);
  assert.deepEqual(result.entityType, resultState.entityType);
});

test('test TOGGLE_LIST_VIEW', function(assert) {
  result = reducerFunction(ACTION_TYPES.TOGGLE_LIST_VIEW, initialState.isListView);
  assert.deepEqual(result.isListView, true);
});

test('test SET_ALL_LIST', function(assert) {
  result = reducerFunction(ACTION_TYPES.SET_ALL_LIST, resultState.list);
  assert.equal(result.list, resultState.list);
});

test('test CREATE_LIST', function(assert) {
  result = reducerFunction(ACTION_TYPES.CREATE_LIST, resultState.list);
  assert.deepEqual(result.list, resultState.list);
});

test('test RESET_ERROR', function(assert) {
  result = reducerFunction(ACTION_TYPES.RESET_ERROR, initialState);
  assert.equal(result.errorMessage, null);
});

test('test LIST_ERROR', function(assert) {
  result = reducerFunction(ACTION_TYPES.LIST_ERROR, resultState.errorMessage);
  assert.equal(result.errorMessage, 'context.error');
});