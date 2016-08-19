import { moduleFor, test } from 'ember-qunit';

moduleFor('adapter:application', 'Unit | Adapter | application', {
});

test('it exists', function(assert) {

  // Since our adapter's methods talk to a server, we move most of our testing into acceptance tests, which
  // can start up the entire app and therefore leverage mirage's mock server.
  let adapter = this.subject();
  assert.ok(adapter);
});
