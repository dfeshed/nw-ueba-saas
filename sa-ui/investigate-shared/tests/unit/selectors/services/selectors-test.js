import { module, test } from 'qunit';
import {
  getCoreDeviceVersion,
  hasSummaryData,
  isCoreServiceNotUpdated
} from 'investigate-shared/selectors/services/selectors';

module('Unit | Selectors | services');

test('determine correct core version is returned', function(assert) {
  const state = {
    services: {
      serviceData: [
        { id: 'id1', displayName: 'Service Name', name: 'SN', version: '10.6.5.0-7206.5.21dd2e7' }
      ]
    },
    queryNode: {
      serviceId: 'sd1'
    }
  };

  const coreVersion = getCoreDeviceVersion(state);
  assert.equal(coreVersion, '10.6.5.0-7206.5.21dd2e7', 'Correct core version');
});

test('when core version is not set', function(assert) {
  const state = {

    services: {
      serviceData: [
        {}
      ]
    },
    queryNode: {
      serviceId: 'sd1'
    }
  };
  const coreVersion = getCoreDeviceVersion(state);
  assert.equal(coreVersion, null, 'Core version is not set');
});

test('determine if core services are not updated with 10.x', function(assert) {
  const state = {
    services: {
      serviceData: [
        { id: 'id1', displayName: 'Service Name', name: 'SN', version: '10.6.5.0-7206.5.21dd2e7' }
      ]
    },
    queryNode: {
      serviceId: 'sd1'
    }
  };

  const flag = isCoreServiceNotUpdated(state, '11.1');

  assert.ok(flag, 'Core Service is not updated');
});

test('determine if core services are not updated with 11.x', function(assert) {
  const state = {
    services: {
      serviceData: [
        { id: 'id1', displayName: 'Service Name', name: 'SN', version: '11.1.0.0-7206.5.21dd2e7' }
      ]
    },
    queryNode: {
      serviceId: 'sd1'
    }
  };

  const flag = isCoreServiceNotUpdated(state, '11.2');

  assert.ok(flag, 'Core Service is not updated');
});

test('determine if core services are updated with 11.1', function(assert) {
  const state = {
    services: {
      serviceData: [
        { id: 'id1', displayName: 'Service Name', name: 'SN', version: '11.1.0.0-7206.5.21dd2e7' }
      ]
    },
    queryNode: {
      serviceId: 'sd1'
    }
  };
  const flag = isCoreServiceNotUpdated(state, '11.1');
  assert.notOk(flag, 'Core Service is up to date');
});

test('determine if core services are updated beyond 11.1', function(assert) {
  const state = {
    services: {
      serviceData: [
        { id: 'id1', displayName: 'Service Name', name: 'SN', version: '11.3.0.0-7206.5.21dd2e7' }
      ]
    },
    queryNode: {
      serviceId: 'sd1'
    }
  };
  const flag = isCoreServiceNotUpdated(state, '11.1');
  assert.notOk(flag, 'Core Service is up to date');
});

test('hasSummaryData', function(assert) {
  const state1 = {
    services: {
      summaryData: { startTime: 0 },
      isSummaryRetrieveData: true
    }
  };
  const result1 = hasSummaryData(state1);
  assert.equal(result1, false);

  const state2 = {
    services: {
      isSummaryRetrieveData: true
    }
  };
  const result2 = hasSummaryData(state2);
  assert.equal(result2, true);

  const state3 = {
    services: {
      summaryData: { startTime: 123 }
    }
  };
  const result3 = hasSummaryData(state3);
  assert.equal(result3, true);
});
