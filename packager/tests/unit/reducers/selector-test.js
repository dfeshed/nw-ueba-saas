import { module, test } from 'qunit';

import { listOfServices,
  defaultDriverServiceName,
  defaultDriverDisplayName,
  defaultDriverDescription } from 'packager/reducers/selectors';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | selectors');

test('get the list of devices', function(assert) {
  const state = Immutable.from({
    packager: {
      devices: [{
        'id': 'id1',
        'name': 'log collector',
        'displayName': 'NWAPPLIANCE27455 - Log Collector',
        'host': '10.10.10.10',
        'port': 1234,
        'useTls': false,
        'version': null,
        'family': null,
        'meta': {}
      }]
    }
  });

  const services = listOfServices(state);
  assert.equal(services.length, 1);
});

test('get the defaultDriver values', function(assert) {
  assert.expect(3);
  const state = Immutable.from({
    packager: {
      defaultPackagerConfig: {
        packageConfig: {
          driverServiceName: 'NWEDriver',
          driverDescription: 'RSA NWE Driver Description',
          driverDisplayName: 'RSA NWE Driver'
        }
      }
    }
  });

  const driverServiceName = defaultDriverServiceName(state);
  const driverDisplayName = defaultDriverDisplayName(state);
  const driverDescription = defaultDriverDescription(state);
  assert.equal(driverDisplayName, 'RSA NWE Driver');
  assert.equal(driverServiceName, 'NWEDriver');
  assert.equal(driverDescription, 'RSA NWE Driver Description');
});

test('check for packageConfig is undefined', function(assert) {
  assert.expect(3);
  const state = Immutable.from({
    packager: {
      defaultPackagerConfig: {}
    }
  });

  const driverServiceName = defaultDriverServiceName(state);
  const driverDisplayName = defaultDriverDisplayName(state);
  const driverDescription = defaultDriverDescription(state);
  assert.deepEqual(driverDisplayName, null);
  assert.deepEqual(driverServiceName, null);
  assert.deepEqual(driverDescription, null);
});
