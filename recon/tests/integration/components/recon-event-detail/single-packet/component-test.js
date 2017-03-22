import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import { enhancedPackets } from 'recon/selectors/packet-selectors';

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

moduleForComponent('recon-event-detail/single-packet', 'Integration | Component | recon event detail / single packet', {
  integration: true
});

test('single packet renders default', function(assert) {
  assert.expect(2);

  const packets = enhancedPackets({
    visuals: {
      isRequestShown: true,
      isResponseShown: true,
      isPayloadOnly: false
    },
    data: {
      packetFields,
      packets: [{
        bytes: atob('EA1/dcTIcFaBmpTdCABFAAA0vV5AAEAGUIjAqDoGMhwAE/+qAFDrQBzdIbTfFIAQEABsVwAAAQEICjLkGBsFvHPr').split(''),
        id: 575575,
        payloadSize: 0,
        position: 1,
        sequence: 3946847453,
        side: 'request',
        timestamp: '1449631503741'
      }]
    }
  });

  this.set('packetFields', packetFields);
  this.set('index', 0);
  this.set('packet', packets[0]);

  this.render(hbs`{{recon-event-detail/single-packet
    index=index
    packet=packet
    packetFields=packetFields
  }}`);

  return wait().then(() => {
    assert.ok(this.$('.rsa-icon-arrow-circle-right-2').length === 1, 'Request arrow shown');
    assert.equal(this.$().text().trim().replace(/\s/g, '').substring(0, 200),
      'requestpacket1InvaliddateID575575SEQ3946847453PAYLOAD0bytes000000000010000020000030000040100d7f75c4c87056819a94dd080045000034bd5e400040065088c0a83a06321c0013ffaa0050eb401cdd21b4df14801010006c570000010');
  });
});

test('single packet renders with hidden header/footer', function(assert) {
  assert.expect(2);

  const packets = enhancedPackets({
    visuals: {
      isRequestShown: true,
      isResponseShown: true,
      isPayloadOnly: true
    },
    data: {
      packetFields,
      packets: [{
        bytes: atob('EA1/dcTIcFaBmpTdCABFAAA0vV5AAEAGUIjAqDoGMhwAE/+qAFDrQBzdIbTfFIAQEABsVwAAAQEICjLkGBsFvHPr').split(''),
        id: 575575,
        payloadSize: 0,
        position: 1,
        sequence: 3946847453,
        side: 'request',
        timestamp: '1449631503741'
      }]
    }
  });

  this.set('packetFields', packetFields);
  this.set('index', 0);
  this.set('packet', packets[0]);

  this.render(hbs`{{recon-event-detail/single-packet
    index=index
    packet=packet
    packetFields=packetFields
  }}`);

  return wait().then(() => {
    assert.ok(this.$('.rsa-icon-arrow-circle-right-2').length === 1, 'Request arrow shown');
    assert.equal(this.$().text().trim().replace(/\s/g, ''), 'requestTherearenopayloadbytesforthispacket');
  });
});
