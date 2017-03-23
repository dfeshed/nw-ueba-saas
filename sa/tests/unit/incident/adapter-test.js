import { moduleFor, test } from 'ember-qunit';

moduleFor('adapter:incident', 'Unit | Adapter | incident', {
  needs: ['service:request']
});

// Replace this with your real tests.
test('it exists', function(assert) {
  const adapter = this.subject();
  assert.ok(adapter);
});
