import { module, test } from 'qunit';
import {
  isCoreServiceNotUpdated,
  getCoreDeviceVersion
} from 'investigate-events/reducers/investigate/services/selectors';

module('Unit | Selectors | services');

test('determine correct core version is returned', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [
          { id: 'id1', displayName: 'Service Name', name: 'SN', version: '10.6.5.0-7206.5.21dd2e7' }
        ]
      },
      queryNode: {
        serviceId: 'sd1'
      }
    }
  };

  const coreVersion = getCoreDeviceVersion(state);
  assert.equal(coreVersion, '10.6.5.0-7206.5.21dd2e7', 'Correct core version');
});

test('when core version is not set', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [
          {}
        ]
      },
      queryNode: {
        serviceId: 'sd1'
      }
    }
  };
  const coreVersion = getCoreDeviceVersion(state);
  assert.equal(coreVersion, null, 'Core version is not set');
});

test('determine if core services are not updated', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [
          { id: 'id1', displayName: 'Service Name', name: 'SN', version: '10.6.5.0-7206.5.21dd2e7' }
        ]
      },
      queryNode: {
        serviceId: 'sd1'
      }
    }
  };

  const appVersion = '11.1.0.0';
  const flag = isCoreServiceNotUpdated(state, appVersion);

  assert.ok(flag, 'Core Service is not updated');
});

test('determine if core services are updated', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [
          { id: 'id1', displayName: 'Service Name', name: 'SN', version: '11.1.0.0-7206.5.21dd2e7' }
        ]
      },
      queryNode: {
        serviceId: 'sd1'
      }
    }
  };
  const appVersion = '11.1.0.0';
  const flag = isCoreServiceNotUpdated(state, appVersion);
  assert.notOk(flag, 'Core Service is up to date');
});