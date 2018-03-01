import EmberObject from '@ember/object';
import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleForComponent('recon-event-detail/single-text', 'Integration | Component | recon event detail single text', {
  integration: true,
  beforeEach() {
    this.registry.injection('component:recon-event-detail/single-text', 'i18n', 'service:i18n');
    this.inject.service('redux');
    initialize(this);
  }
});

test('packet text', function(assert) {
  this.set('index', 0);
  this.set('isLog', false);
  this.set('packet', EmberObject.create({
    'id': 574561,
    'payloadSize': 618,
    'sequence': 3946844195,
    'side': 'response',
    'text': ['Testing text'],
    'timestamp': 1449631503277
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

  this.render(hbs`{{recon-event-detail/single-text
    index=index
    isLog=isLog
    packet=packet
    shouldShowPacket=true
  }}`);

  return wait().then(() => {
    assert.ok(this.$('.rsa-icon-arrow-circle-left-2-filled').length === 1, 'Response arrow shown');
    assert.equal(this.$().text().trim().replace(/\s/g, ''), 'responseTestingtext');
  });
});

test('log text', function(assert) {
  this.set('index', 0);
  this.set('isLog', true);
  this.set('packet', EmberObject.create({
    'id': 574561,
    'payloadSize': 618,
    'sequence': 3946844195,
    'side': 1,
    'text': ['Testing log text'],
    'timestamp': 1449631503277
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

  this.render(hbs`{{recon-event-detail/single-text
    index=index
    isLog=isLog
    packet=packet
    shouldShowPacket=true
  }}`);

  return wait().then(() => {
    assert.equal(this.$().text().trim().replace(/\s/g, ''), 'RawLogTestinglogtext');
  });
});
