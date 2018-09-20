import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'configure/reducers/endpoint/server/reducer';
import * as ACTION_TYPES from 'configure/actions/types/endpoint';

const initialState = Immutable.from({
  isServicesLoading: undefined,
  isServicesRetrieveError: undefined,
  serviceData: undefined,
  isSummaryRetrieveError: undefined
});

module('Unit | Reducers | Endpoint Server', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });
  test('ENDPOINT_SERVER_STATUS for setting the endpoint', function(assert) {
    const previous = Immutable.from({
      isSummaryRetrieveError: false
    });
    const result = reducer(previous, { type: ACTION_TYPES.ENDPOINT_SERVER_STATUS, payload: true });

    assert.deepEqual(result.isSummaryRetrieveError, true, 'endpoint server is offline');
  });
});
