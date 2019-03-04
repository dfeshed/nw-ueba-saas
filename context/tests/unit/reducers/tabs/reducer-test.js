import { test, module } from 'qunit';
import reducer from 'context/reducers/tabs/reducer';
import * as ACTION_TYPES from 'context/actions/types';
import Immutable from 'seamless-immutable';

module('Unit | Reducers | tabs');

const initialState = Immutable.from({
  meta: null,
  activeTabName: 'LIST',
  tabs: null,
  headerButtons: null,
  dataSources: null
});

const endpointDataSource = {
  class: 'graph',
  dataSourceType: 'Endpoint',
  datasourceGroup: 'Endpoint',
  displayType: 'table',
  field: 'Endpoint',
  tabRequired: true,
  title: 'context.header.endpoint'
};

let result;

const reducerFunction = (type, payload) => {
  const action = {
    type,
    payload
  };
  return reducer(initialState, action);
};

test('test RESTORE_DEFAULT', function(assert) {
  result = reducerFunction(ACTION_TYPES.RESTORE_DEFAULT, null);
  assert.deepEqual(result, initialState);
});

test('test UPDATE_ACTIVE_TAB', function(assert) {
  result = reducerFunction(ACTION_TYPES.UPDATE_ACTIVE_TAB, 'Alerts');
  assert.equal(result.activeTabName, 'Alerts');
});

test('test GET_ALL_DATA_SOURCES', function(assert) {
  const state = Immutable.from({
    meta: 'IP',
    activeTabName: 'LIST',
    tabs: null,
    headerButtons: null,
    dataSources: null
  });
  result = reducer(state, { type: ACTION_TYPES.GET_ALL_DATA_SOURCES, payload: [endpointDataSource] });
  assert.equal(result.meta, 'IP');
  assert.equal(result.dataSources.length, 1);
});
