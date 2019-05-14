import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { getNetworkDownloadOptions, getDefaultDownloadFormat } from 'recon/reducers/packets/selectors';

import summaryDataInput from '../../../data/subscriptions/reconstruction-summary/query/data';

module('Unit | selector | packets');

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

