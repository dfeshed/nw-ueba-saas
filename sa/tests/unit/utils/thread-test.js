import Thread from '../../../utils/thread';
import { module, test } from 'qunit';

module('Unit | Utility | thread');

test('it works', function(assert) {
  assert.expect(2);

  // Kick off a thread that pushes a queue of values into a result array.
  let queue = [0, 1, 2],
    result = [];

  Thread.create({
    queue,
    interval: 0,
    rate: 0,
    onNext(value) {
      result.push(value);
    },
    onNextBatch() {
      assert.ok(true, 'onNextBatch hook fired.');
    }
  }).start();

  assert.equal(result.length, 3, 'Unexpected result size.');

});
