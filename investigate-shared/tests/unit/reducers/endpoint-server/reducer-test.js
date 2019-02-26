import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-shared/reducers/endpoint-server/reducer';
import * as ACTION_TYPES from 'investigate-shared/actions/types';
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

  test('LIST_OF_ENDPOINT_SERVERS for setting isServicesLoading on start', function(assert) {
    const previous = Immutable.from({
      isServicesLoading: undefined
    });
    const newAction = makePackAction(LIFECYCLE.START, {
      type: ACTION_TYPES.LIST_OF_ENDPOINT_SERVERS,
      payload: {}
    });
    const result = reducer(previous, newAction);
    assert.deepEqual(result.isServicesLoading, true, 'endpoint server is loading');
  });

  test('LIST_OF_ENDPOINT_SERVERS for setting isServicesLoading on failure', function(assert) {
    const previous = Immutable.from({
      isServicesLoading: undefined,
      isServicesRetrieveError: undefined
    });
    const newAction = makePackAction(LIFECYCLE.FAILURE, {
      type: ACTION_TYPES.LIST_OF_ENDPOINT_SERVERS,
      payload: {}
    });
    const result = reducer(previous, newAction);
    assert.deepEqual(result.isServicesLoading, false, 'isServicesLoading is set to false');
    assert.deepEqual(result.isServicesRetrieveError, true, 'isServicesRetrieveError set to true');
  });

  test('LIST_OF_ENDPOINT_SERVERS for setting isServicesLoading on success', function(assert) {
    const testPayload = {
      data: [{
        displayName: 'endpointloghybrid1 - Endpoint Server'
      },
      {
        displayName: 'testEndpoint'
      }]
    };
    const previous = Immutable.from({
      isServicesLoading: undefined,
      isServicesRetrieveError: undefined,
      serviceData: undefined
    });
    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.LIST_OF_ENDPOINT_SERVERS,
      payload: testPayload
    });
    const result = reducer(previous, newAction);
    assert.deepEqual(result.isServicesLoading, false, 'isServicesLoading is set to false');
    assert.deepEqual(result.isServicesRetrieveError, false, 'isServicesRetrieveError set to true');
    assert.deepEqual(result.serviceData, testPayload.data, 'serviceData is set');
  });
});
