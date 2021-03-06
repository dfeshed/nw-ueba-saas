import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-shared/reducers/endpoint-query/reducer';
import * as ACTION_TYPES from 'investigate-shared/actions/types';

const initialState = Immutable.from({
  serverId: null,
  selectedMachineServerId: null
});

module('Unit | Reducers | Endpoint Query', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });
  test('ENDPOINT_SERVER_SELECTED setting the selected endpoint', function(assert) {
    const previous = Immutable.from({
      serverId: '7456s-wer123-q12345'
    });
    const result = reducer(previous, { type: ACTION_TYPES.ENDPOINT_SERVER_SELECTED, payload: '123qwe-5674' });

    assert.deepEqual(result.serverId, '123qwe-5674', 'setted the different endpoint');
  });

  test('SET_SELECTED_MACHINE_SERVER_ID setting the endpoint id from response', function(assert) {
    const previous = Immutable.from({
      selectedMachineServerId: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_MACHINE_SERVER_ID, payload: '123qwe-5674' });

    assert.equal(result.selectedMachineServerId, '123qwe-5674', 'endpoint id from response');
  });
});