import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';

import { processPacketPayloads } from 'recon/reducers/packets/util';

module('Unit | selector | packets util', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    const cache = this.owner.lookup('service:processed-packet-cache');
    cache.clear();
  });

  test('processPacketPayloads will return an empty array when there are no bytes', function(assert) {
    const packets = Immutable.from([{
      bytes: '',
      id: 4804965123532,
      payloadSize: 6,
      position: 2,
      sequence: 102357698,
      side: 'response',
      timestamp: '1485792552869'
    }]);
    const result = processPacketPayloads(packets, true);
    assert.equal(result.length, 0, 'Did not find an empty array');
  });

  test('processPacketPayloads will return undefined if no packets or packetFields', function(assert) {
    // no packetFields
    const packets = Immutable.from([{
      bytes: 'ABCD',
      id: 4804965123532,
      payloadSize: 6,
      position: 2,
      sequence: 102357698,
      side: 'response',
      timestamp: '1485792552869'
    }]);
    let result = processPacketPayloads(packets, true, null);
    assert.notOk(result, 'Did not find undefined');

    // no packets
    result = processPacketPayloads(null, true, []);
    assert.notOk(result, 'Did not find undefined');
  });
});