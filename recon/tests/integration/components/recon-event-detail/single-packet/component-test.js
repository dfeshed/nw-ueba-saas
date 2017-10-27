import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import startApp from '../../../../helpers/start-app';

import { enhancePackets } from 'recon/reducers/packets/util';
import { renderedPackets } from 'recon/reducers/packets/selectors';
import DataHelper from '../../../../helpers/data-helper';

const packetFields = [
  { length: 6, name: 'eth.dst', position: 0 },
  { length: 6, name: 'eth.src', position: 6 },
  { length: 2, name: 'eth.type', position: 12 },
  { length: 4, name: 'ip.src', position: 26 },
  { length: 4, name: 'ip.dst', position: 30 },
  { length: 1, name: 'ip.proto', position: 23 },
  { length: 2, name: 'tcp.srcport', position: 34 },
  { length: 2, name: 'tcp.dstport', position: 36 }
];

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

moduleForComponent('recon-event-detail/single-packet', 'Integration | Component | recon event detail / single packet', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
    const application = startApp();
    initialize(application);
  }
});

test('single packet renders default', function(assert) {
  assert.expect(3);

  const processedPackets = renderedPackets({
    visuals: {
      isRequestShown: true,
      isResponseShown: true,
      isPayloadOnly: false
    },
    packets: {
      packetFields,
      packets: enhancePackets([packets[0]], 0, packetFields, 1),
      renderIds: [packets[0].id]
    }
  });

  this.set('packetFields', packetFields);
  this.set('index', 2);
  const [ packet ] = processedPackets;
  packet.isContinuation = false;
  this.set('packet', packet);

  // forcing viewportEntered becaues phantom gets confused with spaniel
  this.render(hbs`{{recon-event-detail/single-packet
    index=index
    packet=packet
    packetFields=packetFields
    viewportEntered=true
  }}`);

  return wait().then(() => {
    assert.ok(this.$('.rsa-icon-arrow-circle-left-2-filled').length === 1, 'Response arrow shown');
    assert.ok(this.$('.rsa-packet.is-continuation').length === 0, 'Response is not marked as a continuation of the previous');
    assert.equal(this.$().text().trim().replace(/\s/g, '').substring(0, 200),
      'responsepacket2InvaliddateID4804965123532SEQ102357698PAYLOAD0bytes000000000016000032000048000064f0f755ed59bfa44c11ef6201080045000034dc5c00003e06642136fbf8bb8945834a0050d7a90619dac26ff602e6801216d0f053');
  });
});

test('single packet renders with hidden header/footer bytes', function(assert) {
  assert.expect(4);

  const processedPackets = renderedPackets({
    visuals: {
      isRequestShown: true,
      isResponseShown: true,
      isPayloadOnly: true
    },
    packets: {
      packetFields,
      packets: enhancePackets([packets[1]], packetFields),
      renderIds: [packets[1].id]
    }
  });

  // Toggle the isPayloadOnly redux property
  new DataHelper(this.get('redux')).togglePayloadOnly();

  this.set('packetFields', packetFields);
  this.set('index', 4);
  const [ packet ] = processedPackets;
  packet.isContinuation = false;
  this.set('packet', packet);

  // forcing viewportEntered becaues phantom gets confused with spaniel
  this.render(hbs`{{recon-event-detail/single-packet
    index=index
    packet=packet
    packetFields=packetFields
    viewportEntered=true
  }}`);

  return wait().then(() => {
    assert.ok(this.$('.rsa-icon-arrow-circle-right-2-filled').length === 1, 'Request arrow shown');
    assert.ok(this.$('.rsa-packet.is-continuation').length === 0, 'Request is not marked as a continuation of the previous');
    assert.ok(this.$('.packet-details').length === 0, 'Packet details are not shown');
    assert.equal(this.$().text().trim().replace(/\s/g, '').substring(0, 200), 'request000000000016000032000048000064000080000096000112000128000144000160000176000192000208000224000240000256000272000288000304000320000336000352000368a44c11ef6201f0f755ed59bf08004500044f3c1640007e068');
  });
});

test('single (continuous) packet renders with hidden header/footer bytes', function(assert) {
  assert.expect(4);

  const processedPackets = renderedPackets({
    visuals: {
      isRequestShown: true,
      isResponseShown: true,
      isPayloadOnly: true
    },
    packets: {
      packetFields,
      packets: enhancePackets([packets[1]], packetFields),
      renderIds: [packets[1].id]
    }
  });

  // Toggle the isPayloadOnly redux property
  new DataHelper(this.get('redux')).togglePayloadOnly();

  this.set('packetFields', packetFields);
  this.set('index', 4);
  const [ packet ] = processedPackets;
  packet.isContinuation = true;
  this.set('packet', packet);

  // forcing viewportEntered becaues phantom gets confused with spaniel
  this.render(hbs`{{recon-event-detail/single-packet
    index=index
    packet=packet
    packetFields=packetFields
    viewportEntered=true
  }}`);

  return wait().then(() => {
    assert.ok(this.$('.rsa-icon-arrow-circle-right-2-filled').length === 1, 'Request arrow shown');
    assert.ok(this.$('.rsa-packet.is-continuation').length === 1, 'Request is marked as a continuation of the previous');
    assert.ok(this.$('.packet-details').length === 0, 'Packet details are not shown');
    assert.equal(this.$().text().trim().replace(/\s/g, '').substring(0, 200), 'request000000000016000032000048000064000080000096000112000128000144000160000176000192000208000224000240000256000272000288000304000320000336000352000368a44c11ef6201f0f755ed59bf08004500044f3c1640007e068');
  });
});
