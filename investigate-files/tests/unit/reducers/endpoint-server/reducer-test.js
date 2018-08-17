import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/endpoint-server/reducer';

const initialState = Immutable.from({
  isServicesLoading: undefined,
  isServicesRetrieveError: undefined,
  serviceData: undefined
});

module('Unit | Reducers | Endpoint Server', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });
});
