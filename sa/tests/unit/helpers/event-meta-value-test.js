import { eventMetaValue } from 'sa/helpers/event-meta-value';
import { module, test } from 'qunit';

module('Unit | Helper | event meta value');

test('it can retrieve a value from a meta key with a dot name', function(assert) {
  const event = {};
  const key = 'ip.src';
  const value = 'foo';
  event[key] = value;
  assert.equal(eventMetaValue([event, key]), value, 'Unexpected value from key with dot name.');
});
