import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import ACTION_TYPES from 'investigate-events/actions/types';
import ReduxDataHelper from '../../helpers/redux-data-helper';
import initializationCreators from 'investigate-events/actions/initialization-creators';
import { serviceData } from './data';
import { patchReducer } from '../../helpers/vnext-patch';

module('Unit | Actions | Initialization-Creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('_isServiceIdPresent return false when service is not present in the list', function(assert) {
    const serviceId = '1';
    assert.notOk(initializationCreators._isServiceIdPresent(serviceId, serviceData));
  });

  test('_isServiceIdPresent return true when service is present in the list', function(assert) {
    const serviceId = '555d9a6fe4b0d37c827d402d';
    assert.ok(initializationCreators._isServiceIdPresent(serviceId, serviceData));
  });

  // Concept: When services are retrieved, we check if the service that is stored in state(through localStorage)
  // is present in the list of services retrieved. If not, we pick the first service by default.
  // Before kicking off this test, state had serviceId 10, but because it wasn't found in the list retrieved
  // through the mocked web socket call, we picked the first one.
  test('getServices - Should pick first service from list if one present in state is no longer available', async function(assert) {
    const done = assert.async();
    assert.expect(5);
    let count = 0;
    let fetchedServiceId;
    const getState = () => {
      return new ReduxDataHelper((state) => patchReducer(this, state)).language().serviceId('10').isServicesLoading(false).build();
    };

    const serviceSummaryDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SUMMARY_RETRIEVE, 'action has the correct type');
      done();
    };

    const dispatchFetchService = (action) => {
      if (typeof action === 'function') {
        const serviceSummaryThunk = action;
        serviceSummaryThunk(serviceSummaryDispatch, getState);
      } else if (count === 0) {
        assert.equal(action.type, ACTION_TYPES.SERVICES_RETRIEVE, 'action has the correct type');
        action.promise.then((resolve) => {

          assert.deepEqual(resolve.data, serviceData, 'Expected services from websocket call');
          const [ service ] = resolve.data;
          fetchedServiceId = service.id;
          action.meta.onSuccess(resolve);
        });
        count++;
      } else if (count === 1) {
        assert.equal(action.type, ACTION_TYPES.SERVICE_SELECTED, 'action has the correct type');
        assert.equal(action.payload, fetchedServiceId, 'The first serviceId is selected, even though there is already a service stored in localStorage');
      }
    };

    const thunk = initializationCreators.getServices();
    thunk(dispatchFetchService, getState);


  });

});