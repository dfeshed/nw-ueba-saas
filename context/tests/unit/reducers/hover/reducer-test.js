import { test, module } from 'qunit';
import reducer from 'context/reducers/hover/reducer';
import * as ACTION_TYPES from 'context/actions/types';
import Immutable from 'seamless-immutable';

module('Unit | Reducers | hover');

const entitySummary = [
  { name: 'Archer', count: null, url: 'http://localhost:4200/' },
  { name: 'Incidents', count: '5' },
  { name: 'Alerts', count: '5' },
  { name: 'LIST', count: '5' },
  { name: 'Users', count: '5' },
  { name: 'IOC', count: '5' },
  { name: 'Modules', count: '5' },
  { name: 'Machines', count: null, severity: 'HIGH' }
];

const initialState = Immutable.from({
  modelSummary: null
});

const resultState = Immutable.from({
  modelSummary: entitySummary
});

let result;

const reducerFunction = (type, payload) => {
  const action = {
    type,
    payload
  };
  return reducer(initialState, action);
};

test('test GET_SUMMARY_DATA', function(assert) {
  result = reducerFunction(ACTION_TYPES.GET_SUMMARY_DATA, resultState);
  assert.deepEqual(result.modelSummary, resultState);
});
