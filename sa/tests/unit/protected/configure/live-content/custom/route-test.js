import { moduleFor, test } from 'ember-qunit';

moduleFor('route:protected/configure/live-content/custom', 'Unit | Route | protected/configure/live-content/custom', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
