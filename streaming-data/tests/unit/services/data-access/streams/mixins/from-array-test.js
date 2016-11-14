import { Stream } from 'streaming-data/services/data-access';
import { module, test } from 'qunit';

module('Unit | Utility | stream/mixins/from array');

const SOURCE = [0, 1, 2];

test('it notifies subscribers of the source array\'s existing contents', function(assert) {
  assert.expect(SOURCE.length);

  const stream = Stream.create().fromArray(SOURCE);
  stream.subscribe({
    onNext: (val) => {
      assert.equal(typeof val, 'number', 'Subscriber was notified with an expected value data type.');
    }
  });
  stream.start();
});

test('it supports autoStart', function(assert) {
  assert.expect(SOURCE.length);

  Stream.create().fromArray(SOURCE)
    .autoStart()
    .subscribe({
      onNext: (val) => {
        assert.equal(typeof val, 'number', 'Subscriber was notified with an expected value data type.');
      }
    });
});

test('it notifies subscribers of additions to the source array', function(assert) {
  const arr = [].concat(SOURCE);
  assert.expect(arr.length + 1);

  Stream.create().fromArray(arr)
    .autoStart()
    .subscribe({
      onNext: (val) => {
        assert.equal(typeof val, 'number', 'Subscriber was notified with an expected value data type.');
      }
    });

  arr.pushObject(3);
});

