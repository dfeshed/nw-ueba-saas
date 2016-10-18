import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
const { Object: EmberObject } = Ember;

moduleForComponent('recon-event-detail/single-packet', 'Integration | Component | recon event detail / single packet', {
  integration: true
});

test('single packet functionality', function(assert) {
  assert.expect(2);

  this.set('index', 0);
  this.set('packet', EmberObject.create({
    bytes: 'EA1/dcTIcFaBmpTdCABFAAA0vV5AAEAGUIjAqDoGMhwAE/+qAFDrQBzdIbTfFIAQEABsVwAAAQEICjLkGBsFvHPr',
    id: 575575,
    payloadSize: 0,
    sequence: 3946847453,
    side: 'request',
    timestamp: '1449631503741'
  }));
  this.set('packetFields', [
    {
      'length': 6,
      'name': 'eth.dst',
      'position': 0
    },
    {
      'length': 6,
      'name': 'eth.src',
      'position': 6
    },
    {
      'length': 2,
      'name': 'eth.type',
      'position': 12
    },
    {
      'length': 4,
      'name': 'ip.src',
      'position': 26
    },
    {
      'length': 4,
      'name': 'ip.dst',
      'position': 30
    },
    {
      'length': 1,
      'name': 'ip.proto',
      'position': 23
    },
    {
      'length': 2,
      'name': 'tcp.srcport',
      'position': 34
    },
    {
      'length': 2,
      'name': 'tcp.dstport',
      'position': 36
    }
  ]);

  this.render(hbs`{{recon-event-detail/single-packet
    index=index
    packet=packet
    packetFields=packetFields
  }}`);

  const done = assert.async();

  setTimeout(() => {
    assert.ok(this.$('.rsa-icon-arrow-circle-right-2').length === 1, 'Request arrow shown');
    assert.equal(this.$().text().trim().replace(/\s/g, '').substring(0, 200),
      'requestpacket1InvaliddateID575575SEQ39468474530bytes000000000010000020000030000040100d7f75c4c87056819a94dd080045000034bd5e400040065088c0a83a06321c0013ffaa0050eb401cdd21b4df14801010006c5700000101080a32');
    done();
  }, 1000);
});
