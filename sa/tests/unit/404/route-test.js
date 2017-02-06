import { moduleFor, test } from 'ember-qunit';

moduleFor('route:404', 'Unit | Route | 404', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']
  needs: ['service:assetLoader']
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
