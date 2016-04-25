import { moduleFor, test } from 'ember-qunit';

moduleFor('adapter:application', 'Unit | Adapter | application', {
  // Specify the other units that are required for this test.
  // needs: ['service:store', 'service:websocket']
});

test('it exists', function(assert) {

  // Since our adapter's methods talk to a server, we move most of our testing into acceptance tests, which
  // can start up the entire app and therefore leverage mirage's mock server.
  let adapter = this.subject();
  assert.ok(adapter);
});
