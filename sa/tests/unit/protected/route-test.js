import { moduleFor, test } from 'ember-qunit';

moduleFor('route:protected', 'Unit | Route | protected', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']
  needs: [
    'service:accessControl', 'service:assetLoader', 'service:dateFormat', 'service:headData',
    'service:i18n', 'service:landingPage', 'service:session', 'service:timeFormat',
    'service:timezone', 'service:userActivity', 'service:userIdle', 'service:eventBus'
  ]
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
