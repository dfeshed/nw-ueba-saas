import Ember from 'ember';
import { Socket } from 'streaming-data/services/data-access';
import { test } from 'ember-qunit';
import { module } from 'qunit';

const { typeOf } = Ember;

module('Unit | Util | socket');

test('it accepts subscribers of type object', function(assert) {
  assert.expect(1);
  let stream = Socket._findSocketConfig('test', 'stream');
  assert.notEqual(typeOf(stream), 'undefined', 'Socket configuration undefined');
});

test('it throws an error if socket config is not provided', function(assert) {
  assert.expect(2);
  try {
    Socket._findSocketConfig('dummy', 'stream');
    assert.ok(false, 'fromSocket should have thrown error');
  } catch (err) {
    assert.ok(err, 'Stream could not be instantiated.');
    assert.equal(err.message, 'Invalid socket stream configuration:. model: dummy, method: stream');
  }
});

