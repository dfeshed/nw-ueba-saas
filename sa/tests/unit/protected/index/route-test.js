import { moduleFor, test } from 'ember-qunit';

moduleFor('route:protected/index', 'Unit | Route | protected/index', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']
  needs: ['service:assetLoader', 'service:headData']
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
