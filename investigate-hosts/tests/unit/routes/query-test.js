import { moduleFor, skip } from 'ember-qunit';

moduleFor('route:query', 'Unit | Route | query', {
  // Specify the other units that are required for this test.
  needs: [
    'service:accessControl', 'service:assetLoader', 'service:dateFormat', 'service:headData',
    'service:i18n', 'service:landingPage', 'service:session', 'service:timeFormat',
    'service:timezone', 'service:userActivity', 'service:fatalErrors', 'service:flashMessages'
  ]
});

skip('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
