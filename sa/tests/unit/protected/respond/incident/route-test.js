import { moduleFor, test } from 'ember-qunit';

moduleFor('route:protected/responded/incident', 'Unit | Route | protected/responded/incident', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']
  needs: [
    'service:assetLoader', 'service:fatalErrors', 'service:flashMessages',
    'service:headData', 'service:i18n', 'service:layout', 'service:respondMode'
  ]
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
