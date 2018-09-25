import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'configure/reducers/endpoint/server/reducer';
import * as ACTION_TYPES from 'configure/actions/types/endpoint';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';

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

  test('LIST_OF_ENDPOINT_SERVERS, the action has started', function(assert) {
    const expectedResult = {
      ...initialState,
      isServicesLoading: true
    };
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.LIST_OF_ENDPOINT_SERVERS });
    const result = reducer(initialState, action);
    assert.deepEqual(result, expectedResult);
  });

  test('LIST_OF_ENDPOINT_SERVERS, the action has errors', function(assert) {
    const expectedResult = {
      ...initialState,
      isServicesRetrieveError: true,
      isServicesLoading: false
    };
    const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.LIST_OF_ENDPOINT_SERVERS });
    const result = reducer(initialState, action);
    assert.deepEqual(result, expectedResult);
  });

  test('LIST_OF_ENDPOINT_SERVERS, the action is successfull', function(assert) {
    const expectedResult = {
      ...initialState,
      serviceData: [{ 'displayName': 'EPS-1', 'ip': '10.40.12.8', 'id': '123' }],
      isServicesLoading: false,
      isServicesRetrieveError: false
    };
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.LIST_OF_ENDPOINT_SERVERS,
      payload: {
        data: [{ 'displayName': 'EPS-1', 'ip': '10.40.12.8', 'id': '123' }]
      }
    });
    const result = reducer(initialState, action);
    assert.deepEqual(result, expectedResult);
  });

});
