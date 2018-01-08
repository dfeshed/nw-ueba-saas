import { module, test } from 'qunit';

import { listOfServices } from 'packager/reducers/selectors';
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
