import Stream from '../../../../utils/stream/base';
import { module, test } from 'qunit';

module('Unit | Utility | stream/from socket');

test('it exists', function(assert) {
  assert.expect(1);

  // Since our util's methods talk to a server, we move most of our testing into acceptance tests, which
  // can start up the entire app and therefore leverage mirage's mock server.
  let stream = Stream.create().fromSocket();
  assert.ok(stream, 'Stream could not be instantiated.');
});
