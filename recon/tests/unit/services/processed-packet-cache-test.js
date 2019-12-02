import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

const cacheLength = (cache) => Object.keys(cache).length;

module('Unit | Util | Processed Packet Cache Service', function(hooks) {
  setupTest(hooks);

  test('Can add a packet', function(assert) {
    const service = this.owner.lookup('service:processed-packet-cache');
    service.clear();
    assert.equal(cacheLength(service.get('cache')), 0, 'item was added');
    service.add({ id: 1 });
    assert.equal(cacheLength(service.get('cache')), 1, 'item was added');
  });

  test('Can retrieve a packet', function(assert) {
    const service = this.owner.lookup('service:processed-packet-cache');
    service.clear();
    service.add({ id: 1, foo: 'bar' });
    const item = service.retrieve(1);
    assert.equal(item.foo, 'bar', 'could retrieve the item');
  });

  test('Can clear the cache', function(assert) {
    const service = this.owner.lookup('service:processed-packet-cache');
    service.clear();
    service.add({ id: 1, foo: 'bar' });
    service.add({ id: 2, foo: 'bar' });
    assert.equal(cacheLength(service.get('cache')), 2, '2 items are in cache');
    service.clear();
    assert.equal(cacheLength(service.get('cache')), 0, '0 items are in cache');
  });
});

