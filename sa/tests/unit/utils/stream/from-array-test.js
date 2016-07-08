import Stream from '../../../../utils/stream/base';
import { module, test } from 'qunit';

module('Unit | Utility | stream/from array');

const SOURCE = [0, 1, 2];

test('it notifies subscribers of the source array\'s existing contents', function(assert) {
  assert.expect(SOURCE.length);

  let stream = Stream.create().fromArray(SOURCE);
  stream.subscribe(function(val) {
    assert.equal(typeof val, 'number', 'Subscriber was notified with an expected value data type.');
  });
  stream.start();
});

test('it supports autoStart', function(assert) {
  assert.expect(SOURCE.length);

  Stream.create().fromArray(SOURCE)
    .autoStart()
    .subscribe(function(val) {
      assert.equal(typeof val, 'number', 'Subscriber was notified with an expected value data type.');
    });
});

test('it notifies subscribers of additions to the source array', function(assert) {
  let arr = [].concat(SOURCE);
  assert.expect(arr.length + 1);

  Stream.create().fromArray(arr)
    .autoStart()
    .subscribe(function(val) {
      assert.equal(typeof val, 'number', 'Subscriber was notified with an expected value data type.');
    });

  arr.pushObject(3);
});

