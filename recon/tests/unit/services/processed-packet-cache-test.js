import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

let service;

module('Unit | Util | Processed Packet Cache Service', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    service = this.owner.lookup('service:processed-packet-cache');
    service.clear();
  });

  hooks.after(function() {
    service.clear();
    service = null;
  });

  test('Can add a packet', function(assert) {
    assert.equal(service.count, 0, 'the cache should be empty');
    service.add({ id: 1 });
    assert.equal(service.count, 1, 'there should be one item in the cache');
  });

  test('Can retrieve a packet', function(assert) {
    service.add({ id: 1, foo: 'bar' });
    service.add({ id: 2, foo: 'baz' });
    const item = service.retrieve(0);
    assert.equal(service.count, 2, 'there should be two items in the cache');
    assert.equal(item.foo, 'bar', 'the proper item was not retrieve');
  });

  test('Can clear the cache', function(assert) {
    service.add({ id: 1, foo: 'bar' });
    service.add({ id: 2, foo: 'bar' });
    assert.equal(service.count, 2, '2 items are in cache');
    service.clear();
    assert.equal(service.count, 0, '0 items are in cache');
  });

  test('Can get all non-ignored items from the cache', function(assert) {
    service.add({ id: 1 });
    service.add({ id: 2, ignore: true });
    service.add({ id: 3 });
    assert.equal(service.count, 3, 'there should be three items in the cache');
    const items = service.retrieveAll();
    assert.equal(items.length, 2, 'there should be two items in the cache that are not ignored');
    assert.equal(items[0].id, 1, 'the first item does not have the correct id');
    assert.equal(items[1].id, 3, 'tthe second item does not have the correct id');
  });

  test('Can get the last item from the cache', function(assert) {
    service.add({ id: 1 });
    service.add({ id: 2 });
    assert.equal(service.retrieveLast().id, 2, 'the last item in the cache was not retrieved');
  });
});
