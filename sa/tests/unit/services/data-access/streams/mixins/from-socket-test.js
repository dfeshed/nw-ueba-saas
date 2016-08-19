import { Stream } from 'sa/services/data-access';
import { module, test } from 'qunit';

module('Unit | Utility | stream/mixins/from socket');

test('it exists', function(assert) {
  assert.expect(1);

  const socketConfig = {
    socketUrl: 'socketUrl',
    subscriptionDestination: 'subscriptionDestination',
    requestDestination: 'requestDestination'
  };

  // Since our util's methods talk to a server, we move most of our testing into acceptance tests, which
  // can start up the entire app and therefore leverage mirage's mock server.
  let stream = Stream.create().fromSocket({ socketConfig });
  assert.ok(stream, 'Stream could not be instantiated.');
});