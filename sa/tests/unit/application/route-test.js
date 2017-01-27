import { moduleFor, test } from 'ember-qunit';

moduleFor('route:application', 'Unit | Route | application', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']
  needs: ['service:assetLoader', 'service:session', 'service:userIdle']
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
