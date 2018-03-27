import { typeOf } from '@ember/utils';
import { Socket } from 'streaming-data/services/data-access';
import { test } from 'ember-qunit';
import { module } from 'qunit';

module('Unit | Util | socket');

test('it accepts subscribers of type object', function(assert) {
  assert.expect(1);
  const stream = Socket._findSocketConfig('test', 'promise/_1');
  assert.notEqual(typeOf(stream), 'undefined', 'Socket configuration undefined');
});

test('it throws an error if socket config is not provided', function(assert) {
  assert.expect(2);
  try {
    Socket._findSocketConfig('dummy', 'stream');
    assert.ok(false, '_findSocketConfig should have thrown error');
  } catch (err) {
    assert.ok(err, 'Stream could not be instantiated.');
    assert.equal(err.message, 'Invalid socket stream configuration:. model: dummy, method: stream');
  }
});

test('the socket client\'s subscribe method returns a promise that is resolved once the subscription receipt message is handled', async function(assert) {
  assert.expect(3);

  const methodName = 'promise/_1';
  const modelName = 'test';
  const subscriptionDestination = `/test/subscription/${methodName}`;

  const client = await Socket.createStream(methodName, modelName, {}).fetchSocketClient();
  const subscription = await client.subscribe(subscriptionDestination, () => {});
  assert.ok(subscription.id, 'The subscription has an ID (e.g, sub-0)');
  assert.equal(subscription.destination, subscriptionDestination, 'The subscription destination matches the one used in the subscribe call');
  assert.ok(typeof subscription.send === 'function', 'The subscription has a send function');
});