import { test, module } from 'qunit';
import reducer from 'context/reducers/context/reducer';
import * as ACTION_TYPES from 'context/actions/types';
import Immutable from 'seamless-immutable';
import lookupData from '../../../data/subscriptions/context/stream/data/ip';
import { entitiesMetas } from '../../../data/subscriptions/entity-meta/findAll/data';

module('Unit | Reducers | preferences-panel | Preferences');

const initialState = Immutable.from({
  meta: null,
  lookupKey: null,
  errorMessage: null,
  lookupData: [{}],
  entitiesMetas: null,
  isClicked: false
});

const resultState = Immutable.from({
  meta: 'ip',
  lookupKey: '10.10.10.10',
  errorMessage: null,
  lookupData,
  entitiesMetas,
  isClicked: true
});

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

test('test INITIALIZE_CONTEXT_PANEL', function(assert) {
  result = reducerFunction(ACTION_TYPES.INITIALIZE_CONTEXT_PANEL, resultState);
  assert.equal(result.lookupKey, '10.10.10.10');
  assert.equal(result.meta, 'ip');
});

test('test GET_CONTEXT_ENTITIES_METAS', function(assert) {
  result = reducerFunction(ACTION_TYPES.GET_CONTEXT_ENTITIES_METAS, entitiesMetas);
  assert.deepEqual(result.entitiesMetas, entitiesMetas);
});

test('test CONTEXT_ERROR', function(assert) {
  result = reducerFunction(ACTION_TYPES.CONTEXT_ERROR, 'Some Error errorMessage');
  assert.equal(result.errorMessage, 'Some Error errorMessage');
});

test('test UPDATE_PANEL_CLICKED', function(assert) {
  result = reducerFunction(ACTION_TYPES.UPDATE_PANEL_CLICKED, true);
  assert.ok(result.isClicked);
});

test('test GET_LOOKUP_DATA', function(assert) {
  result = reducerFunction(ACTION_TYPES.GET_LOOKUP_DATA, lookupData);
  assert.equal(result.lookupData[0].Alerts.resultList.length, 15);
  assert.equal(result.lookupData[0].Incidents.resultList.length, 14);
  assert.equal(result.lookupData[0].Archer.resultList.length, 1);
  assert.equal(result.lookupData[0].LIST.resultList.length, 5);
  assert.equal(result.lookupData[0].Machines.resultList.length, 1);
  assert.equal(result.lookupData[0].IOC.resultList.length, 7);
  assert.equal(result.lookupData[0].Modules.resultList.length, 5);
});
