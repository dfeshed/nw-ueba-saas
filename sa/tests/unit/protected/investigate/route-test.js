import { moduleFor, test } from 'ember-qunit';

moduleFor('route:protected/investigate', 'Unit | Route | protected/investigate', {
  // Specify the other units that are required for this test.
  needs: ['service:headData', 'service:accessControl', 'service:investigatePage', 'service:contextualHelp']
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
