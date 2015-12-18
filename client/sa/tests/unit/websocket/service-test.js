import { moduleFor, test } from 'ember-qunit';

moduleFor('service:websocket', 'Unit | Service | websocket', {
});

test('it exists', function(assert) {
  assert.expect(1);

  // Since our service's methods talk to a server, we move most of our testing into acceptance tests, which
  // can start up the entire app and therefore leverage mirage's mock server.
  let service = this.subject();
  assert.ok(service, 'Service not defined.');
});
