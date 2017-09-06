import { moduleFor, test } from 'ember-qunit';

moduleFor('route:application', 'Unit | Route | application', {
  // Specify the other units that are required for this test.
  needs: ['service:dateFormat', 'service:timeFormat', 'service:timezone']
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
