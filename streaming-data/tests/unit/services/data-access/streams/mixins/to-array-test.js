import { Stream } from 'streaming-data/services/data-access';
import { module, test } from 'qunit';

module('Unit | Utility | stream/mixins/to array');

test('it pushes payloads to the target array', function(assert) {
  const stream = Stream.create().autoStart();
  const target = [];

  stream.toArray(target);
  stream.next('foo');
  stream.next({ bar: 'baz' });
  stream.next(3);

  assert.equal(target.length, 3, 'Target array has expected size.');
  assert.equal(target[0], 'foo', 'Payload was pushed to array as expected');
  assert.equal(target[1].bar, 'baz', 'Payload was pushed to array as expected');
  assert.equal(target[2], 3, 'Payload was pushed to array as expected');
});

test('it copies the expected properties to the target array', function(assert) {
  const PROPS = {
    count: 1,
    goal: 2,
    progress: 0,
    total: 2,
    errorCode: 0,
    page: { foo: 'bar' }
  };

  const propNames = Object.keys(PROPS);
  const propCount = propNames.length;
  const stream = Stream.create(PROPS).autoStart();
  const target = [];

  stream.toArray(target);
  stream.next(0);

  assert.expect(propCount);
  propNames.forEach((name) => {
    assert.equal(PROPS[name], target.get(name), 'Property was copied onto target array correctly.');
  });
});

