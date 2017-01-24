import Ember from 'ember';
import ReconPagerMixin from 'recon/mixins/recon-pager';
import { module, test } from 'qunit';

const {
  Object: EmberObject
} = Ember;
const ReconPagerObject = EmberObject.extend(ReconPagerMixin);
const subject = ReconPagerObject.create();

module('Unit | Mixin | recon pager');

test('Event index computed properly', function(assert) {
  assert.expect(1);
  const dataIndex = 3;
  subject.set('dataIndex', dataIndex);
  const eventIndex = subject.get('eventIndex');
  assert.equal(eventIndex, dataIndex + 1, 'eventIndex is one more than dataIndex');
});

test('packet count computed properly', function(assert) {
  assert.expect(1);
  const packets = [ {}, {}, {} ];
  subject.set('packets', packets);
  const packetCount = subject.get('packetCount');
  assert.equal(packetCount, packets.length, 'packetCount equals packet length');
});

test('packet total computed properly', function(assert) {
  assert.expect(2);
  const count = 10;
  const eventMeta = [
    ['id', 123],
    ['packets', count],
    ['foo', 'bar']
  ];
  let packetTotal = subject.get('packetTotal');
  assert.equal(packetTotal, 'unknown', 'packetTotal is unknown');
  subject.set('eventMeta', eventMeta);
  packetTotal = subject.get('packetTotal');
  assert.equal(packetTotal, count, 'packetTotal was reported');
});
