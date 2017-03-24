import { Stream } from 'streaming-data/services/data-access';
import { module, test } from 'qunit';

module('Unit | Utility | stream/mixins/from socket');

test('it exists', function(assert) {
  assert.expect(1);

  const socketConfig = {
    socketUrl: 'socketUrl',
    subscriptionDestination: 'subscriptionDestination',
    requestDestination: 'requestDestination'
  };

  const stream = Stream.create().fromSocket({ socketConfig });
  assert.ok(stream, 'Stream could not be instantiated.');
});