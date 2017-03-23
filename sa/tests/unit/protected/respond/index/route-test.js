import { moduleFor, test } from 'ember-qunit';

moduleFor('route:protected/responded/index', 'Unit | Route | protected/responded/index', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']
  needs: [
    'service:assetLoader', 'service:contextualHelp', 'service:fatalErrors', 'service:flashMessages',
    'service:headData', 'service:i18n', 'service:layout', 'service:respondMode', 'service:session'
  ]
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
