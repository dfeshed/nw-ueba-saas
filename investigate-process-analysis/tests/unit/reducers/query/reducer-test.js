import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-process-analysis/reducers/query/reducer';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';

const initialState = Immutable.from({
  serviceId: null,
  startTime: null,
  endTime: null
});

module('Unit | Reducers | Query', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });

  test('SERVICE_SELECTED set the selected service', function(assert) {
    const previous = Immutable.from({
      serviceId: 1
    });
    const result = reducer(previous, { type: ACTION_TYPES.SERVICE_SELECTED, payload: 2 });
    assert.equal(result.serviceId, 2, 'Expected to set correct service id');
  });

  test('SET_QUERY_TIME_RANGE set time range', function(assert) {
    const previous = Immutable.from({
      startTime: null,
      endTime: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_QUERY_TIME_RANGE, payload: { startTime: 1, endTime: 2 } });
    assert.equal(result.startTime, 1, 'Correct Start Time');
    assert.equal(result.endTime, 2, 'Correct End Time');
  });

  test('SET_PROCESS_ANALYSIS_INPUT sets the query params to state', function(assert) {
    const previous = Immutable.from({
      serviceId: null,
      startTime: null,
      endTime: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_PROCESS_ANALYSIS_INPUT, payload: { sid: 1, st: 2, et: 3 } });
    assert.equal(result.serviceId, 1);
    assert.equal(result.startTime, 2);
    assert.equal(result.endTime, 3);
  });

});
