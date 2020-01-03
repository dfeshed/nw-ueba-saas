import { moduleFor, test } from 'ember-qunit';

moduleFor('route:protected/monitor', 'Unit | Route | protected/monitor', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']
  needs: ['service:assetLoader', 'service:headData']
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
