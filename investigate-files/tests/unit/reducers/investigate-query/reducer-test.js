import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/investigate-query/reducer';
import * as ACTION_TYPES from 'investigate-files/actions/types';

const initialState = Immutable.from({
  serviceId: null,
  startTime: null,
  endTime: null,
  metaFilter: []
});

module('Unit | Reducers | Investigate Query', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });

  test('should set the investigate query input', function(assert) {
    const previous = Immutable.from({
      serviceId: null,
      startTime: null,
      endTime: null,
      metaFilter: []
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_QUERY_INPUT, payload: {
      serviceId: '23f8d359-a4a2-46f1-9637-9636b9c5e8d0',
      startTime: 1534330500,
      endTime: 1534935299
    } });
    assert.equal(result.serviceId, '23f8d359-a4a2-46f1-9637-9636b9c5e8d0');
  });
});
