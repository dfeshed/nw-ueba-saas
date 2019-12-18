import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';

import {
  getNetworkDownloadOptions,
  getDefaultDownloadFormat,
  _packetsRetrieved,
  packetRenderingUnderWay
} from 'recon/reducers/packets/selectors';

import summaryDataInput from '../../../data/subscriptions/reconstruction-summary/query/data';

const packets = [{
  bytes: '8PdV7Vm/pEwR72IBCABFAAA03FwAAD4GZCE2+/i7iUWDSgBQ16kGGdrCb/YC5oASFtDwUwAAAgQFmAEBBAIBAwMI',
  id: 4804965123532,
  payloadSize: 0,
  position: 2,
  sequence: 102357698,
  side: 'response',
  timestamp: '1485792552869'
}, {
  bytes: 'pEwR72IB8PdV7Vm/CABFAARPPBZAAH4GgEyJRYNKNvv4u9epAFBv9gLmBhnaw1AYAQAi5wAAR0VUIC9zdGF0cy5waHA/ZXY9c2l0ZTpwbGF5ZXI6bXVzaWNfcXVhbGl0eToxMjhrYnBzJnNvbmdpZD1Fc0FLcGJXSiZfdD0xNDg1NzkyNTUyODE5JmN0PTE5ODIzMjY0MjEgSFRUUC8xLjENCkhvc3Q6IHd3dy5zYWF2bi5jb20NCkNvbm5lY3Rpb246IGtlZXAtYWxpdmUNCkFjY2VwdDogKi8qDQpYLVJlcXVlc3RlZC1XaXRoOiBYTUxIdHRwUmVxdWVzdA0KVXNlci1BZ2VudDogTW96aWxsYS81LjAgKFdpbmRvd3MgTlQgMTAuMDsgV2luNjQ7IHg2NCkgQXBwbGVXZWJLaXQvNTM3LjM2IChLSFRNTCwgbGlrZSBHZWNrbykgQ2hyb21lLzU1LjAuMjg4My44NyBTYWZhcmkvNTM3LjM2',
  id: 4804965123547,
  payloadSize: 1061,
  position: 4,
  sequence: 1878393574,
  side: 'request',
  timestamp: '1485792552870'
}];

module('Unit | selector | packets', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    const cache = this.owner.lookup('service:processed-packet-cache');
    cache.clear();
  });

  test('getNetworkDownloadOptions', function(assert) {
    const result = getNetworkDownloadOptions(Immutable.from({
      header: {
        headerItems: summaryDataInput.withPayloads.summaryAttributes
      },
      packets: {
        packets
      }
    }));
    assert.ok(result[0].isEnabled, 'PCAP download is enabled');
    assert.notOk(result[3].isEnabled, 'Response payload download is not enabled');
  });

  test('getDefaultDownloadOption', function(assert) {
    const option = {
      key: 'PAYLOAD',
      value: 'downloadPayload',
      isEnabled: true
    };

    const result = getDefaultDownloadFormat(Immutable.from({
      visuals: {
        defaultPacketFormat: 'PAYLOAD'
      },
      header: {
        headerItems: summaryDataInput.withPayloads.summaryAttributes
      },
      packets: {
        packets
      }
    }));
    assert.deepEqual(result, option);
  });

  test('getDefaultDownloadOption when headerItems are not loaded', function(assert) {
    const result = getDefaultDownloadFormat(Immutable.from({
      visuals: {
        defaultPacketFormat: 'PAYLOAD'
      },
      header: {
        headerItems: []
      },
      packets: {
      }
    }));
    assert.equal(typeof result.isEnabled, 'undefined');
  });

  test('_packetsRetrieved returns true when packets are not null', function(assert) {
    const result = _packetsRetrieved(Immutable.from({
      packets: {
        packets: []
      }
    }));
    assert.ok(result, '_packetsRetrieved should be true');
  });

  test('_packetsRetrieved returns false when packets are null', function(assert) {
    const result = _packetsRetrieved(Immutable.from({
      packets: {
        packets: null
      }
    }));
    assert.notOk(result, '_packetsRetrieved should be false');
  });

  test('packetRenderingUnderWay returns true if packets are null', function(assert) {
    const result = packetRenderingUnderWay(Immutable.from({
      visuals: {
        defaultPacketFormat: 'PAYLOAD'
      },
      packets: {
        packets: null,
        packetFields: []
      }
    }));
    assert.ok(result, 'packetRenderingUnderWay should be true');
  });

  test('packetRenderingUnderWay returns true if packetFields are null', function(assert) {
    const result = packetRenderingUnderWay(Immutable.from({
      visuals: {
        defaultPacketFormat: 'PAYLOAD'
      },
      packets: {
        packets: [],
        packetFields: null
      }
    }));
    assert.ok(result, 'packetRenderingUnderWay should be true');
  });

  test('packetRenderingUnderWay returns false if packetFields and packets are not null', function(assert) {
    const result = packetRenderingUnderWay(Immutable.from({
      visuals: {
        defaultPacketFormat: 'PAYLOAD'
      },
      packets: {
        packets: [],
        packetFields: []
      }
    }));
    assert.notOk(result, 'packetRenderingUnderWay should be false');
  });
});